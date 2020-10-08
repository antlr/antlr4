#!/bin/bash

set -euo pipefail

sudo apt-get update -qq
curl -sL https://deb.nodesource.com/setup_10.x | sudo -E bash -
sudo apt-get install -qq nodejs
node --version
