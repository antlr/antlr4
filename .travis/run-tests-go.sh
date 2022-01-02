#!/bin/bash

set -euo pipefail

mvn -Dparallel=classes -DthreadCount=4 -Dtest=go.* test
