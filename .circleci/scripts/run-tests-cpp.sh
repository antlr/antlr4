#!/bin/bash

set -euo pipefail

pushd runtime-testsuite
  echo "running maven tests..."
  if [ $GROUP == "LEXER" ]; then
      mvn -q -Dgroups="org.antlr.v4.test.runtime.category.LexerTests" -Dtest=cpp.TestCompositeLexer test
  elif [ $GROUP == "PARSER" ]; then
      mvn -q -Dgroups="org.antlr.v4.test.runtime.category.ParserTests" -Dtest=cpp.* test
  elif [ $GROUP == "RECURSION" ]; then
      mvn -q -Dgroups="org.antlr.v4.test.runtime.category.LeftRecursionTests" -Dtest=cpp.* test
  else
      mvn -q -Dtest=cpp.* test
  fi
popd

