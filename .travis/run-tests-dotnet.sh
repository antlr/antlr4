#!/bin/bash

set -euo pipefail

# we need to build the runtime before test run, since we used "--no-dependencies"
# when we call dotnet cli for restore and build, in order to speed up

dotnet restore ../runtime/CSharp/runtime/CSharp/Antlr4.Runtime/Antlr4.Runtime.dotnet.csproj
dotnet build -c Release -f netstandard1.3 ../runtime/CSharp/runtime/CSharp/Antlr4.Runtime/Antlr4.Runtime.dotnet.csproj

# call test

if [ $GROUP == "LEXER" ]; then
    mvn -q -Dgroups="org.antlr.v4.test.runtime.category.LexerTests" -Dparallel=classes -DthreadCount=4 -Dtest=csharp.* -Dantlr-csharp-netstandard=true test
elif [ $GROUP == "PARSER" ]; then
    mvn -q -Dgroups="org.antlr.v4.test.runtime.category.ParserTests" -Dparallel=classes -DthreadCount=4 -Dtest=csharp.* -Dantlr-csharp-netstandard=true test
elif [ $GROUP == "RECURSION" ]; then
    mvn -q -Dgroups="org.antlr.v4.test.runtime.category.LeftRecursionTests" -Dparallel=classes -DthreadCount=4 -Dtest=csharp.* -Dantlr-csharp-netstandard=true test
else
    mvn -q -Dparallel=classes -DthreadCount=4 -Dtest=csharp.* -Dantlr-csharp-netstandard=true test
fi
