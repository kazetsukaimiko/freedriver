#!/usr/bin/env python3
"""Checks for publishing the release allowlist from a version tag."""

from __future__ import annotations

import os
import re
import stat
import subprocess
import tempfile
import unittest
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
SCRIPT = Path(__file__).resolve().parent / "publish_jsonlink.sh"
WORKFLOW = ROOT / ".github" / "workflows" / "publish-jsonlink.yml"
CI_WORKFLOW = ROOT / ".github" / "workflows" / "ci.yml"
RELEASE_WORKFLOW = ROOT / ".github" / "workflows" / "release.yml"
POM = ROOT / "pom.xml"
BOM = ROOT / "bom" / "pom.xml"
PUBLISHED_ARTIFACTS = ROOT / ".github" / "release" / "published-artifacts.txt"
MAVEN_POM = "http://maven.apache.org/POM/4.0.0"
POM_ARTIFACTS = {
    "freedriver-parent",
    "freedriver-bom",
    "freedriver-3p-bom",
    "freedriver-jakarta-bom",
}
PACKAGES = "https://maven.pkg.github.com/kazetsukaimiko/freedriver"
PROJECTS = [
    ".",
    "bom",
    "jakarta-bom",
    "3p-bom",
    "jsonlink",
    "math-jpa",
    "victron",
    "victron-jpa",
    "inotify-cdi",
    "metrics",
]


def project_tokens(script_text: str) -> list[str]:
    match = re.search(r"^PROJECTS='([^']+)'", script_text, re.MULTILINE)
    if match is None:
        raise AssertionError("publish script has no PROJECTS list")
    return match.group(1).split(",")


def command_tokens(line: str) -> list[str]:
    return line.split()


def pl_tokens(tokens: list[str]) -> list[str]:
    index = tokens.index("-pl")
    return tokens[index + 1].split(",")


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

    def test_publish_workflow_deploys_on_a_version_tag(self) -> None:
        text = WORKFLOW.read_text(encoding="utf-8")
        self.assertIn('tags:\n      - "*.*.*"', text)
        self.assertIn("packages: write", text)
        self.assertIn("publish_jsonlink.sh", text)
        self.assertIn("version_bump.py", text)
        self.assertIn("server-id: github", text)
        self.assertNotIn("security-review", text)
        self.assertNotIn("manual-review", text)

    def test_ci_runs_package_mode_on_pull_requests(self) -> None:
        text = CI_WORKFLOW.read_text(encoding="utf-8")
        self.assertIn("contents: read", text)
        self.assertNotIn("packages: write", text)
        self.assertIn("mvn --batch-mode --no-transfer-progress verify", text)
        self.assertIn("publish_jsonlink.sh --package", text)
        self.assertIn("maven.repo.local", text)

    def test_script_has_one_project_list(self) -> None:
        text = SCRIPT.read_text(encoding="utf-8")
        self.assertEqual(project_tokens(text), PROJECTS)
        for match in re.finditer(r"-pl\s+(\S+)", text):
            self.assertIn(match.group(1), ('"${PROJECTS}"', "${PROJECTS}"))
        self.assertIn("versions-maven-plugin:2.18.0:set", text)
        self.assertIn('-DnewVersion="${tag}"', text)
        self.assertIn("-DskipTests", text)
        self.assertIn("-DdeployAtEnd=true", text)
        self.assertNotIn("maven.test.skip", text)
        self.assertNotIn("GITHUB_TOKEN=", text)


