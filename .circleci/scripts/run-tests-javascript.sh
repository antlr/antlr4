#!/bin/bash

declare -i RESULT=0

pushd runtime/JavaScript || exit
  echo "running jasmine tests..."
  npm test
  RESULT+=$?
popd || exit

exit $RESULT
