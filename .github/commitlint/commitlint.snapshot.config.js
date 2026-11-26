// The rules for release-please's snapshot pull request: the same as
// commitlint.config.js with the Refs-footer rule off. lint-pr.js picks this
// file only for a pull request that the release App opened and that carries
// the `autorelease: snapshot` label.

const base = require('./commitlint.config.js');

module.exports = {
  ...base,
  rules: {
    ...base.rules,
    'refs-footer': [0],
  },
};
