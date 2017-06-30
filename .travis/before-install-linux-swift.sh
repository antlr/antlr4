set -euo pipefail

# make sure we use trusty repositories (travis by default uses precise)
curl https://repogen.simplylinux.ch/txt/trusty/sources_c4aa56bd26c0f54f391d8fae3e687ef5f6e97c26.txt | sudo tee /etc/apt/sources.list

# install dependencies
# some packages below will be update, swift assumes newer versions
# of, for example, sqlite3 and libicu, without the update some
# tools will not work
sudo apt-get update
sudo apt-get install clang libicu-dev libxml2 sqlite3

# This would fix a know linker issue mentioned in:
# https://bugs.swift.org/browse/SR-2299
sudo ln -sf ld.gold /usr/bin/ld
