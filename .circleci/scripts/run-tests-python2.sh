#!/bin/bash

set -euo pipefail

echo "updating..."
sudo apt-get update -y
echo "done updating"
echo "installing python..."
sudo apt-get install python2
echo "done installing python"
python2 --version

mvn -q -Dparallel=methods -DthreadCount=4 -Dtest=python2.* test

echo $PWD
pushd runtime/Python2/tests
  python2 run.py
  rc=$?
popd

return $rc