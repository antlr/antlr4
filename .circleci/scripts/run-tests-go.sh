#!/bin/bash

set -euo pipefail

go version

pushd runtime-testsuite
    echo "running maven tests..."
    export MAVEN_OPTS="-Xmx8g
    export PATH=$PATH:/usr/local/go/bin # for use on linux
    mvn -Dparallel=classes -DthreadCount=4 -Dtest=go.** test
popd
