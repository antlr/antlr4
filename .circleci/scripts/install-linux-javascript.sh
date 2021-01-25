#!/bin/bash

set -euo pipefail

# use v14 and check
echo "installing nodejs..."
curl -sL https://deb.nodesource.com/setup_14.x | sudo -E bash -
sudo apt-get install -y nodejs
echo node version: $(node --version)
echo "done installing nodejs..."

echo "installing javascript runtime..."
pushd runtime/JavaScript
  npm install
  npm link
popd
echo "done installing javascript runtime"
