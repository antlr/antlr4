#!/bin/bash

set -euo pipefail

pushd runtime-testsuite
mvn -Dparallel=classes -DthreadCount=4 -Dtest=cpp.** test
popd
