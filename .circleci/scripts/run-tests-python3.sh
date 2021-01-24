#!/bin/bash

set -euo pipefail

python3 --version

pushd runtime/Python3/tests
  echo "running native tests..."
  python3 run.py
  rc=$?
  if [ $rc != 0 ]; then
    echo "failed running native tests"
  fi
popd

if [ $rc == 0 ]; then
  pushd runtime-testsuite
    echo "running maven tests..."
    mvn -q -Dtest=python3.* test
    rc=$?
  popd
fi

# return $rc