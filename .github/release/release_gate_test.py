#!/usr/bin/env python3
"""The release gate on recorded GitHub API responses."""

from __future__ import annotations

import base64
import copy
import json
import unittest
from pathlib import Path
from typing import Any

import release_gate
from release_gate import NotFound, run

REPO = "kazetsukaimiko/freedriver"
HEAD = "a" * 40
EARLIER_HEAD = "b" * 40
MERGE = "c" * 40
PUSHED = "d" * 40


def manifest_file(version: str) -> dict[str, Any]:
    content = json.dumps({".": version}).encode("utf-8")
    return {"encoding": "base64", "content": base64.b64encode(content).decode("ascii")}


def review(login: str, state: str, commit: str, at: str) -> dict[str, Any]:
    return {"user": {"login": login}, "state": state, "commit_id": commit, "submitted_at": at}


def release_pull(number: int = 90) -> dict[str, Any]:
    return {
        "number": number,
        "title": "chore(release): release 1.1.0",
        "user": {"login": "freedriver-release[bot]"},
        "head": {"sha": HEAD},
        "merged_at": "2026-12-01T10:00:00Z",
        "merge_commit_sha": MERGE,
        "labels": [{"name": "autorelease: pending"}],
    }


class RecordedApi:
    """Answers from recorded responses, keyed by path."""

    def __init__(self) -> None:
        self.pulls: list[dict[str, Any]] = [release_pull()]
        self.reviews: dict[int, list[dict[str, Any]]] = {
            90: [review("kazetsukaimiko", "APPROVED", HEAD, "2026-12-01T09:00:00Z")]
        }
        self.compare_status = "ahead"
        self.manifest: dict[str, Any] | None = manifest_file("1.1.0")
        self.requests: list[tuple[str, dict[str, str]]] = []

    def get(self, path: str, params: dict[str, str] | None = None) -> Any:
        params = params or {}
        self.requests.append((path, params))
        page = int(params.get("page", "1"))
        size = int(params.get("per_page", "100"))
        if path == f"/repos/{REPO}/pulls":
            assert params["state"] == "closed" and params["base"] == "master", params
            return copy.deepcopy(self.pulls[(page - 1) * size : page * size])
        if path.endswith("/reviews"):
            number = int(path.split("/")[-2])
            return copy.deepcopy(self.reviews.get(number, [])[(page - 1) * size : page * size])
        if path == f"/repos/{REPO}/compare/{MERGE}...{PUSHED}":
            if self.compare_status == "missing":
                raise NotFound(path)
            return {"status": self.compare_status}
        if path == f"/repos/{REPO}/contents/.release-please-manifest.json":
            assert params["ref"] == MERGE, params
            if self.manifest is None:
                raise NotFound(path)
            return copy.deepcopy(self.manifest)
        raise AssertionError(f"unexpected request {path} {params}")


