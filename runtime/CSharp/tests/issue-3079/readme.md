# How to test Issue 3079

1) Build the Antlr Tool first.
2) `bash test.sh` in this directory.

NB: The CSharp runtime source is modified by the test.sh script to
change "debug = true;" for ParserATNSimulator.cs. There is no way to
change the value of the static readonly variable using System.Reflection
after the static initializer for the class has loaded. This is why
it is change in the source here for this test and only this test.
