#!/bin/bash

set -euo pipefail

dart --version

pushd runtime-testsuite
  echo "running maven tests..."
  mvn -Dparallel=classes -DthreadCount=4 -Dtest=dart.** test
popd
