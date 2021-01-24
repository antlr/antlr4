#!/bin/bash

set -euo pipefail

pushd runtime-testsuite
  echo "running maven tests..."
  mvn -q -Dparallel=classes -DthreadCount=4 -Dtest=dart.* test
popd
