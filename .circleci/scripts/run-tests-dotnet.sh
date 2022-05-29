#!/bin/bash

set -euo pipefail

pushd runtime-testsuite
export MAVEN_OPTS="-Xmx8g"
mvn -Dparallel=classes -DthreadCount=4 -Dtest=csharp.** test
popd
