#!/bin/bash

set -euo pipefail

python3 --version

mvn -Dparallel=classes -DthreadCount=4 -Dtest=python3.* test

cd ../runtime/Python3/test

python3 run.py
