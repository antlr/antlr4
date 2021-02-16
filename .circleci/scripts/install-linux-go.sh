#!/bin/bash

set -euo pipefail

echo "installing go SDK..."
sudo apt update
sudo apt install golang-go
go version
echo "done installing go SDK"