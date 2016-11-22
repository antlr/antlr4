#!/bin/bash

mvn -Dparallel=methods -DthreadCount=4 -Dtest=java.* test
mvn -Dparallel=methods -DthreadCount=4 -Dtest=csharp.* test
mvn -Dparallel=methods -DthreadCount=4 -Dtest=python2.* test
mvn -Dparallel=methods -DthreadCount=4 -Dtest=python3.* test
mvn -Dparallel=methods -DthreadCount=4 -Dtest=node.* test
mvn -Dparallel=methods -DthreadCount=4 -Dtest=go.* test
mvn -Dtest=cpp.* test # timeout due to no output for 10 min on travis if in parallel
#mvn -Dparallel=methods -DthreadCount=4 -Dtest=swift.* test
