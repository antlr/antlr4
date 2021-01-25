#!/bin/bash

set -euo pipefail

echo "installing go runtime..."
echo "fetching gimme..."
mkdir ~/bin
export PATH="$PATH:~/bin"
curl -sL -o ~/bin/gimme https://raw.githubusercontent.com/travis-ci/gimme/master/gimme
chmod +x ~/bin/gimme
echo "installing go using gimme..."
eval "$(sudo gimme 1.7.3)"
echo "done installing go using gimme..."
go version
go env
echo "done installing go runtime"