class PublishJsonlinkScriptTest(unittest.TestCase):
    def run_script(
        self,
        args: list[str],
        dry_run: bool = False,
        path: str | None = None,
    ) -> subprocess.CompletedProcess[str]:
        env = os.environ.copy()
        if dry_run:
            env["DRY_RUN"] = "1"
        else:
            env.pop("DRY_RUN", None)
        if path is not None:
            env["PATH"] = path
        return subprocess.run(
            ["bash", str(SCRIPT), *args],
            check=False,
            capture_output=True,
            text=True,
            env=env,
        )

    def test_rejects_a_snapshot_or_prefixed_tag(self) -> None:
        for tag in ("1.0.0-SNAPSHOT", "v1.0.0", "latest", ""):
            with self.subTest(tag=tag):
                result = self.run_script([tag or " "], dry_run=True)
                self.assertNotEqual(result.returncode, 0, result.stderr)

    def test_dry_run_echoes_the_project_list_and_skips_maven(self) -> None:
        with tempfile.TemporaryDirectory() as directory:
            log = Path(directory) / "mvn.log"
            stub = self.maven_stub(Path(directory), log)
            result = self.run_script(["1.0.0"], dry_run=True, path=stub)
            self.assertEqual(result.returncode, 0, result.stderr)
            tokens = command_tokens(result.stdout.strip())
            self.assertEqual(pl_tokens(tokens), PROJECTS)
            self.assertEqual(
                pl_tokens(tokens),
                project_tokens(SCRIPT.read_text(encoding="utf-8")),
            )
            self.assertIn("-DskipTests", tokens)
            self.assertIn("-DdeployAtEnd=true", tokens)
            self.assertIn("deploy", tokens)
            self.assertEqual(log.read_text(encoding="utf-8"), "")

    def test_package_skips_the_tag_gate_and_does_not_deploy(self) -> None:
        with tempfile.TemporaryDirectory() as directory:
            log = Path(directory) / "mvn.log"
            stub = self.maven_stub(Path(directory), log)
            result = self.run_script(
                ["--package", "-Dmaven.repo.local=/tmp/empty-m2"],
                dry_run=True,
                path=stub,
            )
            self.assertEqual(result.returncode, 0, result.stderr)
            tokens = log.read_text(encoding="utf-8").split()
            self.assertEqual(pl_tokens(tokens), PROJECTS)
            self.assertIn("package", tokens)
            self.assertIn("-DskipTests", tokens)
            self.assertNotIn("deploy", tokens)
            self.assertNotIn("-DdeployAtEnd=true", tokens)
            self.assertIn("-Dmaven.repo.local=/tmp/empty-m2", tokens)
            self.assertNotIn("versions-maven-plugin:2.18.0:set", tokens)

            log.write_text("", encoding="utf-8")
            rejected = self.run_script(["--package", "1.0.0"], path=stub)
            self.assertNotEqual(rejected.returncode, 0, rejected.stderr)
            self.assertIn("no version argument", rejected.stderr)
            self.assertEqual(log.read_text(encoding="utf-8"), "")

    def maven_stub(self, directory: Path, log: Path) -> str:
        log.write_text("", encoding="utf-8")
        path = directory / "mvn"
        path.write_text(
            "#!/bin/sh\n" f"printf '%s\\n' \"$@\" >> {log}\nexit 0\n",
            encoding="utf-8",
        )
        path.chmod(path.stat().st_mode | stat.S_IEXEC)
        return f"{directory}:{os.environ.get('PATH', '')}"


def published_ids(path: Path) -> list[str]:
    lines = path.read_text(encoding="utf-8").splitlines()
    if not lines or any(line.strip() != line or line == "" for line in lines):
        raise AssertionError("published artifact ids are one id per line")
    if len(lines) != len(set(lines)):
        raise AssertionError(f"duplicate published artifact ids: {lines}")
    return lines


def managed_coordinates(path: Path) -> list[dict[str, str]]:
    root = ET.parse(path).getroot()
    if root.tag != f"{{{MAVEN_POM}}}project":
        raise AssertionError(f"unexpected pom root {root.tag}")
    namespace = {"m": MAVEN_POM}
    coordinates = []
    for dependency in root.findall(
        "m:dependencyManagement/m:dependencies/m:dependency",
        namespace,
    ):
        coordinate = {}
        for child in list(dependency):
            name = child.tag.removeprefix(f"{{{MAVEN_POM}}}")
            coordinate[name] = (child.text or "").strip()
        coordinates.append(coordinate)
    return coordinates


class PublishedAllowlistBomTest(unittest.TestCase):
    def test_published_artifact_file_is_the_release_allowlist(self) -> None:
        ids = published_ids(PUBLISHED_ARTIFACTS)
        self.assertTrue(POM_ARTIFACTS <= set(ids))

    def test_bom_manages_the_published_jars_and_base_tests(self) -> None:
        ids = published_ids(PUBLISHED_ARTIFACTS)
        jars = [artifact_id for artifact_id in ids if artifact_id not in POM_ARTIFACTS]
        coordinates = managed_coordinates(BOM)
        jars_seen: list[str] = []
        test_jars: list[str] = []
        for coordinate in coordinates:
            self.assertEqual(coordinate.get("groupId"), "io.freedriver", coordinate)
            self.assertEqual(coordinate.get("version"), "${project.version}", coordinate)
            artifact_id = coordinate.get("artifactId", "")
            declared_type = coordinate.get("type", "jar")
            classifier = coordinate.get("classifier")
            if declared_type == "test-jar":
                self.assertEqual(artifact_id, "base", coordinate)
                self.assertEqual(classifier, "tests", coordinate)
                test_jars.append(artifact_id)
                continue
            self.assertEqual(declared_type, "jar", coordinate)
            self.assertNotIn("classifier", coordinate, coordinate)
            self.assertNotIn(artifact_id, POM_ARTIFACTS, coordinate)
            jars_seen.append(artifact_id)
        self.assertCountEqual(jars_seen, jars)
        self.assertEqual(test_jars, ["base"])
        self.assertEqual(len(jars_seen), len(set(jars_seen)))


if __name__ == "__main__":
    unittest.main()
