#!/bin/bash

set -euo pipefail

# use v14 and check
echo "installing nodejs..."
sudo apt update
sudo apt install nodejs 14
sudo apt install npm
echo node version: $(node --version)
echo "done installing nodejs..."

echo "installing javascript runtime..."
pushd runtime/JavaScript
  npm install
  npm link
popd
echo "done installing javascript runtime"
