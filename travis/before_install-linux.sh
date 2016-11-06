#!/bin/bash

set -euo pipefail

thisdir=$(dirname $(readlink -f "$0"))

sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 3FA7E0328081BFF6A14DA29AA6A19B38D3D831EF
sudo add-apt-repository ppa:fkrull/deadsnakes -y
sudo add-apt-repository ppa:rwky/nodejs -y
sudo apt-get update -qq
sudo apt-get install -qq python3.5
sudo apt-get install -qq nodejs
echo "deb http://download.mono-project.com/repo/debian wheezy/snapshots/3.12.1 main" | sudo tee /etc/apt/sources.list.d/mono-xamarin.list
sudo apt-get install -qq mono-complete
eval "$(sudo gimme 1.7.3)"

"$thisdir/before_install-common.sh"
