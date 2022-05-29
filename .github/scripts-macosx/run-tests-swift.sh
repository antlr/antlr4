#!/bin/bash

set -euo pipefail

# linux specific setup, those setup have to be
# here since environment variables doesn't pass
# across scripts
if [ $RUNNER_OS == "Linux" ]; then
  export SWIFT_VERSION=swift-5.0.1
  export SWIFT_HOME=$(pwd)/swift/$SWIFT_VERSION-RELEASE-ubuntu16.04/usr/bin/
  export PATH=$SWIFT_HOME:$PATH

  # download swift
  mkdir swift
  curl https://swift.org/builds/$SWIFT_VERSION-release/ubuntu1604/$SWIFT_VERSION-RELEASE/$SWIFT_VERSION-RELEASE-ubuntu16.04.tar.gz -s | tar xz -C swift &> /dev/null
fi

if [ -z "${JAVA_HOME}" ]
then
  export JAVA_HOME="$(java -XshowSettings:properties -version 2>&1 |
                          grep 'java\.home' | awk '{ print $3 }')"
fi
echo "export JAVA_HOME=$JAVA_HOME"

# check swift
swift --version
swift build --version

# run swift tests

# TODO: https://github.com/antlr/antlr4/issues/3521
# pushd runtime/Swift
# ./boot.py --test
# rc=$?
# popd

# run java tests
cd runtime-testsuite/
#  mvn -e -Dparallel=classes -DthreadCount=4 -Dtest=swift.** test
# I don't know swift enough to make it parallel. revert to single threaded
export MAVEN_OPTS="-Xmx8g"
mvn -e -Dtest=swift.** test
rc=$?
cat target/surefire-reports/*.dumpstream || true

exit $rc
