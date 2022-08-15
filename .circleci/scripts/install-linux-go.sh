#!/bin/bash

set -euo pipefail

echo "installing go SDK..."
sudo apt update
sudo apt install snapd
sudo snap install --classic --channel=1.19/stable go
go version
echo "done installing go SDK"
