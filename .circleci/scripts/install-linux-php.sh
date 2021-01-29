#!/bin/bash

set -euo pipefail

sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 3FA7E0328081BFF6A14DA29AA6A19B38D3D831EF
sudo apt-get update -qq

php -v

git clone https://github.com/antlr/antlr-php-runtime.git
mvn install -DskipTests=true -Dmaven.javadoc.skip=true -B -V