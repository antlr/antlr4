#!/bin/bash

set -euo pipefail

python --version

mvn -Dparallel=classes -DthreadCount=4 -Dtest=python2.* test

cd ../runtime/Python2/tests

python run.py
