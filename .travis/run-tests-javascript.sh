#!/bin/bash

cd runtime/JavaScript

npm install

set -euo pipefail

mvn -q -Dparallel=methods -DthreadCount=1 -Dtest=javascript.* test
