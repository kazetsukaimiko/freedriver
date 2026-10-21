// Pull request titles become the squash commit on master.
// Types are the acceptance list. Scopes are the reactor modules plus the
// repo-level names used when a change is not one module.

const TYPES = [
  'feat',
  'fix',
  'refactor',
  'test',
  'docs',
  'chore',
  'ci',
  'build',
  'perf',
];

const SCOPES = [
  '3p-bom',
  'base',
  'bom',
  'build',
  'ci',
  'client',
  'daly',
  'deps',
  'discovery',
  'docs',
  'ee',
  'electrodacus',
  'genetry',
  'genetry-kibana',
  'inotify',
  'inotify-cdi',
  'jakarta-bom',
  'jsonlink',
  'math',
  'math-jpa',
  'parent',
  'release',
  'serial-api',
  'serial-impl',
  'serial-natives',
  'victron',
  'victron-jpa',
];

const CARD = /(?:^|[\s,])#\d+\b|[\w.-]+\/[\w.-]+#\d+\b|github\.com\/[\w.-]+\/[\w.-]+\/(?:issues|pull)\/\d+\b/;

function footerBlock(raw) {
  const normalized = (raw || '').replace(/\r\n/g, '\n').trim();
  const blocks = normalized.split(/\n[ \t]*\n/);
  if (blocks.length < 2) {
    return '';
  }
  return blocks[blocks.length - 1];
}

function refsFooter(parsed) {
  const lines = footerBlock(parsed.raw)
    .split('\n')
    .map((line) => line.trim())
    .filter(Boolean);
  const refs = lines.filter((line) => line.startsWith('Refs:'));
  if (refs.length === 0) {
    return [
      false,
      'commit body must include a Refs: footer naming the card, for example "Refs: #24"',
    ];
  }
  for (const line of refs) {
    const value = line.replace(/^Refs:[ \t]+/, '');
    if (value === line || !CARD.test(value)) {
      return [false, 'Refs: must name the card, for example "Refs: #24"'];
    }
  }
  return [true];
}

function breakingMarkAndFooter(parsed) {
  const header = (parsed.raw || '').replace(/\r\n/g, '\n').split('\n')[0].trim();
  const bang = /^[a-z]+(?:\([^)\s]+\))?!:\s+\S/.test(header);
  const footer = footerBlock(parsed.raw)
    .split('\n')
    .some((line) => /^BREAKING CHANGE:[ \t]+\S/.test(line.trim()));
  if (bang === footer) {
    return [true];
  }
  if (bang) {
    return [false, 'a breaking "!" in the title requires a BREAKING CHANGE: footer'];
  }
  return [false, 'a BREAKING CHANGE: footer requires a "!" after the type or scope in the title'];
}

module.exports = {
  extends: ['@commitlint/config-conventional'],
  // Inline the conventional-commits header so a breaking "!" stays inside the
  // type/scope/subject parse. The preset package no longer exposes parserOpts.
  parserPreset: {
    parserOpts: {
      headerPattern: /^(\w*)(?:\(([\w$.\-*/ ]*)\))?(!)?: (.*)$/,
      breakingHeaderPattern: /^(\w*)(?:\(([\w$.\-*/ ]*)\))?!: (.*)$/,
      headerCorrespondence: ['type', 'scope', 'breaking', 'subject'],
      noteKeywords: ['BREAKING CHANGE'],
      revertPattern: /^(?:Revert|revert:)\s"?([\s\S]+?)"?\s*This reverts commit (\w*)\./i,
      revertCorrespondence: ['header', 'hash'],
    },
  },
  defaultIgnores: false,
  rules: {
    'type-enum': [2, 'always', TYPES],
    'scope-enum': [2, 'always', SCOPES],
    'scope-empty': [2, 'never'],
    'body-max-line-length': [0],
    'footer-max-line-length': [0],
    'refs-footer': [2, 'always'],
    'breaking-mark-and-footer': [2, 'always'],
  },
  plugins: [
    {
      rules: {
        'refs-footer': refsFooter,
        'breaking-mark-and-footer': breakingMarkAndFooter,
      },
    },
  ],
  TYPES,
  SCOPES,
};
