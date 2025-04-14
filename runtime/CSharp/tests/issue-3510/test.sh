#!/usr/bin/bash

dotnet restore
dotnet build
dotnet run -file input.txt
if [[ "$?" != "0" ]]
then
    echo "Issue 3510 test failed."
    exit 1
fi

