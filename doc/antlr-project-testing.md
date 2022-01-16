# ANTLR project unit tests

## Introduction

Because ANTLR supports multiple target languages, the unit tests are broken into two groups: the unit tests that test the tool itself (in `tool-testsuite`) and the unit tests that test the parser runtimes (in `antlr4/runtime-testsuite`).  The tool tests are straightforward because they are Java code testing Java code; see the section at the bottom of this file.

The runtime tests must be specified in a generic fashion to work across language targets. Furthermore, we must test the various targets from Java. This usually means Java launching processes to compile, say, C++ and run parsers.

As of 4.10, we use a Java descriptor file held as an [UniversalRuntimeTestDescriptor.java object](https://github.com/antlr/antlr4/blob/master/runtime-testsuite/test/org/antlr/v4/test/runtime/UniversalRuntimeTestDescriptor.java) to represent each runtime test. Each test is described with a text file with various sections and resides in a group directory; see [directories under descriptors dir](https://github.com/antlr/antlr4/blob/master/runtime-testsuite/resources/org/antlr/v4/test/runtime/descriptors). Here is a sample test descriptor:

```
[notes]
This is a regression test for blah blah blah...

[type]
Parser

[grammar]
grammar T;
a : ID* {
<writeln("$text")>
};
ID : 'a'..'z'+;
WS : (' '|'\n') -> skip;

[start]
a

[input]
a b c

[output]
"""abc
"""
```

The grammars are strings representing StringTemplates (`ST` objects) so `<writeln("$text")>` will get replace when the unit test file is generated (`Test.java`, `Test.cs`, ...). The `writeln` template must be defined per target.  Here are all of the 
[Target templates for runtime tests](https://github.com/antlr/antlr4/tree/master/runtime-testsuite/resources/org/antlr/v4/test/runtime/templates).  Use triple-quotes `"""` when whitespace matters (usually input/output sections).

## Requirements

*out of date, at least for mono*

In order to perform the tests on all target languages, you need to have the following languages installed:

* `mono` (e.g., `brew install mono`) on non-Windows boxes (on Windows it uses the Microsoft .net stack). Also must [`xbuild` the runtime](https://github.com/antlr/antlr4/blob/master/doc/releasing-antlr.md) before tests will run; see below
* `nodejs`
* Python 2.7
* Python 3.6
* Go
* Swift (via XCode) tested currently only osx
* clang (for C++ target) 

To **install into local repository** `~/.m2/repository/org/antlr`, do this:

```bash
$ export MAVEN_OPTS="-Xmx1G"  # don't forget this on linux
$ mvn install -DskipTests     # make sure all artifacts are visible on this machine
```

Now, make sure C# runtime is built and installed locally.

```bash
cd ~/antlr/code/antlr4/runtime/CSharp/src
rm -rf `find . -name '{obj,bin}'`
dotnet build -c Release runtime/CSharp/src/Antlr4.csproj
```

C++ test rig automatically builds C++ runtime during tests. Others don't need a prebuilt lib.


## Running the runtime tests

A single test rig is sufficient to test all targets against all descriptors using the [junit parameterized tests](https://github.com/junit-team/junit4/wiki/parameterized-tests) mechanism. But, that is inconvenient because we often want to test just a single target or perhaps even just a single test within a single group of a single target. I have automatically generated a bunch of
[Target runtime test rigs](https://github.com/antlr/antlr4/tree/master/runtime-testsuite/test/org/antlr/v4/test/runtime) that allow developers such flexibility. For example, here are the Python3 test rigs in intellij:

<img src=images/testrigs.png width=300>

And the result of testing the entire subdirectory:

<img src=images/python3-tests.png width=400>

## Running test subsets

*From the `runtime-testsuite` dir*

### Run one test group across targets

```bash
$ cd runtime-testsuite
$ export MAVEN_OPTS="-Xmx1G"     # don't forget this on linux
$ mvn -Dtest=TestParserExec test
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running org.antlr.v4.test.runtime.cpp.TestParserExec
...
Tests run: 32, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 114.283 sec
Running org.antlr.v4.test.runtime.csharp.TestParserExec
...
```

Or run all lexer related tests:

```
$ cd runtime-testsuite
$ mvn -Dtest=Test*Lexer* test
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running org.antlr.v4.test.runtime.cpp.TestCompositeLexers
...
```

### Run all tests for a single target

```bash
$ cd runtime-testsuite
$ mvn -Dtest=java.** test
...
```

Or run all lexer related tests in Java target only:

```bash
$ cd runtime-testsuite
$ mvn -Dtest=java.*Lexer* test
...
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running org.antlr.v4.test.runtime.java.TestCompositeLexers
Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.277 sec
Running org.antlr.v4.test.runtime.java.TestLexerErrors
Tests run: 12, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.376 sec
Running org.antlr.v4.test.runtime.java.TestLexerExec
Tests run: 38, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 10.07 sec
Running org.antlr.v4.test.runtime.java.TestSemPredEvalLexer
Tests run: 7, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.255 sec

Results :

Tests run: 59, Failures: 0, Errors: 0, Skipped: 0
```

## Testing in parallel

Use this to run tests in parallel:

```bash
$ export MAVEN_OPTS="-Xmx1G"
$ mvn -Dparallel=classes -DthreadCount=4 test
...
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Concurrency config is parallel='classes', perCoreThreadCount=true, threadCount=4, useUnlimitedThreads=false
...
```

This can be combined with other `-D` above.

## Adding a runtime test

To add a new runtime test, first determine which [group (dir) of tests](https://github.com/antlr/antlr4/blob/master/runtime-testsuite/descriptors/org/antlr/v4/test/runtime/descriptors) it belongs to. Then, add a new descriptor file implementation by filling in one of these (omitting unused sections):

```
[notes]
                                
[type]

[grammar]

[slaveGrammar]

[start]

[input]

[output]

[errors]

[flags]

[skip]
```


Your best bet is to find a similar test in the appropriate group and then copy and paste the descriptor file, creating a new file within the test group dir. Modify the sections to suit your new problem.
 
### Ignoring tests

In order to turn off a test for a particular target, we need to use the `skip` section in the descriptor file. For example, the following skips PHP and Dart targets:

```
[skip]
PHP
Dart
```

### Target API/library testing

Some parts of the runtime API need to be tested with code written specifically in the target language. For example, you can see all of the Java runtime API tests here:

[https://github.com/antlr/antlr4/tree/master/runtime-testsuite/test/org/antlr/v4/test/runtime/java/api](https://github.com/antlr/antlr4/tree/master/runtime-testsuite/test/org/antlr/v4/test/runtime/java/api)

Notice that it is under an `api` dir. The directory above is where all of the `Test*` files go.

### Cross-language actions embedded within grammars

To get:

```
System.out.println($set.stop);
```

Use instead the language-neutral:

```
<writeln("$set.stop")>
```

Template file [runtime-testsuite/resources/org/antlr/v4/test/runtime/templates/Java.test.stg](https://github.com/antlr/antlr4/tree/master/runtime-testsuite/resources/org/antlr/v4/test/runtime/templates/Java.test.stg) has templates like:

```
writeln(s) ::= <<System.out.println(<s>);>>
```

that translate generic operations to target-specific language statements or expressions.

## Adding an ANTLR tool unit test

Just go into the appropriate Java test class in dir [antlr4/tool-testsuite/test/org/antlr/v4/test/tool](https://github.com/antlr/antlr4/tree/master/tool-testsuite/test/org/antlr/v4/test/tool) and add your unit test.


