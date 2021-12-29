#!/bin/bash

set -euo pipefail

mvn -Dparallel=methods -DthreadCount=4 -Dtest=go.* test
