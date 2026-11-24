const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const test = require('node:test');

const { SCOPES } = require('./commitlint.config.js');
const {
  RELEASE_BOT,
  SNAPSHOT_LABEL,
  isReleaseSnapshot,
  lintMessage,
  messageFromPullRequest,
  parseLabels,
} = require('./lint-pr.js');

function lint(title, body, pullRequest) {
  const releaseSnapshot = pullRequest
    ? isReleaseSnapshot(pullRequest.author, parseLabels(pullRequest.labels))
    : false;
  return lintMessage(messageFromPullRequest(title, body), { releaseSnapshot });
}

function accepted(title, body, pullRequest) {
  const result = lint(title, body, pullRequest);
  assert.equal(result.ok, true, result.output);
}

function rejected(title, body, pattern, pullRequest) {
  const result = lint(title, body, pullRequest);
  assert.equal(result.ok, false, result.output);
  if (pattern) {
    assert.match(result.output, pattern);
  }
}

test('a scoped title and a Refs footer pass', () => {
  accepted('feat(jsonlink): add per-board startup actions', 'Refs: #24');
  accepted('chore(3p-bom): split third-party versions', 'Refs: #24');
});

test('every allowed type passes with a scope', () => {
  for (const type of [
    'feat',
    'fix',
    'refactor',
    'test',
    'docs',
    'chore',
    'ci',
    'build',
    'perf',
    'revert',
  ]) {
    accepted(`${type}(base): adjust the scanner`, 'Refs: #24');
  }
});

test('the card can be named as an issue reference or a url', () => {
  accepted('fix(base): correct a value', 'Refs: kazetsukaimiko/freedriver#24');
  accepted(
    'fix(base): correct a value',
    'Refs: https://github.com/kazetsukaimiko/freedriver/issues/24',
  );
  accepted('fix(base): correct a value', 'Body text.\n\nRefs: #24, #25');
});

test('missing or unnamed Refs fail', () => {
  rejected('feat(base): add a knob', '', /Refs:/);
  rejected('feat(base): add a knob', 'Refs: the card', /Refs:/);
  rejected('feat(base): add a knob\nRefs: #24', '', /Refs:/);
});

test('type and scope are required and closed', () => {
  rejected('style(base): tidy imports', 'Refs: #24', /type-enum/);
  rejected('feat: add a knob', 'Refs: #24', /scope-empty/);
  rejected('feat(not-a-module): add a knob', 'Refs: #24', /scope-enum/);
  rejected('feat(base): Add a knob', 'Refs: #24', /subject-case/);
  rejected('feat(base): add a knob.', 'Refs: #24', /subject-full-stop/);
});

test('a breaking change needs both markers', () => {
  rejected('feat(jsonlink)!: change the mapping shape', 'Refs: #24', /breaking-mark-and-footer/);
  rejected(
    'feat(jsonlink): change the mapping shape',
    'BREAKING CHANGE: the old list is gone.\nRefs: #24',
    /breaking-mark-and-footer/,
  );
  accepted(
    'feat(jsonlink)!: change the mapping shape',
    'The old list is gone.\n\nBREAKING CHANGE: callers must send startupActions.\nRefs: #24',
  );
});

test('a revert of a release commit passes with a scope and a Refs footer', () => {
  accepted(
    'revert(release): release 1.1.0',
    'This reverts commit 6d2e3cad9b0f391501a8e1b52643c24f00dc1164.\n\nRefs: #81',
  );
  rejected('revert: release 1.1.0', 'Refs: #81', /scope-empty/);
  rejected('revert(release): release 1.1.0', '', /Refs:/);
});

const SNAPSHOT_TITLE = 'chore(release): release 1.0.1-SNAPSHOT';
const SNAPSHOT_BODY_WITHOUT_REFS =
  ':robot: I have created a release *beep* *boop*\n---\n\n\n### Updating meta-information for bleeding-edge SNAPSHOT release.\n\n---\nThis PR was generated with Release Please.';
const SNAPSHOT_LABELS = JSON.stringify([SNAPSHOT_LABEL]);

