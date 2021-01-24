#!/bin/bash

set -euo pipefail

python2 --version

pushd runtime/Python2/tests
  echo "running native tests..."
  python2 run.py
  rc=$?
  if [ $rc != 0 ]; then
    echo "failed running native tests"
  fi
popd

if [ $rc == 0 ]; then
  pushd runtime-testsuite
    echo "running maven tests..."
    mvn -q -Dtest=python2.* test
    rc=$?
  popd
fi

# return $rc