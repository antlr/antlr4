set -euo pipefail

# install dependencies
# some packages below will be update, swift assumes newer versions
# of, for example, sqlite3 and libicu, without the update some
# tools will not work
sudo apt-get update
sudo apt-get install clang-3.6 libxml2
sudo update-alternatives --install /usr/bin/clang clang /usr/bin/clang-3.6 100

# This would fix a know linker issue mentioned in:
# https://bugs.swift.org/browse/SR-2299
sudo ln -sf ld.gold /usr/bin/ld
