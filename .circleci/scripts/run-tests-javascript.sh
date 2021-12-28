#!/bin/bash

set -euo pipefail

declare -i RESULT=0

pushd runtime/JavaScript

  echo "running jest tests..."
  yarn test
  RESULT+=$?

popd

pushd runtime-testsuite

  echo "running maven tests..."
  mvn -Dtest=javascript.** test
  RESULT+=$?

popd

exit $RESULT
