#!/bin/bash

set -euo pipefail

pushd
  cd runtime-testsuite
  mvn -q -Dparallel=methods -DthreadCount=1 -Dtest=javascript.* test
popd