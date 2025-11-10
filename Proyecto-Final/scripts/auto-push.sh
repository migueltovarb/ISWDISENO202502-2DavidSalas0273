#!/bin/bash
set -euo pipefail
REPO_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )/.." && pwd )"
cd "$REPO_ROOT"
if ! git rev-parse --git-dir > /dev/null 2>&1; then
  echo "[auto-push] git repository not initialized"
  exit 0
fi
REMOTE_NAME="${GIT_AUTO_PUSH_REMOTE:-origin}"
BRANCH="${GIT_AUTO_PUSH_BRANCH:-$(git rev-parse --abbrev-ref HEAD 2>/dev/null || echo main)}"
MESSAGE="${GIT_AUTO_PUSH_MESSAGE:-auto}"
if ! git remote | grep -qx "$REMOTE_NAME"; then
  git remote add "$REMOTE_NAME" https://github.com/migueltovarb/ISWDISENO202502-2DavidSalas0273.git
fi
if git diff --quiet --ignore-submodules HEAD -- && git diff --cached --quiet --ignore-submodules; then
  echo "[auto-push] nothing to commit"
  exit 0
fi
set +e
GIT_TRACE=${GIT_TRACE:-0} git add -A
if ! git commit -m "$MESSAGE"; then
  echo "[auto-push] no staged changes; skipping commit"
fi
git push "$REMOTE_NAME" "$BRANCH"
