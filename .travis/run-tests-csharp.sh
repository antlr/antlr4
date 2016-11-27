#!/bin/bash

mvn -Dparallel=methods -DthreadCount=4 -Dtest=csharp.* test
