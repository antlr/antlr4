#!/bin/bash

set -euo pipefail

pushd runtime-testsuite
export MAVEN_OPTS="-Xmx8g"
mvn -Dtest=csharp.** test
popd
