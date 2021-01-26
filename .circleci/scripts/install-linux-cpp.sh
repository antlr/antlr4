#!/bin/bash

set -euo pipefail

echo "installing cpp SDK..."

sudo apt-get update -y
sudo apt-get install clang++

echo "done installing cpp SDK..."

clang++ --version
