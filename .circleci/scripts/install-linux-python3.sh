#!/bin/bash

set -euo pipefail

echo "updating..."
sudo apt-get update -y
echo "done updating"
echo "installing python 3..."
sudo apt-get install python3
echo "done installing python 3"
