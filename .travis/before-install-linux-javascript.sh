#!/bin/bash

set -euo pipefail

sudo apt-get update -qq

curl -sL https://deb.nodesource.com/setup_14.x | sudo -E bash -
sudo apt-get install -qq nodejs
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.36.0/install.sh | sudo -E bash -
sudo nvm use 14
node --version
