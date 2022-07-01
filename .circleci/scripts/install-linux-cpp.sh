#!/bin/bash

echo "installing cpp SDK..."

sudo apt-get update -y
sudo apt-get install -y clang
sudo apt-get install -y cmake
sudo apt-get install -y pkg-config
sudo apt-get install -y uuid-dev

echo "done installing cpp SDK"

clang++ --version
cmake --version

