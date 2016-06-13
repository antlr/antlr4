# Building ANTLR

Most programmers do not need the information on this page because they will simply download the appropriate jar(s) or use ANTLR through maven (via ANTLR's antlr4-maven-plugin). If you would like to fork the project and fix bugs or tweak the runtime code generation, then you will almost certainly need to build ANTLR itself. There are two components:

 1. the tool that compiles grammars down into parsers and lexers in one of the target languages
 1. the runtime used by those generated parsers and lexers.

I will assume that the root directory is `/tmp` for the purposes of explaining how to build ANTLR in this document.

# Get the source

The first step is to get the Java source code from the ANTLR 4 repository at github. You can download the repository from github, but the easiest thing to do is simply clone the repository on your local disk:

```bash
$ cd /tmp
/tmp $ git clone git@github.com:antlr/antlr4.git
Cloning into 'antlr4'...
remote: Counting objects: 43273, done.
remote: Compressing objects: 100% (57/57), done.
remote: Total 43273 (delta 26), reused 0 (delta 0)
Receiving objects: 100% (43273/43273), 18.76 MiB | 1.60 MiB/s, done.
Resolving deltas: 100% (22419/22419), done.
Checking connectivity... done.
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
[INFO] Reactor Summary:
[INFO] 
[INFO] ANTLR 4 ............................................ SUCCESS [  0.447 s]
[INFO] ANTLR 4 Runtime .................................... SUCCESS [  3.113 s]
[INFO] ANTLR 4 Tool ....................................... SUCCESS [ 14.408 s]
[INFO] ANTLR 4 Maven plugin ............................... SUCCESS [  1.276 s]
[INFO] ANTLR 4 Runtime Test Generator ..................... SUCCESS [  0.773 s]
[INFO] ANTLR 4 Tool Tests ................................. SUCCESS [  6.920 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
...
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
[INFO] ANTLR 4 ............................................ SUCCESS [  0.462 s]
[INFO] ANTLR 4 Runtime .................................... SUCCESS [  9.163 s]
[INFO] ANTLR 4 Tool ....................................... SUCCESS [  3.683 s]
[INFO] ANTLR 4 Maven plugin ............................... SUCCESS [  1.897 s]
[INFO] ANTLR 4 Runtime Test Generator ..................... SUCCESS [07:11 min]
[INFO] ANTLR 4 Tool Tests ................................. SUCCESS [ 16.694 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 07:43 min
...
```

You should see these jars (building 4.5.2-SNAPSHOT):

```bash
/Users/parrt/.m2/repository/org/antlr $ find antlr4* -name '*.jar'
antlr4/4.5/antlr4-4.5.jar
antlr4/4.5.2-SNAPSHOT/antlr4-4.5.2-SNAPSHOT-tests.jar
antlr4/4.5.2-SNAPSHOT/antlr4-4.5.2-SNAPSHOT.jar
antlr4-maven-plugin/4.5/antlr4-maven-plugin-4.5.jar
antlr4-maven-plugin/4.5.2-SNAPSHOT/antlr4-maven-plugin-4.5.2-SNAPSHOT.jar
antlr4-runtime/4.5/antlr4-runtime-4.5.jar
antlr4-runtime/4.5.2-SNAPSHOT/antlr4-runtime-4.5.2-SNAPSHOT.jar
antlr4-runtime-testsuite/4.5.2-SNAPSHOT/antlr4-runtime-testsuite-4.5.2-SNAPSHOT-tests.jar
antlr4-runtime-testsuite/4.5.2-SNAPSHOT/antlr4-runtime-testsuite-4.5.2-SNAPSHOT.jar
antlr4-tool-testsuite/4.5.2-SNAPSHOT/antlr4-tool-testsuite-4.5.2-SNAPSHOT.jar
```

Note that ANTLR is written in itself, which is why maven downloads antlr4-4.5.jar for boostrapping 4.5.2-SNAPSHOT purposes.

## Building without testing
To build without running the tests (saves more than 30 mins), do this:

```bash
mvn -DskipTests install
```

## Building ANTLR in Intellij IDE

After download ANTLR source, just "import project from existing sources" and click on the "Maven Projects" tab in right gutter of IDE. It should build stuff in the background automatically and look like:

<img src=images/intellij-maven.png width=200>
