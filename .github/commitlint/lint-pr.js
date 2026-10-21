#!/usr/bin/env node
// Lint the pull request title and body as the squash commit they become.

const { execFileSync } = require('node:child_process');
const { mkdtempSync, writeFileSync } = require('node:fs');
const { tmpdir } = require('node:os');
const path = require('node:path');

const root = __dirname;

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

function lintMessage(message) {
  const directory = mkdtempSync(path.join(tmpdir(), 'commitlint-'));
  const file = path.join(directory, 'COMMIT_EDITMSG');
  writeFileSync(file, message);
  const command = path.join(root, 'node_modules', '.bin', 'commitlint');
  try {
    const output = execFileSync(
      command,
      ['--edit', file, '--config', path.join(root, 'commitlint.config.js')],
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
  const result = lintMessage(messageFromPullRequest(title, process.env.PR_BODY));
  process.stdout.write(result.output);
  if (!result.ok) {
    process.exit(1);
  }
}

module.exports = { lintMessage, messageFromPullRequest };

if (require.main === module) {
  main();
}
