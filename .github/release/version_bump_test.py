#!/usr/bin/env python3
"""Tests for the release tag rules."""

from __future__ import annotations

import subprocess
import tempfile
import textwrap
import unittest
from pathlib import Path

from version_bump import (
    bump_version,
    check_tag,
    classify,
    expected_tag,
    maven_release_version,
)


def write_pom(directory: Path, version: str) -> None:
    directory.joinpath("pom.xml").write_text(
        textwrap.dedent(
            f"""\
            <project xmlns="http://maven.apache.org/POM/4.0.0">
              <modelVersion>4.0.0</modelVersion>
              <groupId>io.freedriver</groupId>
              <artifactId>example</artifactId>
              <version>{version}</version>
              <dependencies>
                <dependency>
                  <groupId>org.example</groupId>
                  <artifactId>other</artifactId>
                  <version>9.9.9</version>
                </dependency>
              </dependencies>
            </project>
            """
        ),
        encoding="utf-8",
    )


def git(directory: Path, *args: str) -> None:
    subprocess.run(
        ["git", "-C", str(directory), *args],
        check=True,
        capture_output=True,
        text=True,
    )


def init_repo(version: str = "1.0.0-SNAPSHOT") -> tempfile.TemporaryDirectory[str]:
    temporary = tempfile.TemporaryDirectory()
    directory = Path(temporary.name)
    git(directory, "init", "-b", "master")
    git(directory, "config", "user.email", "version-bump-test@example.com")
    git(directory, "config", "user.name", "Version Bump Test")
    git(directory, "config", "core.hooksPath", "/dev/null")
    write_pom(directory, version)
    git(directory, "add", "pom.xml")
    git(
        directory,
        "commit",
        "-m",
        "chore(ci): initial\n\nRefs: #1",
    )
    return temporary


class ClassifyTest(unittest.TestCase):
    def test_types(self) -> None:
        self.assertEqual(classify("fix(base): correct a value\n\nRefs: #2"), "fix")
        self.assertEqual(classify("feat(jsonlink): add startup actions"), "feat")
        self.assertEqual(classify("perf(base): speed up the scan"), None)
        self.assertEqual(classify("chore(deps): bump jackson"), None)
        self.assertEqual(classify("docs(jsonlink): document the mapping"), None)
        self.assertEqual(classify("test(base): cover the timeout"), None)
        self.assertEqual(classify("refactor(base): split the parser"), None)
        self.assertEqual(classify("ci(release): refresh the workflow"), None)
        self.assertEqual(classify("build(parent): bump the compiler plugin"), None)
        self.assertEqual(classify("fix(docs): correct the guide"), "fix")
        self.assertIsNone(classify("Merge pull request #31 from somewhere"))
        self.assertIsNone(classify("not conventional"))

    def test_breaking_markers(self) -> None:
        self.assertEqual(
            classify(
                "refactor(base)!: remove the old constructor\n\n"
                "BREAKING CHANGE: callers must use the factory.\n"
                "Refs: #4"
            ),
            "breaking",
        )
        self.assertEqual(classify("perf(base)!: change the timing unit"), "breaking")
        self.assertEqual(
            classify("fix(base): correct a value\n\nBREAKING CHANGE: the unit changed."),
            "breaking",
        )


class BumpTest(unittest.TestCase):
    def test_rank(self) -> None:
        self.assertEqual(bump_version("1.0.0", ["fix(base): correct a value"]), "1.0.1")
        self.assertEqual(bump_version("1.0.0", ["feat(base): add a knob"]), "1.1.0")
        self.assertEqual(
            bump_version(
                "1.0.0",
                ["fix(base): correct a value", "feat(base): add a knob"],
            ),
            "1.1.0",
        )
        self.assertEqual(
            bump_version("1.0.0", ["feat(base)!: remove the constructor"]),
            "2.0.0",
        )
        self.assertEqual(
            bump_version("1.2.3", ["refactor(base)!: remove the constructor"]),
            "2.0.0",
        )
        self.assertEqual(bump_version("1.2.3", ["perf(base): speed up the scan"]), "1.2.3")
        self.assertEqual(
            bump_version(
                "1.2.3",
                ["docs(base): write the guide", "fix(base): correct a value"],
            ),
            "1.2.4",
        )


class RepositoryTest(unittest.TestCase):
    def test_maven_version_ignores_dependency_versions(self) -> None:
        with tempfile.TemporaryDirectory() as temporary:
            directory = Path(temporary)
            write_pom(directory, "1.2.3-SNAPSHOT")
            self.assertEqual(maven_release_version(directory / "pom.xml"), "1.2.3")

    def test_first_release_uses_the_maven_version(self) -> None:
        temporary = init_repo("1.4.0-SNAPSHOT")
        try:
            directory = Path(temporary.name)
            git(directory, "commit", "--allow-empty", "-m", "feat(base): add a knob\n\nRefs: #2")
            self.assertEqual(expected_tag(directory, "1.4.0"), "1.4.0")
            self.assertEqual(check_tag(directory, "1.4.0", "1.4.0"), [])
            self.assertTrue(check_tag(directory, "1.5.0", "1.4.0"))
        finally:
            temporary.cleanup()

    def test_commits_since_the_previous_tag(self) -> None:
        temporary = init_repo()
        try:
            directory = Path(temporary.name)
            git(directory, "tag", "1.0.0")
            git(directory, "commit", "--allow-empty", "-m", "perf(base): speed up the scan\n\nRefs: #2")
            self.assertEqual(expected_tag(directory, "1.0.0"), "1.0.0")
            git(directory, "commit", "--allow-empty", "-m", "fix(base): correct a value\n\nRefs: #3")
            self.assertEqual(expected_tag(directory, "1.0.1"), "1.0.1")
            git(directory, "commit", "--allow-empty", "-m", "feat(jsonlink): add startup actions\n\nRefs: #4")
            self.assertEqual(expected_tag(directory, "1.1.0"), "1.1.0")
            git(
                directory,
                "commit",
                "--allow-empty",
                "-m",
                "refactor(base)!: remove the old constructor\n\n"
                "BREAKING CHANGE: callers must use the factory.\n"
                "Refs: #5",
            )
            self.assertEqual(expected_tag(directory, "2.0.0"), "2.0.0")
            git(directory, "tag", "2.0.0")
            self.assertEqual(check_tag(directory, "2.0.0", "2.0.0"), [])
            self.assertTrue(check_tag(directory, "2.0.1", "2.0.0"))
        finally:
            temporary.cleanup()

    def test_non_semver_tag_is_not_a_previous_release(self) -> None:
        temporary = init_repo()
        try:
            directory = Path(temporary.name)
            git(directory, "tag", "not-a-release")
            git(directory, "commit", "--allow-empty", "-m", "fix(base): correct a value\n\nRefs: #2")
            self.assertEqual(expected_tag(directory, "1.0.0"), "1.0.0")
        finally:
            temporary.cleanup()


if __name__ == "__main__":
    unittest.main()
