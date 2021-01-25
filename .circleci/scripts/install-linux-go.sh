#!/bin/bash

set -euo pipefail

echo "installing go runtime..."
sudo apt install golang-go
go version
echo "done installing go runtime"