#!/bin/bash

# Zip it
rm -f antlr4-cpp-runtime-source.zip
zip -r antlr4-cpp-runtime-source.zip "antlr4cpp.*" "antlrcpp-ios" "src" "CMakeLists.txt" "antlrcpp.xcodeproj" \
  -X --exclude "**/.DS_Store" "src/lib*" "antlrcpp.xcodeproj/xcuserdata/*"

# Deploy
cp antlr4-cpp-runtime-source.zip ~/antlr/sites/website-antlr4/download
