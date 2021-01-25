#!/bin/bash

set -euo pipefail

pushd runtime-testsuite
  echo "running maven tests..."
  mvn -q -Dparallel=methods -DthreadCount=1 -Dtest=javascript.* test
popd