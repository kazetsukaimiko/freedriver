#!/usr/bin/env bash
# Install the pinned git-cliff binary into DEST (a directory).
set -euo pipefail

version=2.14.2
dest=${1:?destination directory}

arch=$(uname -m)
case "$arch" in
  x86_64) target=x86_64-unknown-linux-gnu ;;
  aarch64 | arm64) target=aarch64-unknown-linux-gnu ;;
  *)
    echo "unsupported architecture: $arch" >&2
    exit 1
    ;;
esac

case "$target" in
  x86_64-unknown-linux-gnu)
    sha512=26d1f7c8ea2400f2ccb0b2e7f321635f600b583bf4cd1f7afaa5e8c24068ad1088a2504e0a4d9f4333d4bb20a80329327d9462634343ee3af91e1a7fbdc6f18a
    ;;
  aarch64-unknown-linux-gnu)
    sha512=fd27768ea76fea8c3ba3f3247fef2f6604988b629f15e48bf40be5eaa08b8fef2e4087cc9c8af16bc522bc19cc369611fa7692101b513f63469ad58522708600
    ;;
esac

archive="git-cliff-${version}-${target}.tar.gz"
url="https://github.com/orhun/git-cliff/releases/download/v${version}/${archive}"
workdir=$(mktemp -d)
trap 'rm -rf "$workdir"' EXIT

curl -fsSL -o "$workdir/$archive" "$url"
actual=$(sha512sum "$workdir/$archive" | awk '{print $1}')
if [[ "$actual" != "$sha512" ]]; then
  echo "checksum mismatch for $archive" >&2
  exit 1
fi
tar -xzf "$workdir/$archive" -C "$workdir"
binary=$(find "$workdir" -type f -name git-cliff -print -quit)
if [[ -z "$binary" ]]; then
  echo "git-cliff binary missing from $archive" >&2
  exit 1
fi
mkdir -p "$dest"
install -m 0755 "$binary" "$dest/git-cliff"
