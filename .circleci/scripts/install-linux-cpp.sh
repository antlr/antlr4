#!/bin/bash

set -euo pipefail

echo "installing cpp SDK..."

sudo apt-get update -y
sudo apt-get install -y clang
sudo apt-get install -y cmake
sudo apt-get install -y pkg-config
sudo apt-get install -y uuid-dev

echo "done installing cpp SDK"

clang++ --version
cmake --version

echo "building cpp runtime..."

pushd "runtime/Cpp/"
  echo $PWD
  rc=0
  if [ $rc == 0 ]; then
    cmake . -DCMAKE_BUILD_TYPE=release
    rc=$?
  fi
  if [ $rc == 0 ]; then
    make -j 8
    rc=$?
  fi
popd


echo "done building cpp runtime"

