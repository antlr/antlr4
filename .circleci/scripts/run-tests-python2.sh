#!/bin/bash

set -euo pipefail

python2 --version

# TODO: https://github.com/antlr/antlr4/issues/3521
#
# pushd runtime/Python2/tests
#   echo "running native tests..."
#   python2 run.py
#   rc=$?
#   if [ $rc != 0 ]; then
#     echo "failed running native tests"
#   fi
# popd

pushd runtime-testsuite
  echo "running maven tests..."
  export MAVEN_OPTS="-Xmx8g"
  mvn -Dparallel=classes -DthreadCount=4 -Dtest=python2.** test
popd