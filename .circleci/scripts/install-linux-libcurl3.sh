set -euo pipefail

ls -all /lib/x86_64-linux-gnu/ || grep libcurl

# This would fix missing CURL_OPENSSL_3
mkdir ~/libcurl3
cd ~/libcurl3
wget http://archive.ubuntu.com/ubuntu/pool/main/c/curl/libcurl3_7.47.0-1ubuntu2_amd64.deb
ar x libcurl3* data.tar.xz
tar xf data.tar.xz
sudo cp -L ~/libcurl3/usr/lib/x86_64-linux-gnu/libcurl.so.4 /usr/lib/libcurl.so.3
cd ..
sudo rm -rf ~/libcurl3
