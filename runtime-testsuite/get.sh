#!/bin/sh

# Script we're using to get some of the test classes required for the other targets

pwd=`pwd`
dir="$pwd/test/org/antlr/v4/test/runtime"

# python2
wget https://raw.githubusercontent.com/antlr/antlr4-python2/master/tool/test/org/antlr/v4/test/runtime/python2/BasePython2Test.java -O $dir/python2/BasePython2Test.java

# python3
wget https://raw.githubusercontent.com/antlr/antlr4-python3/master/tool/test/org/antlr/v4/test/runtime/python3/BasePython3Test.java -O $dir/python3/BasePython3Test.java

mkdir -p $dir/python

wget https://raw.githubusercontent.com/antlr/antlr4-python3/master/tool/test/org/antlr/v4/test/runtime/python/BasePythonTest.java -O $dir/python/BasePythonTest.java

# javascript
wget https://raw.githubusercontent.com/antlr/antlr4-javascript/master/tool/test/org/antlr/v4/test/runtime/javascript/node/BaseTest.java -O $dir/javascript/node/BaseTest.java

# csharp
wget https://raw.githubusercontent.com/antlr/antlr4-csharp/master/tool/test/org/antlr/v4/test/runtime/csharp/BaseTest.java -O $dir/csharp/BaseTest.java
