#!/bin/bash

set -euo pipefail

php -v

composer install -d runtime/PHP

php_path=$(which php)
pushd runtime-testsuite
  echo "running maven tests..."
  mvn -q -DPHP_PATH="${php_path}" -Dparallel=methods -DthreadCount=4 -Dtest=php.* test
popd
