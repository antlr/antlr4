#!/bin/bash

set -euo pipefail

cd ../runtime/JavaScript
npm install
npm link
cd ../../runtime-testsuite
mvn -q -Dparallel=methods -DthreadCount=1 -Dtest=javascript.* test
