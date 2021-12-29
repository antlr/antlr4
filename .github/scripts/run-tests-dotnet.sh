#!/bin/bash

set -euo pipefail

export PATH=$PATH:~/.dotnet

# we need to build the runtime before test run, since we used "--no-dependencies"
# when we call dotnet cli for restore and build, in order to speed up

dotnet build -c Release -f netstandard2.0 runtime/CSharp/Antlr4.csproj

# run tests
cd runtime-testsuite/

if [ $GROUP == "LEXER" ]; then
    mvn -Dgroups="org.antlr.v4.test.runtime.category.LexerTests" -Dtest=csharp.** test
elif [ $GROUP == "PARSER1" ]; then
    mvn -Dgroups="org.antlr.v4.test.runtime.category.ParserTestsGroup1" -Dtest=csharp.** test
elif [ $GROUP == "RECURSION" ]; then
    mvn -Dgroups="org.antlr.v4.test.runtime.category.LeftRecursionTests" -Dtest=csharp.** test
else
    mvn -Dtest=csharp.** test
fi
