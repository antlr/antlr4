set -euo pipefail

# download swift
mkdir swift
curl https://swift.org/builds/$SWIFT_VERSION-release/ubuntu1404/$SWIFT_VERSION-RELEASE/$SWIFT_VERSION-RELEASE-ubuntu14.04.tar.gz -s | tar xz -C swift &> /dev/null

# make sure we use trusty repositories (travis by default uses precise)
curl https://repogen.simplylinux.ch/txt/trusty/sources_c4aa56bd26c0f54f391d8fae3e687ef5f6e97c26.txt | sudo tee /etc/apt/sources.list

# install dependencies
# some packages below will be update, swift assumes newer versions
# of, for example, sqlite3 and libicu, without the update some
# tools will not work
sudo apt-get update
sudo apt-get install clang libicu-dev libxml2 sqlite3

# check swift
export SWIFT_HOME=$(pwd)/swift/$SWIFT_VERSION-RELEASE-ubuntu14.04/usr/bin/
export PATH=${SWIFT_HOME):$PATH
swift --version
swift build --version
