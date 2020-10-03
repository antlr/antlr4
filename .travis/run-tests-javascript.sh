#!/bin/bash

cd target/classes/JavaScript

npm install

cd ../../..

set -euo pipefail

mvn -q -Dparallel=methods -DthreadCount=1 -Dtest=javascript.* test
