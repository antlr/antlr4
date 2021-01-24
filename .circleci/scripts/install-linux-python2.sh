#!/bin/bash

set -euo pipefail

echo "updating..."
sudo apt-get update -y
echo "done updating"
echo "installing python 2..."
sudo apt-get install python2
echo "done installing python 2"
