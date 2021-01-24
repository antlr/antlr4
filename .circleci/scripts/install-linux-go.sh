#!/bin/bash

set -euo pipefail

echo "installing gimme..."
sudo curl -sL -o ~/bin/gimme https://raw.githubusercontent.com/travis-ci/gimme/master/gimme
sudo chmod +x ~/bin/gimme
echo "installing go..."
eval "$(sudo gimme 1.7.3)"
( go version ; go env ) || true
echo "done installing go"