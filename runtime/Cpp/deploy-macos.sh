#!/bin/bash

# Clean left overs from previous builds if there are any
rm -f -R antlr4-runtime build lib 2> /dev/null
rm antlr4-cpp-runtime-macos.zip 2> /dev/null

# Get utf8 dependency.
mkdir -p runtime/thirdparty 2> /dev/null
pushd runtime/thirdparty
if [ ! -d utfcpp ]
then
    git clone https://github.com/nemtrif/utfcpp.git utfcpp
    pushd utfcpp
    git checkout tags/v3.1.1
    popd
fi
popd

# Binaries
xcodebuild -project runtime/antlrcpp.xcodeproj \
           -target antlr4                      \
           # GCC_PREPROCESSOR_DEFINITIONS='$GCC_PREPROCESSOR_DEFINITIONS USE_UTF8_INSTEAD_OF_CODECVT' \
           -configuration Release
xcodebuild -project runtime/antlrcpp.xcodeproj \
           -target antlr4_static               \
           # GCC_PREPROCESSOR_DEFINITIONS='$GCC_PREPROCESSOR_DEFINITIONS USE_UTF8_INSTEAD_OF_CODECVT' \
           -configuration Release
rm -f -R lib
mkdir lib
mv runtime/build/Release/libantlr4-runtime.a lib/
mv runtime/build/Release/libantlr4-runtime.dylib lib/

# Headers
rm -f -R antlr4-runtime
pushd runtime/src
find . -name '*.h' | cpio -pdm ../../antlr4-runtime
popd
pushd runtime/thirdparty/utfcpp/source
find . -name '*.h' | cpio -pdm ../../../../antlr4-runtime
popd

# Zip up and clean up
zip -r antlr4-cpp-runtime-macos.zip antlr4-runtime lib

rm -f -R antlr4-runtime build lib

# Deploy
#cp antlr4-cpp-runtime-macos.zip ~/antlr/sites/website-antlr4/download
