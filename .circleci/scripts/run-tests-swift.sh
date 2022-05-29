#!/bin/bash

# Appears to be unused

set -euo pipefail

# TODO: https://github.com/antlr/antlr4/issues/3521
#
# pushd runtime/Swift
#   echo "running native tests..."
#   ./boot.py --test
#   rc=$?
#   if [ $rc != 0 ]; then
#     echo "failed running native tests"
#   fi
# popd

pushd runtime-testsuite
  echo "running maven tests..."
  export MAVEN_OPTS="-Xmx8g"
  mvn -Dparallel=classes -DthreadCount=4 -Dtest=swift.** test
popd
