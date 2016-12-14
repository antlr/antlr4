# Functional Testing in ANTLR

## Introduction

As with any software also ANTLR has to test its own functionality, both in terms of correctness as well as stability. For this goal a number of unit tests exist that test the entire ANTLR functionality, from the tool itself (code generation) to all target runtimes and their generated recognizers.

Because ANTLR supports multiple target languages, the unit tests are broken into two groups: the unit tests that test the tool itself (in the folder `tool-testsuite`) and the unit tests that test the parser runtimes (in the folder `runtime-testsuite`).  The tool tests are straightforward because they are Java code testing Java code; see the section at the bottom of this file.

The runtime tests must be specified in a generic fashion to work across language targets. Furthermore, we must test the various targets from Java. This usually means Java launching processes to compile, say, C++ and run parsers.

# Testing tool and targets

In order to perform the tests on all target languages, you need to have the following language tools installed:

* mono (for the C# target, e.g. `brew install mono`)
* nodejs
* Python 2.7
* Python 3.5
* Go
* Swift 3 (via XCode 8+) tested currently only on macOS
* clang (for C++ target)

## Running the runtime tests in IntelliJ

A single test rig is sufficient to test all targets against all descriptors using the [junit parameterized tests](https://github.com/junit-team/junit4/wiki/parameterized-tests) mechanism. But, that is inconvenient because we often want to test just a single target or perhaps even just a single test within a single group of a single target. I have automatically generated a bunch of
[Target runtime test rigs](https://github.com/antlr/antlr4/tree/master/runtime-testsuite/test/org/antlr/v4/test/runtime) that allow developers such flexibility. For example, here are the Python3 test rigs in IntelliJ:

<img src=images/testrigs.png width=300>

And the result of testing the entire subdirectory:

<img src=images/python3-tests.png width=400>


## Running the runtime tests on command line

To run the tests and **install into local repository** `~/.m2/repository/org/antlr`, do this:

```bash
$ mvn install -DskipTests=true   # make sure all artifacts are visible on this machine
$ mvn install                    # now "do it with feeling"
...
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running org.antlr.v4.test.runtime.csharp.TestCompositeLexers
dir /var/folders/s1/h3qgww1x0ks3pb30l8t1wgd80000gn/T/TestCompositeLexers-1446068612451
Starting build /usr/bin/xbuild /p:Configuration=Release /var/folders/s1/h3qgww1x0ks3pb30l8t1wgd80000gn/T/TestCompositeLexers-1446068612451/Antlr4.Test.mono.csproj
dir /var/folders/s1/h3qgww1x0ks3pb30l8t1wgd80000gn/T/TestCompositeLexers-1446068615081
Starting build /usr/bin/xbuild /p:Configuration=Release /var/folders/s1/h3qgww1x0ks3pb30l8t1wgd80000gn/T/TestCompositeLexers-1446068615081/Antlr4.Test.mono.csproj
Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 3.451 sec
Running org.antlr.v4.test.runtime.csharp.TestCompositeParsers
dir /var/folders/s1/h3qgww1x0ks3pb30l8t1wgd80000gn/T/TestCompositeParsers-1446068615864
antlr reports warnings from [-visitor, -Dlanguage=CSharp, -o, /var/folders/s1/h3qgww1x0ks3pb30l8t1wgd80000gn/T/TestCompositeParsers-1446068615864, -lib, /var/folders/s1/h3qgww1x0ks3pb30l8t1wgd80000gn/T/TestCompositeParsers-1446068615864, -encoding, UTF-8, /var/folders/s1/h3qgww1x0ks3pb30l8t1wgd80000gn/T/TestCompositeParsers-1446068615864/M.g4]
...
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
[INFO] 
[INFO] ANTLR 4 ............................................ SUCCESS [  0.445 s]
[INFO] ANTLR 4 Runtime .................................... SUCCESS [  3.392 s]
[INFO] ANTLR 4 Tool ....................................... SUCCESS [  1.373 s]
[INFO] ANTLR 4 Maven plugin ............................... SUCCESS [  1.519 s]
[INFO] ANTLR 4 Runtime Test Annotations ................... SUCCESS [  0.086 s]
[INFO] ANTLR 4 Runtime Test Processors .................... SUCCESS [  0.014 s]
[INFO] ANTLR 4 Runtime Tests (2nd generation) ............. SUCCESS [06:39 min]
[INFO] ANTLR 4 Tool Tests ................................. SUCCESS [  6.922 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 06:53 min
[INFO] Finished at: 2016-11-16T15:36:56-08:00
[INFO] Final Memory: 44M/458M
[INFO] ------------------------------------------------------------------------
```

Note: That is actually the result of running the much faster:

`mvn -Dparallel=methods -DthreadCount=4 install`


You should see these jars (when building 4.6-SNAPSHOT):

```bash
/Users/parrt/.m2/repository/org/antlr $ find antlr4* -name '*.jar'
antlr4-maven-plugin/4.6-SNAPSHOT/antlr4-maven-plugin-4.6-SNAPSHOT.jar
antlr4-runtime-test-annotation-processors/4.6-SNAPSHOT/antlr4-runtime-test-annotation-processors-4.6-SNAPSHOT.jar
antlr4-runtime-test-annotations/4.6-SNAPSHOT/antlr4-runtime-test-annotations-4.6-SNAPSHOT.jar
antlr4-runtime-testsuite/4.6-SNAPSHOT/antlr4-runtime-testsuite-4.6-SNAPSHOT-tests.jar
antlr4-runtime-testsuite/4.6-SNAPSHOT/antlr4-runtime-testsuite-4.6-SNAPSHOT.jar
antlr4-runtime/4.6-SNAPSHOT/antlr4-runtime-4.6-SNAPSHOT.jar
antlr4-tool-testsuite/4.6-SNAPSHOT/antlr4-tool-testsuite-4.6-SNAPSHOT.jar
antlr4/4.6-SNAPSHOT/antlr4-4.6-SNAPSHOT-tests.jar
antlr4/4.6-SNAPSHOT/antlr4-4.6-SNAPSHOT.jar
```

Note that ANTLR is written in itself, which is why maven downloads antlr4-4.5.jar for boostrapping 4.6-SNAPSHOT purposes.

## Running test subsets on the command line

> **IMPORTANT:** run all commands from the `runtime-testsuite` dir and watch the correct spelling of the `-Dtest` parameter. Language targets have to be specified in all lower case. Test names are case sensitive.

### Run one test group across targets

```bash
$ cd runtime-testsuite
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
$ mvn -Dtest=Test*Lexer* test
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running org.antlr.v4.test.runtime.cpp.TestCompositeLexers
...
```

### Run all tests for a single target

```bash
$ mvn -Dtest=java.* test
...
```

Or run all lexer related tests in Java target only:

```bash
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
$ mvn -Dparallel=methods -DthreadCount=4 test
...
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Concurrency config is parallel='methods', perCoreThreadCount=true, threadCount=4, useUnlimitedThreads=false
...
```

This can be combined with other `-D` above.

## Building without testing

To build without running the tests (saves a lot of time), do this:

```bash
mvn -DskipTests install
```

## Adding or Changing Tests

See the [Adding unit tests](adding-tests.md) document for details about how enhancing the test rig.
