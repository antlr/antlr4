#!/bin/bash

set -euo pipefail

echo "installing go runtime..."
echo "fetching gimme..."
curl -sL -o /tmp/gimme https://raw.githubusercontent.com/travis-ci/gimme/master/gimme
chmod +x /tmp/gimme
echo "installing go using gimme..."
eval "$(sudo /tmp/gimme 1.7.3)"
echo "done installing go using gimme..."
go version
go env
echo "done installing go runtime"