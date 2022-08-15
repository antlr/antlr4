#!/bin/bash

echo "installing go SDK..."
sudo apt update
sudo apt install snapd
snap
sudo snap install --classic --channel=1.19/stable go
go version
echo "done installing go SDK"
