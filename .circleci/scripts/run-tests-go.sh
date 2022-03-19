#!/bin/bash

set -euo pipefail

go version

pushd runtime-testsuite
    echo "running maven tests..."
    export MAVEN_OPTS="-Xmx2g"
    mvn -Dparallel=classes -DthreadCount=4 -Dtest=go.** test
popd
