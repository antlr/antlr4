#!/bin/bash

set -euo pipefail

pushd runtime-testsuite
mvn -Dparallel=classes -DthreadCount=4 -Dtest=cpp.** test
popd

#pushd runtime/Cpp
#ctest
#popd
#
#pushd runtime-testsuite
#if [ $GROUP == "LEXER" ]; then
#    mvn -Dparallel=classes -DthreadCount=1 -Dgroups="org.antlr.v4.test.runtime.category.LexerTests" -Dtest=cpp.** test
#elif [ $GROUP == "PARSER1" ]; then
#    mvn -Dparallel=classes -DthreadCount=1 -Dgroups="org.antlr.v4.test.runtime.category.ParserTestsGroup1" -Dtest=cpp.** test
#elif [ $GROUP == "PARSER2" ]; then
#    mvn -Dparallel=classes -DthreadCount=1 -Dgroups="org.antlr.v4.test.runtime.category.ParserTestsGroup2" -Dtest=cpp.** test
#elif [ $GROUP == "RECURSION" ]; then
#    mvn -Dparallel=classes -DthreadCount=1 -Dgroups="org.antlr.v4.test.runtime.category.LeftRecursionTests" -Dtest=cpp.** test
#else
#    mvn -Dparallel=classes -DthreadCount=4 -Dtest=cpp.** test
#fi
#popd
