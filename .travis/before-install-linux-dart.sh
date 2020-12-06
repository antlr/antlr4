#!/bin/bash

set -euo pipefail
wget https://storage.googleapis.com/dart-archive/channels/stable/release/2.8.4/linux_packages/dart_2.8.4-1_amd64.deb
sudo dpkg -i ./dart_2.8.4-1_amd64.deb
sudo rm ./dart_2.8.4-1_amd64.deb
sudo apt-get install -f
