#!/bin/bash

set -euo pipefail

pushd runtime-testsuite/
  echo "running maven tests..."
  if [ $GROUP == "LEXER" ]; then
      mvn -q -Dgroups="org.antlr.v4.test.runtime.category.LexerTests" -Dparallel=classes -DthreadCount=4 -Dtest=csharp.TestCompositeLexers test
  elif [ $GROUP == "PARSER" ]; then
      mvn -q -Dgroups="org.antlr.v4.test.runtime.category.ParserTests" -Dparallel=classes -DthreadCount=4 -Dtest=csharp.* test
  elif [ $GROUP == "RECURSION" ]; then
      mvn -q -Dgroups="org.antlr.v4.test.runtime.category.LeftRecursionTests" -Dparallel=classes -DthreadCount=4 -Dtest=csharp.* test
  else
      mvn -q -Dparallel=methods -DthreadCount=4 -Dtest=csharp.* test
  fi
popd
