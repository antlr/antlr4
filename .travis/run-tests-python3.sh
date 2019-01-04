#!/bin/bash

set -euo pipefail

mvn -q -Dparallel=methods -DthreadCount=4 -Dtest=python3.* test

cd ../runtime/Python3/test

python3.6 run.py