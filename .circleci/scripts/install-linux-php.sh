#!/bin/bash

set -euo pipefail

sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 3FA7E0328081BFF6A14DA29AA6A19B38D3D831EF
sudo apt-get update -qq

sudo apt update

sudo apt install lsb-release ca-certificates apt-transport-https software-properties-common -y
sudo add-apt-repository ppa:ondrej/php -y
sudo apt install php8.0-gd php8.0-xml php8.0-soap php8.0-mbstring
php -v

sudo apt install composer

git clone https://github.com/antlr/antlr-php-runtime.git runtime/PHP
composer install -d runtime/PHP

mvn install -DskipTests=true -Dmaven.javadoc.skip=true -B -V