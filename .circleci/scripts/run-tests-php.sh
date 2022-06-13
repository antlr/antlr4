#!/bin/bash

set -euo pipefail

php -v

php_path=$(which php)
pushd runtime-testsuite
  echo "running maven tests..."
  export MAVEN_OPTS="-Xmx8g"
  mvn -Dtest=php.** test
popd
