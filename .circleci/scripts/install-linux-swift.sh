#!/bin/bash

echo "installing swift SDK..."

sudo apt update && sudo apt upgrade
sudo apt install binutils git gnupg2 libc6-dev libcurl4 libedit2 libgcc-9-dev libpython2.7 libsqlite3-0 libstdc++-9-dev libxml2 libz3-dev pkg-config tzdata zlib1g-dev

wget https://download.swift.org/swift-5.6.2-release/ubuntu2004/swift-5.6.2-RELEASE/swift-5.6.2-RELEASE-ubuntu20.04.tar.gz

tar xzf swift-5.6.2-RELEASE-ubuntu20.04.tar.gz

sudo mv swift-5.6.2-RELEASE-ubuntu20.04 /usr/share/swift

export PATH=$PATH:/usr/share/swift/usr/bin

echo "done installing swift SDK..."

# check swift
swift --version
