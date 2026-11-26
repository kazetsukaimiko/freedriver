#!/usr/bin/env python3
"""Contract checks for .github/workflows/release-please.yml."""

from __future__ import annotations

import re
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
WORKFLOWS = ROOT / ".github" / "workflows"
WORKFLOW = WORKFLOWS / "release-please.yml"
GATE = "python3 .github/release/release_gate.py"
RELEASE_PLEASE_ACTION = "googleapis/release-please-action"
APP_TOKEN_ACTION = "actions/create-github-app-token"
PINNED = re.compile(r"^[\w.-]+/[\w./-]+@[0-9a-f]{40}$")


def workflow() -> str:
    return WORKFLOW.read_text(encoding="utf-8")


def jobs(text: str) -> dict[str, str]:
    body = text.split("\njobs:\n", 1)[1]
    names = re.findall(r"^  ([\w-]+):$", body, re.MULTILINE)
    parts = re.split(r"^  [\w-]+:$", body, flags=re.MULTILINE)[1:]
    return dict(zip(names, parts))


def uses(text: str) -> list[str]:
    return [match.split("#")[0].strip() for match in re.findall(r"^\s*(?:- )?uses:\s*(.+)$", text, re.MULTILINE)]


def run_lines(text: str) -> list[str]:
    return re.findall(r"^\s*run:\s*(.+)$", text, re.MULTILINE)


class ReleaseWorkflowTest(unittest.TestCase):
    def test_runs_on_a_push_to_master_one_run_at_a_time(self) -> None:
        text = workflow()
        self.assertIn("on:\n  push:\n    branches: [master]\n", text)
        self.assertNotIn("pull_request", text)
        self.assertNotIn("workflow_dispatch", text)
        self.assertIn("concurrency:\n  group: release-please\n  cancel-in-progress: false\n", text)
        self.assertIn("\npermissions: {}\n", text)

    def test_the_jobs_are_the_gate_and_release_please(self) -> None:
        self.assertEqual(list(jobs(workflow())), ["release-gate", "release-please"])

    def test_release_please_needs_the_gate(self) -> None:
        job = jobs(workflow())["release-please"]
        self.assertIn("    needs: release-gate\n", job)
        self.assertNotIn("if:", job)
        self.assertNotIn("continue-on-error", workflow())

    def test_the_gate_reads_with_the_built_in_token_outside_any_environment(self) -> None:
        job = jobs(workflow())["release-gate"]
        self.assertIn("    permissions:\n      contents: read\n      pull-requests: read\n    steps:", job)
        self.assertNotIn("write", job)
        self.assertNotIn("environment:", job)
        self.assertNotIn("secrets.", job)
        self.assertIn("GITHUB_TOKEN: ${{ github.token }}", job)
        self.assertEqual(run_lines(job), [GATE])
        self.assertEqual([use.split("@")[0] for use in uses(job)], ["actions/checkout"])
        self.assertIn("persist-credentials: false", job)

    def test_only_release_please_reads_the_app_secrets_in_environment_release(self) -> None:
        job = jobs(workflow())["release-please"]
        self.assertIn("    environment: release\n", job)
        self.assertIn("    permissions: {}\n", job)
        self.assertEqual(workflow().count("environment:"), 1)
        for secret in ("RELEASE_APP_ID", "RELEASE_APP_PRIVATE_KEY"):
            with self.subTest(secret=secret):
                self.assertIn(f"secrets.{secret}", job)
                self.assertIn(secret, job.split("uses:")[0])
        for workflow_file in sorted(WORKFLOWS.glob("*.yml")):
            text = workflow_file.read_text(encoding="utf-8")
            if workflow_file != WORKFLOW:
                with self.subTest(workflow=workflow_file.name):
                    self.assertNotIn("RELEASE_APP", text)
                    self.assertNotIn("environment: release", text)

    def test_a_missing_secret_fails_naming_it_and_the_environment(self) -> None:
        job = jobs(workflow())["release-please"]
        check = job.split("uses:")[0]
        self.assertIn("for name in RELEASE_APP_ID RELEASE_APP_PRIVATE_KEY", check)
        self.assertIn("secret ${name} is missing from environment 'release'", check)
        self.assertIn('exit "${missing}"', check)

    def test_every_action_is_pinned_to_a_full_commit(self) -> None:
        for use in uses(workflow()):
            with self.subTest(use=use):
                self.assertRegex(use, PINNED)

    def test_the_token_is_the_apps_and_reaches_release_please_only(self) -> None:
        text = workflow()
        job = jobs(text)["release-please"]
        self.assertEqual(
            [use.split("@")[0] for use in uses(job)],
            [APP_TOKEN_ACTION, RELEASE_PLEASE_ACTION],
        )
        self.assertEqual(text.count("steps.app-token.outputs.token"), 1)
        self.assertIn("token: ${{ steps.app-token.outputs.token }}", job)
        self.assertNotIn("github.token", job)
        self.assertNotIn("GITHUB_TOKEN", job)
        self.assertIn("permission-contents: write", job)
        self.assertIn("permission-pull-requests: write", job)
        self.assertEqual(len(re.findall(r"permission-[\w-]+:", job)), 2)

    def test_tags_come_from_release_please_alone(self) -> None:
        # The only step that can write is release-please-action, with its own
        # view of the merged pull request. Every other step is the gate or the
        # secret check, and neither writes a tag or a release.
        text = workflow()
        job = jobs(text)["release-please"]
        self.assertEqual(len(run_lines(text)), 2)
        self.assertEqual(text.count(RELEASE_PLEASE_ACTION), 1)
        for word in ("git tag", "git push", "gh release", "gh api", "createRef", "skip-github-release", "release-as"):
            with self.subTest(word=word):
                self.assertNotIn(word, text)
        self.assertIn("config-file: release-please-config.json", job)
        self.assertIn("manifest-file: .release-please-manifest.json", job)
        self.assertNotIn("packages:", text)

    def test_only_the_gate_and_release_please_read_the_pending_set(self) -> None:
        for workflow_file in sorted(WORKFLOWS.glob("*.yml")):
            with self.subTest(workflow=workflow_file.name):
                self.assertNotIn("autorelease", workflow_file.read_text(encoding="utf-8"))
        readers = [
            path.name
            for path in sorted((ROOT / ".github" / "release").iterdir())
            if path.is_file()
            and not path.name.endswith("_test.py")
            and "autorelease: pending" in path.read_text(encoding="utf-8")
        ]
        self.assertEqual(readers, ["release_gate.py"])

    def test_no_workflow_merges_a_pull_request(self) -> None:
        for workflow_file in sorted(WORKFLOWS.glob("*.yml")):
            text = workflow_file.read_text(encoding="utf-8")
            with self.subTest(workflow=workflow_file.name):
                self.assertNotIn("pr merge", text)
                self.assertNotIn("automerge", text.lower())
                self.assertNotIn("auto-merge", text.lower())


if __name__ == "__main__":
    unittest.main()
