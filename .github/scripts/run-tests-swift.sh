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
pushd runtime/Swift
./boot.py --test
rc=$?
popd

if [ $rc == 0 ]; then
  # run java tests
  cd runtime-testsuite/
  if [ $GROUP == "LEXER" ]; then
      mvn -e -q -Dgroups="org.antlr.v4.test.runtime.category.LexerTests" -Dtest="swift.*" test
  elif [ $GROUP == "PARSER" ]; then
      mvn -e -q -Dgroups="org.antlr.v4.test.runtime.category.ParserTests" -Dtest="swift.*" test
  elif [ $GROUP == "RECURSION" ]; then
      mvn -e -q -Dgroups="org.antlr.v4.test.runtime.category.LeftRecursionTests" -Dtest="swift.*" test
  else
      mvn -e -q -Dtest=swift.* test
  fi
  rc=$?
  cat target/surefire-reports/*.dumpstream || true
fi
exit $rc
