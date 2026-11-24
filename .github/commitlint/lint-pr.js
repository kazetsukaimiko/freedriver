#!/usr/bin/env node
// Lint the pull request title and body as the squash commit they become.

const { execFileSync } = require('node:child_process');
const { mkdtempSync, writeFileSync } = require('node:fs');
const { tmpdir } = require('node:os');
const path = require('node:path');

const root = __dirname;

// release-please's snapshot pull request (docs/release.md). A label is
// self-asserted: any writer can set it. The author of a pull request the
// release App opened is not, so the Refs-footer exemption needs both.
const RELEASE_BOT = 'freedriver-release[bot]';
const SNAPSHOT_LABEL = 'autorelease: snapshot';

function parseLabels(raw) {
  try {
    const labels = JSON.parse(raw || '[]');
    return Array.isArray(labels) ? labels.filter((label) => typeof label === 'string') : [];
  } catch {
    return [];
  }
}

function isReleaseSnapshot(author, labels) {
  return author === RELEASE_BOT && Array.isArray(labels) && labels.includes(SNAPSHOT_LABEL);
}

function messageFromPullRequest(title, body) {
  const subject = (title ?? '').replace(/\r\n/g, '\n').trim();
  const description = (body ?? '')
    .replace(/\r\n/g, '\n')
    .replace(/^\n+/, '')
    .replace(/\s+$/, '');
  if (!description) {
    return subject;
  }
  return `${subject}\n\n${description}\n`;
}

function lintMessage(message, { releaseSnapshot = false } = {}) {
  const directory = mkdtempSync(path.join(tmpdir(), 'commitlint-'));
  const file = path.join(directory, 'COMMIT_EDITMSG');
  writeFileSync(file, message);
  const command = path.join(root, 'node_modules', '.bin', 'commitlint');
  try {
    const output = execFileSync(
      command,
      [
        '--edit',
        file,
        '--config',
        path.join(root, releaseSnapshot ? 'commitlint.snapshot.config.js' : 'commitlint.config.js'),
      ],
      { cwd: root, encoding: 'utf8' },
    );
    return { ok: true, output };
  } catch (error) {
    const stdout = error.stdout ? error.stdout.toString() : '';
    const stderr = error.stderr ? error.stderr.toString() : '';
    return { ok: false, output: `${stdout}${stderr}` };
  }
}

function main() {
  const title = process.env.PR_TITLE ?? '';
  if (!title.trim()) {
    console.error('pull request title is empty');
    process.exit(1);
  }
  const releaseSnapshot = isReleaseSnapshot(
    process.env.PR_AUTHOR ?? '',
    parseLabels(process.env.PR_LABELS),
  );
  const result = lintMessage(messageFromPullRequest(title, process.env.PR_BODY), {
    releaseSnapshot,
  });
  process.stdout.write(result.output);
  if (!result.ok) {
    process.exit(1);
  }
}

module.exports = {
  RELEASE_BOT,
  SNAPSHOT_LABEL,
  isReleaseSnapshot,
  lintMessage,
  messageFromPullRequest,
  parseLabels,
};

if (require.main === module) {
  main();
}
