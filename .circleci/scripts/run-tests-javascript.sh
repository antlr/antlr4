#!/bin/bash

set -euo pipefail

pushd runtime-testsuite
  echo "running maven tests..."
  mvn -q -Dtest=javascript.* test
popd