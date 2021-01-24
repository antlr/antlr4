#!/bin/bash

set -euo pipefail

echo "installing gimme..."
curl -sL -o ~/bin/gimme https://raw.githubusercontent.com/travis-ci/gimme/master/gimme
chmod +x ~/bin/gimme
echo "installing go..."
eval "$(sudo gimme 1.7.3)"
export PATH=$PATH:~/go/bin
go version
go env
echo "done installing go"