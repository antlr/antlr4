#!/bin/bash

set -euo pipefail

echo "updating..."
sudo apt-get update -y
echo "done updating"
echo "installing python..."
sudo apt-get install python2
echo "done installing python"
python --version

mvn -q -Dparallel=methods -DthreadCount=4 -Dtest=python2.* test

cd ../runtime/Python2/tests

python run.py