class ReleaseGateTest(unittest.TestCase):
    def setUp(self) -> None:
        self.api = RecordedApi()

    def refusals(self) -> list[release_gate.Refusal]:
        return run(self.api, REPO, PUSHED)

    def assert_refused(self, check: str, number: int = 90) -> str:
        refusals = self.refusals()
        self.assertEqual([(r.number, r.check) for r in refusals], [(number, check)], refusals)
        message = refusals[0].message()
        self.assertIn(f"pull request #{number}", message)
        self.assertIn(f"refused on {check}", message)
        return message

    def test_an_approved_app_release_passes(self) -> None:
        self.assertEqual(self.refusals(), [])

    def test_a_release_merged_without_kazes_approval_is_refused(self) -> None:
        # As after a merge through an admin bypass of the master ruleset.
        self.api.reviews[90] = []
        message = self.assert_refused("approval")
        self.assertIn("kazetsukaimiko has not approved it", message)

    def test_another_reviewers_approval_is_not_kazes(self) -> None:
        self.api.reviews[90] = [review("lonewatt-architect", "APPROVED", HEAD, "2026-12-01T09:00:00Z")]
        self.assert_refused("approval")

    def test_another_author_is_refused(self) -> None:
        self.api.pulls[0]["user"]["login"] = "kazetsukaiyuni"
        message = self.assert_refused("author")
        self.assertIn("kazetsukaiyuni", message)

    def test_an_approval_on_an_earlier_head_is_refused(self) -> None:
        self.api.reviews[90] = [review("kazetsukaimiko", "APPROVED", EARLIER_HEAD, "2026-12-01T09:00:00Z")]
        message = self.assert_refused("approval")
        self.assertIn(EARLIER_HEAD, message)
        self.assertIn(HEAD, message)

    def test_a_dismissed_or_withdrawn_approval_is_refused(self) -> None:
        for state in ("DISMISSED", "CHANGES_REQUESTED"):
            with self.subTest(state=state):
                self.api.reviews[90] = [
                    review("kazetsukaimiko", "APPROVED", HEAD, "2026-12-01T09:00:00Z"),
                    review("kazetsukaimiko", state, HEAD, "2026-12-01T09:30:00Z"),
                ]
                self.assert_refused("approval")

    def test_a_later_comment_keeps_the_approval(self) -> None:
        self.api.reviews[90].append(review("kazetsukaimiko", "COMMENTED", HEAD, "2026-12-01T09:30:00Z"))
        self.assertEqual(self.refusals(), [])

    def test_a_manifest_version_that_differs_from_the_tag_is_refused(self) -> None:
        self.api.manifest = manifest_file("1.0.9")
        message = self.assert_refused("manifest-version")
        self.assertIn("1.1.0", message)
        self.assertIn("1.0.9", message)

    def test_a_missing_manifest_or_a_title_without_a_version_is_refused(self) -> None:
        self.api.manifest = None
        self.assert_refused("manifest-version")
        self.api.manifest = manifest_file("1.1.0")
        self.api.pulls[0]["title"] = "chore(release): release the next one"
        self.assert_refused("manifest-version")

    def test_a_merge_commit_off_master_is_refused(self) -> None:
        for status in ("behind", "diverged", "missing"):
            with self.subTest(status=status):
                self.api.compare_status = status
                self.assert_refused("merge-commit")

    def test_the_pushed_head_can_be_the_merge_commit(self) -> None:
        self.api.compare_status = "identical"
        self.assertEqual(self.refusals(), [])

    def test_only_merged_pending_pull_requests_are_candidates(self) -> None:
        tagged = release_pull(80)
        tagged["labels"] = [{"name": "autorelease: tagged"}]
        rejected = release_pull(81)
        rejected["labels"] = [{"name": "autorelease: rejected"}]
        snapshot = release_pull(82)
        snapshot["labels"] = [{"name": "autorelease: snapshot"}]
        closed = release_pull(83)
        closed["merged_at"] = None
        self.api.pulls = [tagged, rejected, snapshot, closed]
        self.assertEqual(self.refusals(), [])
        self.assertFalse([path for path, _ in self.api.requests if path.endswith("/reviews")])

    def test_one_refused_candidate_among_several_fails_the_gate(self) -> None:
        self.api.pulls.append(release_pull(91))
        self.api.reviews[91] = []
        self.assert_refused("approval", number=91)

    def test_every_page_of_pull_requests_and_reviews_is_read(self) -> None:
        filler = []
        for number in range(1000, 1150):
            pull = release_pull(number)
            pull["labels"] = []
            filler.append(pull)
        late = release_pull(91)
        self.api.pulls = filler + [late]
        self.api.reviews[91] = [
            review("lonewatt-architect", "COMMENTED", HEAD, f"2026-11-30T00:00:{i % 60:02d}Z") for i in range(100)
        ] + [review("kazetsukaimiko", "APPROVED", HEAD, "2026-12-01T09:00:00Z")]
        self.assertEqual(self.refusals(), [])
        self.api.reviews[91].pop()
        self.assert_refused("approval", number=91)

    def test_the_gate_only_reads(self) -> None:
        text = Path(release_gate.__file__).read_text(encoding="utf-8")
        for word in ("git tag", "/git/refs", "/releases", "method=", "subprocess"):
            with self.subTest(word=word):
                self.assertNotIn(word, text)


if __name__ == "__main__":
    unittest.main()
