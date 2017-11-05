#!/bin/bash

set -euo pipefail

cache_dir="$HOME/Library/Caches/Antlr4"
dotnet_url='https://download.microsoft.com/download/B/9/F/B9F1AF57-C14A-4670-9973-CDF47209B5BF/dotnet-dev-osx-x64.1.0.4.pkg'
dotnet_file=$(basename "$dotnet_url")
dotnet_shasum='63b5d99028cd8b2454736076106c96ba7d05f0fc'

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
