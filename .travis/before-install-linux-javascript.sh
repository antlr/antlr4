#!/bin/bash

set -euo pipefail

sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 3FA7E0328081BFF6A14DA29AA6A19B38D3D831EF
sudo apt-get update -qq
curl -sL https://deb.nodesource.com/setup_0.12 | sudo -E bash -
sudo apt-get install -qq nodejs
node --version
