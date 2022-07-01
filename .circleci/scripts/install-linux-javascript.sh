#!/bin/bash

# use v14 and check
echo "installing nodejs..."
curl -sL https://deb.nodesource.com/setup_14.x | sudo -E bash -
sudo apt-get install -y nodejs
echo node version: $(node --version)
echo "done installing nodejs"

echo "packaging javascript runtime..."
pushd runtime/JavaScript || exit
  sudo npm install
  sudo npm link
popd || exit
echo "done packaging javascript runtime"
