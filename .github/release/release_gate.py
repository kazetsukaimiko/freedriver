#!/usr/bin/env python3
"""Release gate: refuse a tag for any release pull request kaze did not approve.

Runs as the first job of .github/workflows/release-please.yml on a push to
master, before release-please. It reads GitHub's API with the built-in token
(contents: read, pull-requests: read) and runs no pull request code.

Candidates are found the way release-please finds them: merged pull requests
into master that carry the `autorelease: pending` label. Every candidate must
pass four checks (docs/release.md). One refusal fails the gate, the
release-please job is skipped, and nothing is tagged or published. The gate
only refuses; it never creates a tag.
"""

from __future__ import annotations

import base64
import json
import os
import re
import sys
import urllib.error
import urllib.parse
import urllib.request
from dataclasses import dataclass
from typing import Any, Protocol

RELEASE_BOT = "freedriver-release[bot]"
APPROVER = "kazetsukaimiko"
PENDING_LABEL = "autorelease: pending"
TARGET_BRANCH = "master"
MANIFEST = ".release-please-manifest.json"
TITLE = re.compile(r"^chore\(release\): release (\d+\.\d+\.\d+)$")
# A COMMENTED review leaves the reviewer's approval state as it was.
DECISIVE_REVIEW_STATES = {"APPROVED", "CHANGES_REQUESTED", "DISMISSED"}
PAGE_SIZE = 100


class NotFound(Exception):
    """The API answered 404."""


class Api(Protocol):
    def get(self, path: str, params: dict[str, str] | None = None) -> Any: ...


@dataclass(frozen=True)
class Candidate:
    """One merged pull request into master labelled `autorelease: pending`."""

    number: int
    title: str
    author: str
    head_sha: str
    merge_commit_sha: str | None
    reviews: list[dict[str, Any]]
    merge_commit_on_master: bool
    manifest_version: str | None


@dataclass(frozen=True)
class Refusal:
    number: int
    check: str
    detail: str

    def message(self) -> str:
        return f"release gate: pull request #{self.number} refused on {self.check}: {self.detail}"


def title_version(title: str) -> str | None:
    match = TITLE.match(title.strip())
    return match.group(1) if match else None


def approval_refusal(candidate: Candidate) -> str | None:
    decisive = [
        review
        for review in candidate.reviews
        if (review.get("user") or {}).get("login") == APPROVER
        and review.get("state") in DECISIVE_REVIEW_STATES
    ]
    if not decisive:
        return f"{APPROVER} has not approved it"
    latest = max(decisive, key=lambda review: review.get("submitted_at") or "")
    if latest["state"] != "APPROVED":
        return f"{APPROVER}'s latest review is {latest['state']}"
    if latest.get("commit_id") != candidate.head_sha:
        return (
            f"{APPROVER} approved {latest.get('commit_id')}, "
            f"and the final head is {candidate.head_sha}"
        )
    return None


def evaluate(candidate: Candidate) -> list[Refusal]:
    refusals: list[Refusal] = []

    def refuse(check: str, detail: str) -> None:
        refusals.append(Refusal(candidate.number, check, detail))

    if candidate.author != RELEASE_BOT:
        refuse("author", f"the author is {candidate.author}, and releases come from {RELEASE_BOT}")

    approval = approval_refusal(candidate)
    if approval:
        refuse("approval", approval)

    if not candidate.merge_commit_sha:
        refuse("merge-commit", "it has no merge commit")
    elif not candidate.merge_commit_on_master:
        refuse(
            "merge-commit",
            f"merge commit {candidate.merge_commit_sha} is not on {TARGET_BRANCH} at the pushed head",
        )

    version = title_version(candidate.title)
    if version is None:
        refuse("manifest-version", f"the title {candidate.title!r} names no release version")
    elif candidate.manifest_version != version:
        refuse(
            "manifest-version",
            f"the title proposes {version}, and {MANIFEST} at the merge commit has "
            f"{candidate.manifest_version}",
        )
    return refusals


