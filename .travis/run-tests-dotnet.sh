#!/bin/bash

mvn -q -Dparallel=methods -DthreadCount=4 -Dtest=csharp.* -Dantlr-csharp-netstandard=true test

