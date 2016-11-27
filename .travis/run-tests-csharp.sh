#!/bin/bash

mvn -q -Dparallel=methods -DthreadCount=4 -Dtest=csharp.* test
