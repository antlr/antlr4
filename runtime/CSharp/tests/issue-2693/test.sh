#!/usr/bin/bash

dotnet restore
dotnet build
dotnet run -file cpm22.asm
if [[ "$?" != "0" ]]
then
    echo "Issue 2693 test failed."
    exit 1
fi

