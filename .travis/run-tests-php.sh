#!/bin/bash

set -euo pipefail

php_path=$(which php)

composer install -d ../runtime/PHP

mvn -DPHP_PATH="${php_path}" -Dparallel=classes -DthreadCount=4 -Dtest=php.* test
