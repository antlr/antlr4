#!/bin/bash

set -euo pipefail

mvn -Dparallel=classes -DthreadCount=4 -Dtest=java.* test
cd ../tool-testsuite
mvn test
