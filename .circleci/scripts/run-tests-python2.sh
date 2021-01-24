#!/bin/bash

set -euo pipefail

echo "updating..."
sudo apt-get update -y
echo "done updating"
echo "installing python..."
sudo apt-get install python2
echo "done installing python"
python2 --version

pushd runtime/Python2/tests
  python2 run.py
  rc=$?
  if [ $rc != 0 ]; then
    echo "failed running native tests"
  fi
popd

if [ $rc == 0 ]; then
  pushd runtime-test-suite
    mvn -q -Dtest=python2.* test
    rc=$?
  popd
fi

return $rc