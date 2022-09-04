#!/bin/bash

# Clean left overs from previous builds if there are any
rm -rf antlr4-runtime build lib
rm -f antlr4-cpp-runtime-macos.zip

# Binaries
cmake . -D CMAKE_OSX_ARCHITECTURES="arm64; x86_64" -DCMAKE_BUILD_TYPE=Release &> /dev/null
make -j 8

rm -rf lib
mkdir lib
cp runtime/libantlr4-runtime.dylib lib
cp runtime/libantlr4-runtime.a lib

# Headers
rm -f -R antlr4-runtime
pushd runtime/src
find . -name '*.h' | cpio -pdm ../../antlr4-runtime
popd

# Zip up and clean up
zip -r antlr4-cpp-runtime-macos.zip antlr4-runtime lib

rm -f -R antlr4-runtime build lib

# Deploy
#cp antlr4-cpp-runtime-macos.zip ~/antlr/sites/website-antlr4/download
