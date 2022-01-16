#!/bin/bash

# Appears to be unused

set -euo pipefail

pushd runtime/Swift
  echo "running native tests..."
  ./boot.py --test
  rc=$?
  if [ $rc != 0 ]; then
    echo "failed running native tests"
  fi
popd


if [ $rc == 0 ]; then
  pushd runtime-testsuite
    echo "running maven tests..."
    mvn -Dparallel=classes -DthreadCount=4 -Dtest=swift.** test
  popd
fi
