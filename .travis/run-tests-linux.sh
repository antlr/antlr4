#!/bin/bash

mvn -Dtest=java.* test
mvn -Dtest=csharp.* test
mvn -Dtest=python2.* test
mvn -Dtest=python3.* test
mvn -Dtest=node.* test
mvn -Dtest=go.* test
mvn -Dtest=cpp.* test
