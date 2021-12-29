#!/bin/bash

set -euo pipefail

pushd runtime-testsuite/
  echo "running maven tests..."
  if [ $GROUP == "LEXER" ]; then
      mvn -Dparallel=classes -DthreadCount=4 -Dgroups="org.antlr.v4.test.runtime.category.LexerTests" -Dtest=csharp.** test
  elif [ $GROUP == "PARSER1" ]; then
      mvn -Dparallel=classes -DthreadCount=4 -Dgroups="org.antlr.v4.test.runtime.category.ParserTestsGroup1" -Dtest=csharp.** test
  elif [ $GROUP == "RECURSION" ]; then
      mvn -Dparallel=classes -DthreadCount=4 -Dgroups="org.antlr.v4.test.runtime.category.LeftRecursionTests" -Dtest=csharp.** test
  else
      mvn -Dparallel=classes -DthreadCount=4 -Dtest=csharp.** test
  fi
popd
