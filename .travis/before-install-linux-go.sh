#!/bin/bash

set -euo pipefail

sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 3FA7E0328081BFF6A14DA29AA6A19B38D3D831EF
sudo add-apt-repository ppa:fkrull/deadsnakes -y
sudo add-apt-repository ppa:rwky/nodejs -y
sudo apt-get update -qq
eval "$(sudo gimme 1.7.3)"
( go version ; go env ) || true
