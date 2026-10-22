const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const test = require('node:test');

const { SCOPES } = require('./commitlint.config.js');
const { lintMessage, messageFromPullRequest } = require('./lint-pr.js');

function lint(title, body) {
  return lintMessage(messageFromPullRequest(title, body));
}

function accepted(title, body) {
  const result = lint(title, body);
  assert.equal(result.ok, true, result.output);
}

function rejected(title, body, pattern) {
  const result = lint(title, body);
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
  for (const type of ['feat', 'fix', 'refactor', 'test', 'docs', 'chore', 'ci', 'build', 'perf']) {
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
