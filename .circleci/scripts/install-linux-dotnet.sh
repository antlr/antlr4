#!/bin/bash

set -euo pipefail

echo "installing .Net SDK..."
wget https://packages.microsoft.com/config/ubuntu/16.04/packages-microsoft-prod.deb -O packages-microsoft-prod.deb
sudo dpkg -i packages-microsoft-prod.deb
sudo apt-get update; \
  sudo apt-get install -y apt-transport-https && \
  sudo apt-get update && \
  sudo apt-get install -y dotnet-sdk-3.1
export PATH=$PATH:~/.dotnet
echo "done installing .Net SDK"

# we need to build the runtime before test run, since we used "--no-dependencies"
# when we call dotnet cli for restore and build, in order to speed up
echo "building runtime..."
dotnet build -c Release -f netstandard2.0 runtime/CSharp/src/Antlr4.csproj
echo "done building runtime"
