#!/bin/bash

set -euo pipefail

echo "installing swift SDK..."

.circleci/scripts/install-linux-libcurl3.sh

# see https://tecadmin.net/install-swift-ubuntu-1604-xenial/
sudo apt-get update -y
sudo apt-get install clang libicu-dev
sudo apt-get install libpython2.7 libpython2.7-dev

export SWIFT_VERSION=swift-5.3.2
echo "installing gpg key..."
wget -q -O - https://swift.org/keys/all-keys.asc | sudo gpg --import -
echo "downloading SDK gpg key..."
SWIFT_SDK=https://swift.org/builds/$SWIFT_VERSION-release/ubuntu1604/$SWIFT_VERSION-RELEASE/$SWIFT_VERSION-RELEASE-ubuntu16.04.tar.gz
echo $SWIFT_SDK
wget -q $SWIFT_SDK
sudo tar xzf $SWIFT_VERSION-RELEASE-ubuntu16.04.tar.gz
mv $SWIFT_VERSION-RELEASE-ubuntu16.04 $PWD/swift

export SWIFT_HOME=$PWD/swift/$SWIFT_VERSION-RELEASE-ubuntu16.04/usr/bin/
export PATH=$PWD/swift/usr/bin:$PATH

# This would fix a know linker issue mentioned in: # https://bugs.swift.org/browse/SR-2299
sudo ln -sf ld.gold /usr/bin/ld
# This would fix missing libtinfo.so.5
sudo apt install libncurses5

echo "done installing swift SDK..."

# check swift
swift --version
swift build --version
