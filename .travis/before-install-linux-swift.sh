set -euo pipefail

# download swift
mkdir swift
curl https://swift.org/builds/$SWIFT_VERSION-release/ubuntu1404/$SWIFT_VERSION-RELEASE/$SWIFT_VERSION-RELEASE-ubuntu14.04.tar.gz -s | tar xz -C swift &> /dev/null

# install dependencies
sudo apt-get install clang libicu52

# update libstdc++6, travis uses some old version
echo "\n" | sudo add-apt-repository ppa:ubuntu-toolchain-r/test
sudo apt-get update
sudo apt-get install gcc-4.9
sudo apt-get install libstdc++6

# check swift
export PATH=$(pwd)/swift/$SWIFT_VERSION-RELEASE-ubuntu14.04/usr/bin:$PATH
swift --version
swift build --version
