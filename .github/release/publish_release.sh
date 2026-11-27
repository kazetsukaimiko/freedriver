#!/usr/bin/env bash
# Publish one closed allowlist to GitHub Packages on a MAJOR.MINOR.PATCH tag.
# --package builds that same list and does not deploy.
set -euo pipefail

# One project list. The root is the parent POM. jsonlink-api is named
# here so the allowlist publishes the schema contract; -am would also
# build it once jsonlink depends on it, and -am adds the reactor
# dependencies serial-impl, serial-api, base, math, and inotify.
PROJECTS='.,bom,jakarta-bom,3p-bom,jsonlink-api,jsonlink,math-jpa,victron,victron-jpa,inotify-cdi,metrics'

if [[ "${1:-}" == "--package" ]]; then
  shift
  for arg in "$@"; do
    if [[ "$arg" != -* ]]; then
      echo "package mode takes no version argument" >&2
      exit 1
    fi
  done
  exec mvn --batch-mode --no-transfer-progress \
    package \
    -DskipTests \
    -pl "${PROJECTS}" \
    -am \
    "$@"
fi

tag=${1:?version tag}

if [[ ! "$tag" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
  echo "tag ${tag} is not MAJOR.MINOR.PATCH" >&2
  exit 1
fi

if [[ $# -ne 1 ]]; then
  echo "deploy takes only the version tag" >&2
  exit 1
fi

if [[ "${DRY_RUN:-}" == "1" ]]; then
  echo "mvn --batch-mode --no-transfer-progress deploy -pl ${PROJECTS} -am -DskipTests -DdeployAtEnd=true"
  exit 0
fi

mvn --batch-mode org.codehaus.mojo:versions-maven-plugin:2.18.0:set \
  -DnewVersion="${tag}" \
  -DgenerateBackupPoms=false \
  -DprocessAllModules=true

mvn --batch-mode --no-transfer-progress \
  deploy \
  -pl "${PROJECTS}" \
  -am \
  -DskipTests \
  -DdeployAtEnd=true
