#!/bin/bash

set -euo pipefail

mvn -Dparallel=methods -DthreadCount=4 -Dtest=java.* test
cd ../tool-testsuite
mvn test