def paginate(api: Api, path: str, params: dict[str, str] | None = None) -> list[Any]:
    items: list[Any] = []
    page = 1
    while True:
        batch = api.get(path, {**(params or {}), "per_page": str(PAGE_SIZE), "page": str(page)})
        items.extend(batch)
        if len(batch) < PAGE_SIZE:
            return items
        page += 1


def merge_commit_on_master(api: Api, repository: str, merge_sha: str | None, pushed_sha: str) -> bool:
    if not merge_sha:
        return False
    try:
        comparison = api.get(f"/repos/{repository}/compare/{merge_sha}...{pushed_sha}")
    except NotFound:
        return False
    # The pushed head is the merge commit or descends from it.
    return comparison.get("status") in ("ahead", "identical")


def manifest_version(api: Api, repository: str, merge_sha: str | None) -> str | None:
    if not merge_sha:
        return None
    try:
        file = api.get(f"/repos/{repository}/contents/{MANIFEST}", {"ref": merge_sha})
        manifest = json.loads(base64.b64decode(file["content"]))
    except (NotFound, KeyError, ValueError):
        return None
    version = manifest.get(".") if isinstance(manifest, dict) else None
    return version if isinstance(version, str) else None


def candidates(api: Api, repository: str, pushed_sha: str) -> list[Candidate]:
    pulls = paginate(
        api,
        f"/repos/{repository}/pulls",
        {"state": "closed", "base": TARGET_BRANCH, "sort": "updated", "direction": "desc"},
    )
    found: list[Candidate] = []
    for pull in pulls:
        labels = {label["name"] for label in pull.get("labels", [])}
        if not pull.get("merged_at") or PENDING_LABEL not in labels:
            continue
        number = pull["number"]
        merge_sha = pull.get("merge_commit_sha")
        found.append(
            Candidate(
                number=number,
                title=pull.get("title") or "",
                author=(pull.get("user") or {}).get("login") or "",
                head_sha=pull["head"]["sha"],
                merge_commit_sha=merge_sha,
                reviews=paginate(api, f"/repos/{repository}/pulls/{number}/reviews"),
                merge_commit_on_master=merge_commit_on_master(api, repository, merge_sha, pushed_sha),
                manifest_version=manifest_version(api, repository, merge_sha),
            )
        )
    return found


def run(api: Api, repository: str, pushed_sha: str) -> list[Refusal]:
    refusals: list[Refusal] = []
    for candidate in candidates(api, repository, pushed_sha):
        refusals.extend(evaluate(candidate))
    return refusals


class GitHubApi:
    def __init__(self, base_url: str, token: str) -> None:
        self.base_url = base_url.rstrip("/")
        self.token = token

    def get(self, path: str, params: dict[str, str] | None = None) -> Any:
        url = f"{self.base_url}{urllib.parse.quote(path)}"
        if params:
            url = f"{url}?{urllib.parse.urlencode(params)}"
        request = urllib.request.Request(
            url,
            headers={
                "Accept": "application/vnd.github+json",
                "Authorization": f"Bearer {self.token}",
                "X-GitHub-Api-Version": "2022-11-28",
            },
        )
        try:
            with urllib.request.urlopen(request, timeout=30) as response:
                return json.load(response)
        except urllib.error.HTTPError as error:
            if error.code == 404:
                raise NotFound(path) from error
            raise


def main() -> int:
    repository = os.environ["GITHUB_REPOSITORY"]
    pushed_sha = os.environ["GITHUB_SHA"]
    api = GitHubApi(os.environ.get("GITHUB_API_URL", "https://api.github.com"), os.environ["GITHUB_TOKEN"])
    refusals = run(api, repository, pushed_sha)
    if not refusals:
        print("release gate: every release pull request waiting for a tag passes")
        return 0
    for refusal in refusals:
        print(f"::error::{refusal.message()}")
    print(
        "release gate: release-please is skipped, so nothing is tagged or published. "
        "docs/release.md describes the recovery.",
        file=sys.stderr,
    )
    return 1


if __name__ == "__main__":
    sys.exit(main())
