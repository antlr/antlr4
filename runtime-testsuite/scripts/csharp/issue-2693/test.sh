#!/usr/bin/bash

files="/d/java-parser-test/android-sdk-sources-for-api-level-5/android/accounts/Account.java"

test() {
    dotnet-antlr -s compilationUnit $2
    dotnet restore Generated/Test.csproj
    dotnet build Generated/Test.csproj
    for i in $files
    do
        ./Generated/bin/Debug/net5.0/Test.exe -file $i >> o
    done
    rm -rf Generated
}

testj() {
    dotnet-antlr -s compilationUnit -t Java $2
    cd Generated
    ./build.sh
    for i in $files
    do
        cat $i | ./run.sh >> ../oj
    done
    cd ..
    rm -rf Generated
}

test after -x
testj after -x
