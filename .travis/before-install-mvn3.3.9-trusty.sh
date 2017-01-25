#!/bin/sh

set -ex

wget http://apache.mirrors.lucidnetworks.net/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz && \
    wget https://www.apache.org/dist/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz.md5 && \
    echo "$(cat apache-maven-3.3.9-bin.tar.gz.md5)  apache-maven-3.3.9-bin.tar.gz" > apache-maven-3.3.9-bin.tar.gz.md5 && \
    md5sum -c *.md5

sudo rm -rf /usr/local/maven/ && sudo mkdir -p /usr/local/maven && \
    sudo tar xzvf apache-maven-3.3.9-bin.tar.gz -C /usr/local/maven --strip-components=1

mvn -v

