#!/bin/bash

set -euo pipefail

export PATH=$PATH:/Users/travis/.dotnet

# we need to build the runtime before test run, since we used "--no-dependencies"
# when we call dotnet cli for restore and build, in order to speed up

dotnet build -c Release -f netstandard2.0 ../runtime/CSharp/src/Antlr4.csproj

# call test

if [ $GROUP == "LEXER" ]; then
    mvn -q -Dgroups="org.antlr.v4.test.runtime.category.LexerTests" -Dparallel=classes -DthreadCount=4 -Dtest=csharp.* test
elif [ $GROUP == "PARSER" ]; then
    mvn -q -Dgroups="org.antlr.v4.test.runtime.category.ParserTests" -Dparallel=classes -DthreadCount=4 -Dtest=csharp.* test
elif [ $GROUP == "RECURSION" ]; then
    mvn -q -Dgroups="org.antlr.v4.test.runtime.category.LeftRecursionTests" -Dparallel=classes -DthreadCount=4 -Dtest=csharp.* test
else
    mvn -q -Dparallel=methods -DthreadCount=4 -Dtest=csharp.* test
fi
