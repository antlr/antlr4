#!/bin/bash

set -euo pipefail

echo "installing go runtime..."
pushd /tmp
  curl -sL -o gimme https://raw.githubusercontent.com/travis-ci/gimme/master/gimme
  chmod +x gimme
  eval "$(sudo gimme 1.7.3)"
popd
go version
go env
echo "done installing go runtime"