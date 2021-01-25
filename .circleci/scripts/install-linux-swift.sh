set -euo pipefail

echo "installing swift SDK..."
sudo apt-get update
sudo apt-get install clang libicu-dev
sudo apt-get install libcurl3 libpython2.7 libpython2.7-dev
wget -q -O - https://swift.org/keys/all-keys.asc | sudo gpg --import -

export SWIFT_VERSION=swift-5.0.1

wget https://swift.org/builds/$SWIFT_VERSION-release/ubuntu1604/$SWIFT_VERSION-RELEASE/$SWIFT_VERSION-RELEASE-ubuntu16.04.tar.gz
sudo tar xzf swift-4.0.3-RELEASE-ubuntu16.04.tar.gz
mv swift-4.0.3-RELEASE-ubuntu16.04 $PWD/swift

export SWIFT_HOME=$PWD/swift/$SWIFT_VERSION-RELEASE-ubuntu16.04/usr/bin/
export PATH=$PWD/swift/usr/bin:$PATH

# This would fix a know linker issue mentioned in:
# https://bugs.swift.org/browse/SR-2299
sudo ln -sf ld.gold /usr/bin/ld

echo "done installing swift SDK..."

# check swift
swift --version
swift build --version
