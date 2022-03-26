#!/bin/bash

set -euo pipefail

python3 --version

# TODO: https://github.com/antlr/antlr4/issues/3521
#
# pushd runtime/Python3/tests
#   echo "running native tests..."
#   python3 run.py
#   rc=$?
#   if [ $rc != 0 ]; then
#     echo "failed running native tests"
#   fi
# popd

pushd runtime-testsuite
  echo "running maven tests..."
  export MAVEN_OPTS="-Xmx8g"
  mvn -Dparallel=classes -DthreadCount=4 -Dtest=python3.** test
popd
