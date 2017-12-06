#!/bin/bash

set -euo pipefail

cache_dir="$HOME/Library/Caches/Antlr4"
dotnet_url='https://download.microsoft.com/download/F/4/F/F4FCB6EC-5F05-4DF8-822C-FF013DF1B17F/dotnet-dev-osx-x64.1.1.4.pkg'
dotnet_file=$(basename "$dotnet_url")
dotnet_shasum='dc46d93716db8bea8cc3c668088cc9e39384b5a4'

thisdir=$(dirname "$0")

# OpenSSL setup for dotnet core
mkdir -p /usr/local/lib
ln -s /usr/local/opt/openssl/lib/libcrypto.1.0.0.dylib /usr/local/lib/
ln -s /usr/local/opt/openssl/lib/libssl.1.0.0.dylib /usr/local/lib/

# download dotnet core
mkdir -p "$cache_dir"
(cd "$cache_dir"
 if [ -f "$dotnet_file" ]
 then
     if ! shasum -s -c <<<"$dotnet_shasum  $dotnet_file"
     then
         rm -f "$dotnet_file"
     fi
 fi
 if ! [ -f "$dotnet_file" ]
 then
     curl "$dotnet_url" -o "$dotnet_file"
 fi
)

# install dotnet core
sudo installer -pkg "$cache_dir/$dotnet_file" -target /

# make the link
ln -s /usr/local/share/dotnet/dotnet /usr/local/bin/
