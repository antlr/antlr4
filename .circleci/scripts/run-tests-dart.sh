#!/bin/bash

set -euo pipefail

dart --version

pushd runtime-testsuite
  echo "running maven tests..."
#  mvn -q -Dparallel=classes -DthreadCount=4 -Dtest=dart.* test
  mvn -q -Dtest=dart.* test
popd
