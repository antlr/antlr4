#!/bin/bash

set -euo pipefail
mvn -q -Dparallel=classes -DthreadCount=4 -Dtest=dart.* test
