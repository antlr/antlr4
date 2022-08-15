#!/bin/bash

echo "installing go SDK..."
sudo apt update
sudo apt install snapd
curl -OL https://go.dev/dl/go1.19.linux-amd64.tar.gz
sudo tar -C /usr/local -xvf go1.19.linux-amd64.tar.gz
export PATH=$PATH:/usr/local/go/bin
go version
echo "done installing go SDK"
