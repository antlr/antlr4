set -euo pipefail

echo "installing clang..."
sudo apt-get update
sudo apt-get install -y libxml2
sudo apt-get install -y clang-3.6
# sudo update-alternatives --install /usr/bin/clang clang /usr/bin/clang-3.6 100
ls /usr/bin/

# This would fix a know linker issue mentioned in:
# https://bugs.swift.org/browse/SR-2299
sudo ln -sf ld.gold /usr/bin/ld
echo "done installing clang..."

echo "installing swift SDK..."
export SWIFT_VERSION=swift-5.0.1
export SWIFT_HOME=$(pwd)/swift/$SWIFT_VERSION-RELEASE-ubuntu16.04/usr/bin/
export PATH=$SWIFT_HOME:$PATH
# download swift
mkdir swift
curl https://swift.org/builds/$SWIFT_VERSION-release/ubuntu1604/$SWIFT_VERSION-RELEASE/$SWIFT_VERSION-RELEASE-ubuntu16.04.tar.gz -s | tar xz -C swift &> /dev/null
echo "done installing swift SDK..."

# check swift
swift --version
swift build --version
