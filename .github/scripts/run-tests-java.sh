#!/bin/bash

set -euo pipefail

if [ -z "${JAVA_HOME-}" ]
then
  export JAVA_HOME="$(java -XshowSettings:properties -version 2>&1 |
                          grep 'java\.home' | awk '{ print $3 }')"
  echo "export JAVA_HOME=$JAVA_HOME"
fi

# run java tests
cd runtime-testsuite/

if [ $GROUP == "LEXER" ]; then
    mvn -X -e -q -Dgroups="org.antlr.v4.test.runtime.category.LexerTests" -Dtest="swift.*" test
elif [ $GROUP == "PARSER" ]; then
    mvn -q -Dgroups="org.antlr.v4.test.runtime.category.ParserTests" -Dtest="swift.*" test
elif [ $GROUP == "RECURSION" ]; then
    mvn -q -Dgroups="org.antlr.v4.test.runtime.category.LeftRecursionTests" -Dtest="swift.*" test
else
    mvn -q -Dtest="java.*" test
fi
rc=$?
cat target/surefire-reports/*.dumpstream || true
exit $rc
