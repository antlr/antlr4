#!/bin/bash

# linux specific setup, those setup have to be
# here since environment variables doesn't pass
# across scripts
if [ $TRAVIS_OS_NAME == "linux" ]; then
  export SWIFT_VERSION=swift-3.1.1
  export SWIFT_HOME=$(pwd)/swift/$SWIFT_VERSION-RELEASE-ubuntu14.04/usr/bin/
  export PATH=$SWIFT_HOME:$PATH

  # download swift
  mkdir swift
  curl https://swift.org/builds/$SWIFT_VERSION-release/ubuntu1404/$SWIFT_VERSION-RELEASE/$SWIFT_VERSION-RELEASE-ubuntu14.04.tar.gz -s | tar xz -C swift &> /dev/null
fi

# check swift
swift --version
swift build --version

if [ $GROUP == "LEXER" ]; then GROUPS=-Dgroups="org.antlr.v4.test.runtime.category.LexerTests"
elif [ $GROUP == "PARSER" ]; then GROUPS=-Dgroups="org.antlr.v4.test.runtime.category.ParserTests"
elif [ $GROUP == "RECURSION" ]; then GROUPS=-Dgroups="org.antlr.v4.test.runtime.category.LeftRecursionTests"
else GROUPS=
fi

mvn -q $GROUPS -Dtest=swift.* test
