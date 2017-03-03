#!/bin/bash

set -euo pipefail

# install dotnet
sudo sh -c 'echo "deb [arch=amd64] https://apt-mo.trafficmanager.net/repos/dotnet-release/ trusty main" > /etc/apt/sources.list.d/dotnetdev.list'
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 417A0893
sudo apt-get update

sudo apt-get install dotnet-dev-1.0.0-preview2.1-003177

# install mvn
wget http://apache.mirrors.lucidnetworks.net/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz && \
    wget https://www.apache.org/dist/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz.md5 && \
    echo "$(cat apache-maven-3.3.9-bin.tar.gz.md5)  apache-maven-3.3.9-bin.tar.gz" > apache-maven-3.3.9-bin.tar.gz.md5 && \
    md5sum -c *.md5

sudo rm -rf /usr/local/maven/ && sudo mkdir -p /usr/local/maven && \
    sudo tar xzvf apache-maven-3.3.9-bin.tar.gz -C /usr/local/maven --strip-components=1

mvn -v
whereis mono
which mono
mono --version
