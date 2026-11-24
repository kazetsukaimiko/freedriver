# Releasing freedriver

freedriver is released by [release-please](https://github.com/googleapis/release-please) with the `maven` release type. A release is a pull request that kazetsukaimiko approves and merges. Merging it creates the version tag and the GitHub Release, and the tag starts the package publish, which kazetsukaimiko approves in the `packages` environment.

Agents never merge a release pull request. kazetsukaimiko calls each release through the Project Manager.

## The flow

1. A pull request merges to `master`. `.github/workflows/release-please.yml` runs on that push.
2. The **release gate** job checks every merged release pull request that is waiting for a tag (see [The release gate](#the-release-gate-tag-time)). The release-please job runs only when the gate passes.
3. The **release-please** job opens or updates one pull request:
   - a **snapshot pull request**, `chore(release): release X.Y.Z-SNAPSHOT`, when the poms on `master` still carry a released version. It sets the next `-SNAPSHOT` on the root pom version and on each child pom's parent version, and nothing else.
   - a **release pull request**, `chore(release): release X.Y.Z`, when `master` has a `feat`, a `fix` or a breaking change since the last release. It sets `X.Y.Z` on those same pom versions, writes `CHANGELOG.md` and updates `.release-please-manifest.json`.
4. kazetsukaimiko approves and squash-merges the release pull request when he calls the release.
5. That merge is a push to `master`. The gate passes the merged release pull request, and release-please creates the tag `X.Y.Z` on its merge commit and a GitHub Release whose notes are the changelog for that version.
6. The tag push starts `.github/workflows/publish-release.yml`. From #70 on, its publish job runs in the `packages` environment and waits there for kazetsukaimiko's deployment approval. After that approval it publishes the release allowlist to GitHub Packages at version `X.Y.Z`. So a release takes two approvals from kazetsukaimiko: the review on the release pull request, and the `packages` deployment after the tag.
7. The same push opens the snapshot pull request for the next development version. It needs one approving review and creates no tag, GitHub Release or package.

The first run after this flow lands opens a snapshot pull request for `1.0.1-SNAPSHOT`: tag `1.0.0` was cut by hand while the poms stayed on `1.0.0-SNAPSHOT`, and release-please moves the poms past a released version before it proposes the next one. The first release pull request follows once that snapshot pull request merges.

## Versions

release-please computes the version from the conventional commits on `master` since the last release:

| Commit | Bump |
| --- | --- |
| a breaking change (`!` and a `BREAKING CHANGE:` footer) | major |
| `feat` | minor |
| `fix` | patch |
| `perf`, `refactor`, `revert`, `docs`, `test`, `chore`, `ci`, `build`, `style` | none, and no release pull request |

`release-please-config.json` lists `feat` and `fix` as the visible changelog sections and hides every other type. release-please opens a release pull request only when the changelog has a visible entry, so a merge of a hidden type alone leaves the release pull request as it is.

Tags are `MAJOR.MINOR.PATCH` with no `v` prefix. The reactor is one release-please package at the repository root: one version, one tag, one release pull request. There are 24 `pom.xml` files; `serial-natives/pom.xml` is among them. `genetry-kibana/dependency-reduced-pom.xml` is a build output and stays as it is.

History starts after tag `1.0.0` (`6d2e3cad9b0f391501a8e1b52643c24f00dc1164`): `bootstrap-sha` names that commit and the manifest starts at `1.0.0`.

### What consumers pin

The GitHub Release title and the heading of its notes are the released version. That is the version autonomy moves its freedriver dependency to.

## Who does what

| Actor | Does |
| --- | --- |
| `freedriver-release[bot]`, the release GitHub App | Authors the release and snapshot pull requests, creates the tag and the GitHub Release |
| kazetsukaimiko | Approves and merges release pull requests, and approves the `packages` deployment that publishes |
| Any reviewer | Approves the snapshot pull request |
| `publish-release.yml` | Publishes the packages from the tag, after kazetsukaimiko's approval in the `packages` environment (#70) |

No workflow merges a pull request, and nothing acts as kazetsukaimiko.

Release and snapshot pull requests carry the footer `Refs: #81`. #81 is the standing "freedriver releases" card; it stays open and lists each release.

### The release GitHub App and the `release` environment

- **App:** `freedriver-release`, owned by kazetsukaimiko, installed on this repository only, with repository permissions `contents: write` and `pull-requests: write`.
- **Environment:** `release`, with deployment branches limited to `master`.
- **Secrets of that environment:** `RELEASE_APP_ID` and `RELEASE_APP_PRIVATE_KEY`.
- **Tag ruleset:** the App is a bypass actor on the `*.*.*` tag ruleset (#61), so it can create version tags.

Only the release-please job runs in the `release` environment, and only it reads the two secrets. It mints a token for one run with `actions/create-github-app-token`. Every action that receives that token is pinned to a full commit SHA. When a secret is missing, the release-please job fails with a message naming the secret and the `release` environment.

An App token starts other workflows, so CI and commitlint run on the release and snapshot pull requests, and the tag push starts `publish-release.yml`.

commitlint requires a `Refs:` footer on every pull request. release-please writes `Refs: #81` on both of its pull requests. As a fallback, `lint-pr.js` lifts that one rule for a pull request that carries `autorelease: snapshot` and whose author is `freedriver-release[bot]`; the label alone lifts nothing, because any writer can set a label.

## Authorization

A release happens only with kazetsukaimiko's approval. Two gates enforce it.

### Code-owner review (merge time)

`.github/CODEOWNERS` gives kazetsukaimiko these paths:

```
/.github/ @kazetsukaimiko
/CHANGELOG.md @kazetsukaimiko
/release-please-config.json @kazetsukaimiko
/.release-please-manifest.json @kazetsukaimiko
```

The `master` ruleset (id 18279336, read 2026-11-23) has:

| Setting | Value |
| --- | --- |
| `require_code_owner_review` | true |
| `dismiss_stale_reviews_on_push` | true |
| `require_last_push_approval` | false |
| `required_approving_review_count` | 1 |

Every release pull request changes `CHANGELOG.md` and the manifest, so it merges only with kazetsukaimiko's approval, and a push after that approval dismisses it. The App is the author, so kazetsukaimiko's approval counts as the code-owner review. A snapshot pull request changes only pom versions, so one approving review from any reviewer merges it.

The regression check for this gate is manual, because a repository test cannot exercise a ruleset: on a release pull request kazetsukaimiko has approved, a new push to its head removes the approval and GitHub blocks the merge until he approves again. If a ruleset change turns off both `dismiss_stale_reviews_on_push` and `require_last_push_approval`, kazetsukaimiko turns one back on.

### The release gate (tag time)

The release gate is the first job of `release-please.yml`. It runs `.github/release/release_gate.py` from `master` with the built-in `GITHUB_TOKEN` (`contents: read`, `pull-requests: read`), outside any environment. It reads GitHub's API only and runs no pull request code.

It finds candidates the way release-please does: merged pull requests into `master` that carry the `autorelease: pending` label. For each one it checks:

| Check | Passes when |
| --- | --- |
| `author` | the pull request's author is `freedriver-release[bot]` |
| `approval` | kazetsukaimiko's latest review is `APPROVED` and was submitted on the pull request's final head commit |
| `merge-commit` | the pull request has a merge commit and it is on `master` at the pushed head |
| `manifest-version` | the version in the pull request title equals the `"."` version in `.release-please-manifest.json` at that merge commit |

For `approval`, a `COMMENTED` review leaves the approval as it was, as on GitHub; a later `CHANGES_REQUESTED` or `DISMISSED` review from kazetsukaimiko removes it.

When any candidate fails any check, the gate fails with a message naming the pull request number and the failed check (`release gate: pull request #N refused on <check>: …`), and the release-please job is skipped: no tag, no GitHub Release, no package. The gate only refuses. Tags come from release-please alone.

## A release pull request merged without kazetsukaimiko's approval

The only way to merge a release pull request without that approval is an admin bypass of the `master` ruleset. A release never needs one, because kazetsukaimiko approves the App's pull request like any other.

After such a merge, the push runs `release-please.yml`, the gate refuses the pull request on the `approval` check, and release-please stays skipped. Nothing is tagged or published, so there is nothing to delete. The pull request keeps `autorelease: pending`, so the gate refuses it again on every later push until the recovery below.

Recovery is kazetsukaimiko's, in two steps:

1. **Revert the release commit on `master`** with a commit titled `revert(release): …` and a `Refs:` footer. The revert changes `CHANGELOG.md`, which he owns, so an agent opens the revert pull request and he approves and merges it.
2. **Relabel the merged release pull request:** remove `autorelease: pending` and add `autorelease: rejected`. release-please and the gate ignore that label.

The next push to `master` then passes the gate and release-please runs as usual. The revert restored the poms and the manifest, and nothing was published, so the next release pull request proposes the same version again.

## Labels

| Label | Meaning | Set by |
| --- | --- | --- |
| `autorelease: pending` | a release pull request waiting for its tag | release-please |
| `autorelease: tagged` | a release pull request whose tag and GitHub Release exist | release-please |
| `autorelease: snapshot` | a snapshot pull request | release-please |
| `autorelease: rejected` | a merged release pull request that is never tagged (see the recovery above) | kazetsukaimiko |

## Before the first run

kazetsukaimiko sets these up; agent accounts cannot:

1. Create the GitHub App `freedriver-release` (`contents: write`, `pull-requests: write`) and install it on this repository only.
2. Create the environment `release` with deployment branches limited to `master`, and add the secrets `RELEASE_APP_ID` and `RELEASE_APP_PRIVATE_KEY`.
3. Add the App as a bypass actor on the `*.*.*` tag ruleset (#61).
4. Create the label `autorelease: rejected`.

If the App gets another name, the bot login in `.github/release/release_gate.py` and `.github/commitlint/lint-pr.js` follows it.
