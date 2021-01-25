#!/bin/bash

set -euo pipefail

echo "installing go runtime..."
echo "fetching gimme..."
curl -sL -o /usr/bin/gimme https://raw.githubusercontent.com/travis-ci/gimme/master/gimme
chmod +x /usr/bin/gimme
echo "installing go using gimme..."
eval "$(sudo /usr/bin/gimme 1.7.3)"
echo "done installing go using gimme..."
go version
go env
echo "done installing go runtime"