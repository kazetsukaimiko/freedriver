#!/usr/bin/env bash
# Set the reactor to the version tag and deploy jsonlink (plus the reactor
# modules its POM needs) to GitHub Packages on kazetsukaimiko/freedriver.
set -euo pipefail

tag=${1:?version tag}

if [[ ! "$tag" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
  echo "tag ${tag} is not MAJOR.MINOR.PATCH" >&2
  exit 1
fi

if [[ "${DRY_RUN:-}" == "1" ]]; then
  echo "set Maven version ${tag} and deploy -pl jsonlink -am"
  exit 0
fi

mvn --batch-mode org.codehaus.mojo:versions-maven-plugin:2.18.0:set \
  -DnewVersion="${tag}" \
  -DgenerateBackupPoms=false \
  -DprocessAllModules=true

mvn --batch-mode --no-transfer-progress \
  -pl jsonlink \
  -am \
  -Dmaven.test.skip=true \
  deploy
