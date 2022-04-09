#!/bin/bash

set -euo pipefail

sudo apt install software-properties-common
sudo add-apt-repository ppa:ondrej/php
sudo apt update

sudo apt install composer
composer self-update

sudo apt install php8.0
sudo apt install php8.0-mbstring
sudo apt install php8.0-xml
php -v


git clone https://github.com/antlr/antlr-php-runtime.git runtime/PHP
composer install -d runtime/PHP

mvn install -DskipTests=true -Dmaven.javadoc.skip=true -B -V