#!/usr/bin/env python3
"""Checks for publishing jsonlink jars from a version tag."""

from __future__ import annotations

import os
import subprocess
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
SCRIPT = Path(__file__).resolve().parent / "publish_jsonlink.sh"
WORKFLOW = ROOT / ".github" / "workflows" / "publish-jsonlink.yml"
RELEASE_WORKFLOW = ROOT / ".github" / "workflows" / "release.yml"
POM = ROOT / "pom.xml"
PACKAGES = "https://maven.pkg.github.com/kazetsukaimiko/freedriver"


class PublishJsonlinkConfigTest(unittest.TestCase):
    def test_packages_url_is_this_repository(self) -> None:
        pom = POM.read_text(encoding="utf-8")
        self.assertIn("<distributionManagement>", pom)
        self.assertIn(PACKAGES, pom)
        self.assertIn("<id>github</id>", pom)
        self.assertNotIn("SNAPSHOT", pom[pom.index("<distributionManagement>") :])

    def test_release_notes_workflow_stays_notes(self) -> None:
        text = RELEASE_WORKFLOW.read_text(encoding="utf-8")
        self.assertIn("Publish the GitHub release", text)
        self.assertIn("git-cliff", text)
        self.assertNotIn("deploy", text)
        self.assertNotIn("packages: write", text)

    def test_publish_workflow_deploys_jsonlink_on_a_version_tag(self) -> None:
        text = WORKFLOW.read_text(encoding="utf-8")
        self.assertIn('tags:\n      - "*.*.*"', text)
        self.assertIn("packages: write", text)
        self.assertIn("publish_jsonlink.sh", text)
        self.assertIn("version_bump.py", text)
        self.assertIn("server-id: github", text)
        self.assertNotIn("security-review", text)
        self.assertNotIn("manual-review", text)

    def test_script_deploys_jsonlink_at_the_tag_version(self) -> None:
        text = SCRIPT.read_text(encoding="utf-8")
        self.assertIn("-pl jsonlink", text)
        self.assertIn("-am", text)
        self.assertIn("deploy", text)
        self.assertIn("versions-maven-plugin:2.18.0:set", text)
        self.assertIn("-DnewVersion=\"${tag}\"", text)
        self.assertIn("-Dmaven.test.skip=true", text)
        self.assertNotIn("GITHUB_TOKEN=", text)


class PublishJsonlinkScriptTest(unittest.TestCase):
    def run_script(self, tag: str, dry_run: bool = True) -> subprocess.CompletedProcess[str]:
        env = {"DRY_RUN": "1"} if dry_run else {}
        return subprocess.run(
            ["bash", str(SCRIPT), tag],
            check=False,
            capture_output=True,
            text=True,
            env={**os.environ, **env},
        )

    def test_rejects_a_snapshot_or_prefixed_tag(self) -> None:
        for tag in ("1.0.0-SNAPSHOT", "v1.0.0", "latest", ""):
            with self.subTest(tag=tag):
                result = self.run_script(tag or " ")
                self.assertNotEqual(result.returncode, 0, result.stderr)

    def test_dry_run_accepts_the_maven_release_version(self) -> None:
        result = self.run_script("1.0.0")
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertIn("1.0.0", result.stdout)
        self.assertIn("-pl jsonlink -am", result.stdout)


if __name__ == "__main__":
    unittest.main()
