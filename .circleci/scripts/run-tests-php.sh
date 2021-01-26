#!/bin/bash

set -euo pipefail

php_path=$(which php)

composer install -d ../runtime/PHP

mvn -q -DPHP_PATH="${php_path}" -Dparallel=methods -DthreadCount=4 -Dtest=php.* test
