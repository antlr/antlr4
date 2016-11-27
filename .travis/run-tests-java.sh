#!/bin/bash

mvn -q -Dparallel=methods -DthreadCount=4 -Dtest=java.* test
cd ../tool-testsuite
mvn test
