#!/bin/bash

set -euo pipefail

pushd runtime/Cpp
ctest
popd

pushd runtime-testsuite
if [ $GROUP == "LEXER" ]; then
    mvn -Dgroups="org.antlr.v4.test.runtime.category.LexerTests" -Dtest=cpp.** test
elif [ $GROUP == "PARSER1" ]; then
    mvn -Dgroups="org.antlr.v4.test.runtime.category.ParserTestsGroup1" -Dtest=cpp.** test
elif [ $GROUP == "PARSER2" ]; then
    mvn -Dgroups="org.antlr.v4.test.runtime.category.ParserTestsGroup2" -Dtest=cpp.** test
elif [ $GROUP == "RECURSION" ]; then
    mvn -Dgroups="org.antlr.v4.test.runtime.category.LeftRecursionTests" -Dtest=cpp.** test
else
    mvn -Dtest=cpp.* test
fi
popd
