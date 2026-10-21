#!/usr/bin/env python3
"""Decide the release tag from the Maven version and conventional commits.

The tag is MAJOR.MINOR.PATCH and equals the root pom version with a trailing
-SNAPSHOT removed. With no earlier version tag, that Maven version is the
release. After a version tag, commits since that tag bump the version:

- a breaking change bumps the major version
- otherwise a feat commit bumps the minor version
- otherwise a fix commit bumps the patch version
- refactor, test, docs, chore, ci, build, and perf do not bump

A breaking change is a ``!`` before the colon or a ``BREAKING CHANGE:`` footer.
"""

from __future__ import annotations

import argparse
import re
import subprocess
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

SEMVER = re.compile(r"^([0-9]+)\.([0-9]+)\.([0-9]+)$")
HEADER = re.compile(
    r"^(?P<type>feat|fix|refactor|test|docs|chore|ci|build|perf)"
    r"(?:\([^)]*\))?(?P<bang>!)?: (?P<subject>\S.*)$"
)
BREAKING_FOOTER = re.compile(r"(?m)^BREAKING CHANGE:[ \t]+\S")


def maven_release_version(pom_path: Path) -> str:
    root = ET.parse(pom_path).getroot()
    version = root.find("version")
    if version is None and root.tag.startswith("{"):
        namespace = root.tag.split("}", 1)[0][1:]
        version = root.find(f"{{{namespace}}}version")
    text = "" if version is None or version.text is None else version.text.strip()
    if not text:
        raise SystemExit(f"no project version in {pom_path}")
    if text.endswith("-SNAPSHOT"):
        text = text[: -len("-SNAPSHOT")]
    return text


def classify(message: str) -> str | None:
    """Return breaking, feat, fix, or None for a raw commit message."""
    normalized = message.replace("\r\n", "\n").strip()
    if not normalized:
        return None
    header = normalized.split("\n", 1)[0].strip()
    match = HEADER.match(header)
    breaking = bool(match and match.group("bang")) or bool(
        BREAKING_FOOTER.search(normalized)
    )
    if breaking:
        return "breaking"
    if match is None:
        return None
    if match.group("type") == "feat":
        return "feat"
    if match.group("type") == "fix":
        return "fix"
    return None


def bump_version(previous: str, messages: list[str]) -> str:
    parsed = SEMVER.fullmatch(previous)
    if parsed is None:
        raise SystemExit(f"previous version {previous} is not MAJOR.MINOR.PATCH")
    major, minor, patch = (int(part) for part in parsed.groups())
    rank = 0
    for message in messages:
        kind = classify(message)
        if kind == "breaking":
            rank = max(rank, 3)
        elif kind == "feat":
            rank = max(rank, 2)
        elif kind == "fix":
            rank = max(rank, 1)
    if rank == 3:
        return f"{major + 1}.0.0"
    if rank == 2:
        return f"{major}.{minor + 1}.0"
    if rank == 1:
        return f"{major}.{minor}.{patch + 1}"
    return previous


def git(repo: Path, *args: str) -> str:
    result = subprocess.run(
        ["git", "-C", str(repo), *args],
        check=True,
        capture_output=True,
        text=True,
    )
    return result.stdout


def previous_version_tag(repo: Path) -> str | None:
    """Latest semver tag strictly before HEAD. A tag on HEAD is this release."""
    head = git(repo, "rev-parse", "HEAD").strip()
    names = [
        line.strip()
        for line in git(repo, "tag", "--merged", "HEAD").splitlines()
        if line.strip()
    ]
    candidates: list[tuple[int, int, int]] = []
    for name in names:
        parsed = SEMVER.fullmatch(name)
        if parsed is None:
            continue
        commit = git(repo, "rev-parse", f"{name}^{{}}").strip()
        if commit == head:
            continue
        candidates.append(tuple(int(part) for part in parsed.groups()))
    if not candidates:
        return None
    major, minor, patch = max(candidates)
    return f"{major}.{minor}.{patch}"


def commit_messages_since(repo: Path, previous: str | None) -> list[str]:
    revision = "HEAD" if previous is None else f"{previous}..HEAD"
    raw = git(repo, "log", revision, "--format=%B%x1e")
    return [message.strip("\n") for message in raw.split("\x1e") if message.strip()]


def expected_tag(repo: Path, maven_version: str) -> str:
    if not SEMVER.fullmatch(maven_version):
        raise SystemExit(
            f"Maven release version {maven_version} is not MAJOR.MINOR.PATCH"
        )
    previous = previous_version_tag(repo)
    if previous is None:
        return maven_version
    return bump_version(previous, commit_messages_since(repo, previous))


def check_tag(repo: Path, tag: str, maven_version: str) -> list[str]:
    errors: list[str] = []
    if SEMVER.fullmatch(tag) is None:
        errors.append(f"tag {tag} is not MAJOR.MINOR.PATCH")
    if tag != maven_version:
        errors.append(
            f"tag {tag} is not the Maven release version {maven_version}"
        )
    if not errors:
        expected = expected_tag(repo, maven_version)
        if tag != expected:
            errors.append(
                f"tag {tag} does not match the conventional bump {expected} "
                "(breaking bumps major, feat bumps minor, fix bumps patch)"
            )
    return errors


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "tag",
        nargs="?",
        help="version tag to check; omit to print the expected tag",
    )
    parser.add_argument("--pom", default="pom.xml")
    parser.add_argument("--repo", default=".")
    args = parser.parse_args(argv)
    repo = Path(args.repo).resolve()
    maven_version = maven_release_version(repo / args.pom)
    if args.tag is None:
        print(expected_tag(repo, maven_version))
        return 0
    errors = check_tag(repo, args.tag, maven_version)
    if errors:
        for error in errors:
            print(error, file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
