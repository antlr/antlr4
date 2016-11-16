# Building ANTLR

Most programmers do not need the information on this page because they will simply download the appropriate jar(s) or use ANTLR through maven (via ANTLR's antlr4-maven-plugin). If you would like to fork the project and fix bugs or tweak the runtime code generation, then you will almost certainly need to build ANTLR itself. There are two components:

 1. the tool that compiles grammars down into parsers and lexers in one of the target languages
 1. the runtime used by those generated parsers and lexers.

I will assume that the root directory is `/tmp` for the purposes of explaining how to build ANTLR in this document.

# Get the source

The first step is to get the Java source code from the ANTLR 4 repository at github. You can download the repository from github, but the easiest thing to do is simply clone the repository on your local disk:

```bash
$ cd /tmp
/tmp $ git clone https://github.com/antlr/antlr4.git
Cloning into 'antlr4'...
remote: Counting objects: 61480, done.
remote: Total 61480 (delta 0), reused 0 (delta 0), pack-reused 61480
Receiving objects: 100% (61480/61480), 31.24 MiB | 7.18 MiB/s, done.
Resolving deltas: 100% (32970/32970), done.
Checking connectivity... done.
Checking out files: 100% (1427/1427), done.
```

# Compile

```bash
$ cd /tmp
$ git clone git@github.com:antlr/antlr4.git
Cloning into 'antlr4'...
remote: Counting objects: 59858, done.
remote: Compressing objects: 100% (57/57), done.
remote: Total 59858 (delta 28), reused 9 (delta 9), pack-reused 59786
Receiving objects: 100% (59858/59858), 31.10 MiB | 819.00 KiB/s, done.
Resolving deltas: 100% (31898/31898), done.
Checking connectivity... done.
$ cd antlr4
$ mvn compile
..
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
[INFO] 
[INFO] ANTLR 4 ............................................ SUCCESS [  0.432 s]
[INFO] ANTLR 4 Runtime .................................... SUCCESS [  4.334 s]
[INFO] ANTLR 4 Tool ....................................... SUCCESS [  1.686 s]
[INFO] ANTLR 4 Maven plugin ............................... SUCCESS [  1.654 s]
[INFO] ANTLR 4 Runtime Test Annotations ................... SUCCESS [  0.096 s]
[INFO] ANTLR 4 Runtime Test Processors .................... SUCCESS [  0.025 s]
[INFO] ANTLR 4 Runtime Tests (2nd generation) ............. SUCCESS [  1.932 s]
[INFO] ANTLR 4 Tool Tests ................................. SUCCESS [  0.018 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 10.324 s
[INFO] Finished at: 2016-11-16T13:49:38-08:00
[INFO] Final Memory: 42M/488M
[INFO] ------------------------------------------------------------------------
```

# Testing tool and targets

In order to perform the tests on all target languages, make sure that you have `mono` and `nodejs` installed. For example, on OS X:

```bash
$ brew install mono
$ brew install node
```

To run the tests and **install into local repository** `~/.m2/repository/org/antlr`, do this:

```bash
$ mvn install
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

Note: That is actually result of running the much faster:

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

## Running test subsets

### Run one test group across targets

```bash
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

## Building ANTLR in Intellij IDE

After download ANTLR source, just "import project from existing sources" and click on the "Maven Projects" tab in right gutter of IDE. It should build stuff in the background automatically and look like:

<img src=images/intellij-maven.png width=200>
