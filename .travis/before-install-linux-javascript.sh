#!/bin/bash

set -euo pipefail

sudo apt-get update -qq

## download and install nodejs
#curl -sL https://deb.nodesource.com/setup_14.x | sudo -E bash -
#sudo apt-get install -qq nodejs
#
## download and install nvm
#curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.36.0/install.sh | sudo -E bash -
#export NVM_DIR="$HOME/.nvm"
#[ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"  # This loads nvm
#
## use v14 and check
#sudo nvm use 14
node --version
