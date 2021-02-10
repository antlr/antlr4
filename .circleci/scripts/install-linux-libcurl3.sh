#!/bin/bash

echo "before patching"
ls -all /lib/x86_64-linux-gnu/ | grep libcurl

# This would fix missing CURL_OPENSSL_3
# use a dedicated temp dir in the user space
mkdir ~/libcurl3
cd ~/libcurl3
# fetch latest libcurl3
wget http://archive.ubuntu.com/ubuntu/pool/main/c/curl/libcurl3_7.47.0-1ubuntu2_amd64.deb
# extract data.tar.xz
ar x libcurl3* data.tar.xz
# extract all from data.tar.xz
tar xf data.tar.xz
# copy libcurl.so.3 where required
sudo cp -L ~/libcurl3/usr/lib/x86_64-linux-gnu/libcurl.so.4.4.0 /lib/x86_64-linux-gnu/libcurl.so.4.4.0
sudo ln -sf libcurl.so.4.4.0 /lib/x86_64-linux-gnu/libcurl.so.4
cd ..
# drop dedicated temp dir
sudo rm -rf ~/libcurl3

echo "after patching"
ls -all /lib/x86_64-linux-gnu/ | grep libcurl
