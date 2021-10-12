#!/bin/bash

set -euo pipefail

# use v14 and check
echo "installing nodejs..."
curl -sL https://deb.nodesource.com/setup_14.x | sudo -E bash -
sudo apt-get install -y nodejs
echo node version: $(node --version)
echo "done installing nodejs"

echo "installing yarn..."
sudo npm install -g yarn@v1.22.10
echo "done installing yarn"

echo "packaging javascript runtime..."
pushd runtime/JavaScript
  sudo npm install
  sudo npm link
popd
echo "done packaging javascript runtime"
