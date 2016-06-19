#!/bin/bash

# Clean left overs from previous builds if there are any
rm -f -R antlr4-runtime build lib
rm antlr4-cpp-runtime-macos.zip

# Binaries
xcodebuild -project antlrcpp.xcodeproj -target antlr4 -configuration Release
xcodebuild -project antlrcpp.xcodeproj -target antlr4_static -configuration Release
rm -f -R lib
mkdir lib
mv build/Release/libantlr4-runtime.a lib/
mv build/Release/libantlr4-runtime.dylib lib/

# Headers
rm -f -R antlr4-runtime
pushd src
find . -name '*.h' | cpio -pdm ../antlr4-runtime
popd

# Zip up and clean up
zip -r antlr4-cpp-runtime-macos.zip antlr4-runtime lib

rm -f -R antlr4-runtime build lib

# Deploy
cp antlr4-cpp-runtime-macos.zip ~/antlr/sites/website-antlr4/download
