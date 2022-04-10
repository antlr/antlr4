#!/bin/bash

set -euo pipefail

php -v

php_path=$(which php)
pushd runtime-testsuite
  echo "running maven tests..."
  export MAVEN_OPTS="-Xmx8g"
  mvn -DPHP_PATH="${php_path}" -Dparallel=classes -DthreadCount=4 -Dtest=php.** test
popd
