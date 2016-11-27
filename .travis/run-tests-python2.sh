#!/bin/bash

mvn -Dparallel=methods -DthreadCount=4 -Dtest=python2.* test
