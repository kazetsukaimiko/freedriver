#!/usr/bin/env python3
"""Contract checks for the release-please config, manifest, and code owners."""

from __future__ import annotations

import json
import re
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
CONFIG = ROOT / "release-please-config.json"
MANIFEST = ROOT / ".release-please-manifest.json"
CODEOWNERS = ROOT / ".github" / "CODEOWNERS"
COMMITLINT_CONFIG = ROOT / ".github" / "commitlint" / "commitlint.config.js"

TAG_1_0_0 = "6d2e3cad9b0f391501a8e1b52643c24f00dc1164"
TITLE_PATTERN = "chore(release): release ${version}"
RELEASES_CARD_FOOTER = "Refs: #81"
VISIBLE_TYPES = {"feat", "fix"}
HIDDEN_TYPES = {
    "perf",
    "refactor",
    "revert",
    "docs",
    "test",
    "chore",
    "ci",
    "build",
    "style",
}
OWNED_BY_KAZE = [
    "/.github/ @kazetsukaimiko",
    "/CHANGELOG.md @kazetsukaimiko",
    "/release-please-config.json @kazetsukaimiko",
    "/.release-please-manifest.json @kazetsukaimiko",
]


def config() -> dict:
    return json.loads(CONFIG.read_text(encoding="utf-8"))


class CodeOwnersTest(unittest.TestCase):
    def test_kaze_owns_the_release_files(self) -> None:
        lines = [
            line.strip()
            for line in CODEOWNERS.read_text(encoding="utf-8").splitlines()
            if line.strip() and not line.lstrip().startswith("#")
        ]
        for line in OWNED_BY_KAZE:
            with self.subTest(line=line):
                self.assertIn(line, lines)

    def test_no_later_rule_reassigns_a_release_file(self) -> None:
        # The last matching CODEOWNERS pattern wins, so every rule is one of
        # kazetsukaimiko's.
        for line in CODEOWNERS.read_text(encoding="utf-8").splitlines():
            line = line.strip()
            if not line or line.startswith("#"):
                continue
            with self.subTest(line=line):
                self.assertEqual(line.split()[1:], ["@kazetsukaimiko"])


class ReleasePleaseConfigTest(unittest.TestCase):
    def test_one_maven_package_at_the_root(self) -> None:
        data = config()
        self.assertEqual(data["release-type"], "maven")
        # An empty package entry keeps the component empty. A package-name
        # would make release-please look for a component branch and tag nothing.
        self.assertEqual(data["packages"], {".": {}})

    def test_tags_have_no_v_and_no_component(self) -> None:
        data = config()
        self.assertIs(data["include-v-in-tag"], False)
        self.assertIs(data["include-component-in-tag"], False)

    def test_release_and_snapshot_share_one_title_pattern(self) -> None:
        # With separate-pull-requests false the Merge plugin titles and footers
        # both the release and the snapshot pull request from the group
        # pattern. The strategy reads merged titles with
        # pull-request-title-pattern, so the two are the same string.
        data = config()
        self.assertIs(data["separate-pull-requests"], False)
        self.assertEqual(data["pull-request-title-pattern"], TITLE_PATTERN)
        self.assertEqual(data["group-pull-request-title-pattern"], TITLE_PATTERN)

    def test_title_scope_is_a_commitlint_scope(self) -> None:
        scope = re.match(r"^chore\(([^)]+)\): ", TITLE_PATTERN).group(1)
        scopes = COMMITLINT_CONFIG.read_text(encoding="utf-8")
        self.assertIn(f"'{scope}',", scopes)
        self.assertIn("${version}", TITLE_PATTERN)

    def test_footer_names_the_releases_card(self) -> None:
        self.assertEqual(config()["pull-request-footer"], RELEASES_CARD_FOOTER)

    def test_only_feat_and_fix_are_visible(self) -> None:
        sections = config()["changelog-sections"]
        visible = {s["type"] for s in sections if not s.get("hidden", False)}
        hidden = {s["type"] for s in sections if s.get("hidden", False)}
        self.assertEqual(visible, VISIBLE_TYPES)
        self.assertEqual(hidden, HIDDEN_TYPES)
        for section in sections:
            if section["type"] in HIDDEN_TYPES:
                with self.subTest(type=section["type"]):
                    self.assertIs(section["hidden"], True)

    def test_snapshot_pull_requests_stay_on(self) -> None:
        text = CONFIG.read_text(encoding="utf-8")
        self.assertNotIn("skip-snapshot", text)
        self.assertNotIn("always-update", text)
        self.assertNotIn("release-as", text)

    def test_history_starts_after_tag_1_0_0(self) -> None:
        self.assertEqual(config()["bootstrap-sha"], TAG_1_0_0)
        manifest = json.loads(MANIFEST.read_text(encoding="utf-8"))
        self.assertEqual(set(manifest), {"."})
        self.assertRegex(manifest["."], r"^\d+\.\d+\.\d+$")


if __name__ == "__main__":
    unittest.main()
