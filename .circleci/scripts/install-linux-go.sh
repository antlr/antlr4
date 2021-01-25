#!/bin/bash

set -euo pipefail

echo "installing go runtime..."
echo "creating bin dir..."
mkdir ~/bin
export PATH=$PATH:~/bin
echo "fetching gimme..."
curl -sL -o /tmp/gimme https://raw.githubusercontent.com/travis-ci/gimme/master/gimme
sudo mv /tmp/gimme ~/bin/gimme
sudo chmod +x ~/bin/gimme
echo "installing go using gimme..."
sudo ~/bin/gimme 1.7.3
echo "done installing go using gimme..."
go version
go env
echo "done installing go runtime"