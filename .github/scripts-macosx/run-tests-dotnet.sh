#!/bin/bash

# Appears not to be used at moment

set -euo pipefail

export PATH=$PATH:~/.dotnet

# we need to build the runtime before test run, since we used "--no-dependencies"
# when we call dotnet cli for restore and build, in order to speed up

dotnet build -c Release -f netstandard2.0 runtime/CSharp/Antlr4.csproj

# run tests
pushd runtime-testsuite/
export MAVEN_OPTS="-Xmx8g"
mvn -Dparallel=classes -DthreadCount=4 -Dtest=csharp.** test
popd
