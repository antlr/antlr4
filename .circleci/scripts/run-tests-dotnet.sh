#!/bin/bash

set -euo pipefail

pushd runtime-testsuite/
  echo "running maven tests..."
  if [ $GROUP == "LEXER" ]; then
      mvn -Dgroups="org.antlr.v4.test.runtime.category.LexerTests" -Dtest=csharp.** test
  elif [ $GROUP == "PARSER1" ]; then
      mvn -Dgroups="org.antlr.v4.test.runtime.category.ParserTestsGroup1" -Dtest=csharp.** test
  elif [ $GROUP == "RECURSION" ]; then
      mvn -Dgroups="org.antlr.v4.test.runtime.category.LeftRecursionTests" -Dtest=csharp.** test
  else
      mvn -Dtest=csharp.** test
  fi
popd
