#!/bin/bash

set -euo pipefail

dart --version

pushd runtime-testsuite
  echo "running maven tests..."
  export MAVEN_OPTS="-Xmx8g"
  mvn -Dtest=dart.** test
popd
