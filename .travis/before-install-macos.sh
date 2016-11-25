#!/bin/bash

set -euo pipefail

thisdir=$(dirname "$0")

brew update
brew install mono python3 cmake

# Work around apparent rvm bug that is in Travis's Xcode image.
# https://github.com/direnv/direnv/issues/210
# https://github.com/travis-ci/travis-ci/issues/6307
shell_session_update() { :; }

( go version ; go env ) || true
python --version
python3 --version
