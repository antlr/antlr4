#!/bin/bash

# Zip it
rm -f antlr4-cpp-runtime-source.zip
zip -r antlr4-cpp-runtime-source.zip "README.md" "cmake" "demo" "runtime" "CMakeLists.txt" "License.txt" "deploy-macos.sh" "deploy-source.sh" "deploy-windows.cmd" "VERSION" \
  -X -x "*.DS_Store*" "antlrcpp.xcodeproj/xcuserdata/*" "*Build*" "*DerivedData*" "*.jar" "demo/generated/*"

# Deploy
#cp antlr4-cpp-runtime-source.zip ~/antlr/sites/website-antlr4/download
