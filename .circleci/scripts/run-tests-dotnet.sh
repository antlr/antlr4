#!/bin/bash

set -euo pipefail

pushd runtime-testsuite/
  echo "running maven tests..."
  if [ $GROUP == "LEXER" ]; then
      mvn -q -Dgroups="org.antlr.v4.test.runtime.category.LexerTests" -Dtest=csharp.* test
  elif [ $GROUP == "PARSER" ]; then
      mvn -q -Dgroups="org.antlr.v4.test.runtime.category.ParserTests" -Dtest=csharp.* test
  elif [ $GROUP == "RECURSION" ]; then
      mvn -q -Dgroups="org.antlr.v4.test.runtime.category.LeftRecursionTests" -Dtest=csharp.* test
  else
      mvn -q -Dtest=csharp.* test
  fi
popd
