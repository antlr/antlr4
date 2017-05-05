#!/bin/bash

set -euo pipefail

sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 3FA7E0328081BFF6A14DA29AA6A19B38D3D831EF
sudo apt-get update -qq
echo "deb http://download.mono-project.com/repo/debian wheezy/snapshots/3.12.1 main" | sudo tee /etc/apt/sources.list.d/mono-xamarin.list
sudo apt-get install -qq mono-complete
