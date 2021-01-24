#!/bin/bash

set -euo pipefail

echo "installing go..."
sudo apt-get update
sudo apt-get install gccgo-go=1.7.3
go version
go env
echo "done installing go"