#!/bin/bash

set -euo pipefail

python2.7 --version

mvn -q -Dparallel=methods -DthreadCount=4 -Dtest=python2.* test

cd ../runtime/Python2/tests

python2.7 run.py
