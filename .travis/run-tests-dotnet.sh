#!/bin/bash

mvn -q -Dparallel=classes -DthreadCount=4 -Dtest=csharp.* -Dantlr-csharp-netstandard=true test

