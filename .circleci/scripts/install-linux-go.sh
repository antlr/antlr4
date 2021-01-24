#!/bin/bash

set -euo pipefail

echo "installing gimme..."
sudo apt-get update
sudo apt-get install gimme
echo "installing go..."
eval "$(sudo gimme 1.7.3)"
( go version ; go env ) || true
echo "done installing go"