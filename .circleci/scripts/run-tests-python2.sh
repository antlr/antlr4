#!/bin/bash

set -euo pipefail

sudo apt-get update -y
apt-get install python2
python --version

mvn -q -Dparallel=methods -DthreadCount=4 -Dtest=python2.* test

cd ../runtime/Python2/tests

python run.py