test('the release App is the bot the release gate names', () => {
  assert.equal(RELEASE_BOT, 'freedriver-release[bot]');
  assert.equal(SNAPSHOT_LABEL, 'autorelease: snapshot');
  const gate = fs.readFileSync(path.join(__dirname, '..', 'release', 'release_gate.py'), 'utf8');
  assert.ok(gate.includes(`RELEASE_BOT = "${RELEASE_BOT}"`));
});

test('a snapshot pull request the release App opened passes without a Refs footer', () => {
  accepted(SNAPSHOT_TITLE, SNAPSHOT_BODY_WITHOUT_REFS, {
    author: RELEASE_BOT,
    labels: SNAPSHOT_LABELS,
  });
});

test('the snapshot label on anyone else\'s pull request still needs a Refs footer', () => {
  for (const author of ['kazetsukaiyuni', 'kazetsukaimiko', 'freedriver-release', '']) {
    rejected(SNAPSHOT_TITLE, SNAPSHOT_BODY_WITHOUT_REFS, /Refs:/, {
      author,
      labels: SNAPSHOT_LABELS,
    });
  }
});

test('an unlabelled pull request from the release App still needs a Refs footer', () => {
  for (const labels of ['[]', JSON.stringify(['autorelease: pending']), '', 'not json']) {
    rejected(SNAPSHOT_TITLE, SNAPSHOT_BODY_WITHOUT_REFS, /Refs:/, { author: RELEASE_BOT, labels });
  }
});

test('the snapshot exemption lifts the Refs footer rule only', () => {
  const snapshot = { author: RELEASE_BOT, labels: SNAPSHOT_LABELS };
  rejected('chore(master): release 1.0.1-SNAPSHOT', SNAPSHOT_BODY_WITHOUT_REFS, /scope-enum/, snapshot);
  rejected('style(release): release 1.0.1-SNAPSHOT', SNAPSHOT_BODY_WITHOUT_REFS, /type-enum/, snapshot);
  const base = require('./commitlint.config.js');
  const lifted = require('./commitlint.snapshot.config.js');
  const changed = Object.keys(base.rules).filter(
    (rule) => JSON.stringify(base.rules[rule]) !== JSON.stringify(lifted.rules[rule]),
  );
  assert.deepEqual(changed, ['refs-footer']);
  assert.deepEqual(Object.keys(lifted.rules), Object.keys(base.rules));
});

test('release-please pull requests pass as release-please writes them', () => {
  const release =
    ':robot: I have created a release *beep* *boop*\n---\n\n\n<details><summary>1.0.1</summary>\n\n## [1.0.1](https://github.com/kazetsukaimiko/freedriver/compare/1.0.0...1.0.1) (2026-11-23)\n\n\n### Bug Fixes\n\n* **jsonlink:** read every toggle and appliance json file ([#68](https://github.com/kazetsukaimiko/freedriver/issues/68))\n</details>\n\n---\nRefs: #81';
  const snapshot =
    ':robot: I have created a release *beep* *boop*\n---\n\n\n<details><summary>1.0.1-SNAPSHOT</summary>\n\n### Updating meta-information for bleeding-edge SNAPSHOT release.\n</details>\n\n---\nRefs: #81';
  accepted('chore(release): release 1.0.1', release);
  accepted(SNAPSHOT_TITLE, snapshot);
});

test('merge titles are linted', () => {
  rejected(
    'Merge pull request #32 from kazetsukaimiko/yuni/27-provision-uuid',
    'Refs: #24',
    /type-empty|type-enum/,
  );
});

test('reactor modules are scopes', () => {
  const pom = fs.readFileSync(path.join(__dirname, '..', '..', 'pom.xml'), 'utf8');
  const withoutComments = pom.replace(/<!--[\s\S]*?-->/g, '');
  const modules = [...withoutComments.matchAll(/<module>([^<]+)<\/module>/g)].map(
    (match) => match[1],
  );
  assert.ok(modules.length > 0);
  for (const moduleName of modules) {
    assert.ok(SCOPES.includes(moduleName), moduleName);
  }
});
