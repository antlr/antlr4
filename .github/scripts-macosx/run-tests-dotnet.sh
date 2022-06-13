#!/bin/bash

# Appears not to be used at moment

set -euo pipefail

export PATH=$PATH:~/.dotnet

# run tests
pushd runtime-testsuite/
export MAVEN_OPTS="-Xmx8g"
mvn -Dtest=csharp.** test
popd
