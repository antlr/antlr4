#!/bin/bash

# linux specific setup, those setup have to be
# here since environment variables doesn't pass
# across scripts
if [ $TRAVIS_OS_NAME == "linux" ]; then
  export SWIFT_VERSION=swift-4.0
  export SWIFT_HOME=$(pwd)/swift/$SWIFT_VERSION-RELEASE-ubuntu14.04/usr/bin/
  export PATH=$SWIFT_HOME:$PATH

  # download swift
  mkdir swift
  curl https://swift.org/builds/$SWIFT_VERSION-release/ubuntu1404/$SWIFT_VERSION-RELEASE/$SWIFT_VERSION-RELEASE-ubuntu14.04.tar.gz -s | tar xz -C swift &> /dev/null
fi

# check swift
swift --version
swift build --version

if [ $GROUP == "LEXER" ]; then
    mvn -q -Dgroups="org.antlr.v4.test.runtime.category.LexerTests" -Dtest=swift.* test
elif [ $GROUP == "PARSER" ]; then
    mvn -q -Dgroups="org.antlr.v4.test.runtime.category.ParserTests" -Dtest=swift.* test
elif [ $GROUP == "RECURSION" ]; then
    mvn -q -Dgroups="org.antlr.v4.test.runtime.category.LeftRecursionTests" -Dtest=swift.* test
else
    mvn -q -Dtest=swift.* test
fi
