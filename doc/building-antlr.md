# Building ANTLR

Most programmers do not need the information on this page because they will simply download the appropriate jar(s) or use ANTLR through maven (via ANTLR's antlr4-maven-plugin). If you would like to fork the project and fix bugs or tweak the runtime code generation, then you will almost certainly need to build ANTLR itself. There are two components:

 1. the tool that compiles grammars down into parsers and lexers in one of the target languages
 1. the runtime used by those generated parsers and lexers.

I will assume that the root directory is `/tmp` for the purposes of explaining how to build ANTLR in this document.

*As of 4.6, ANTLR tool and Java-target runtime requires Java 7. As of 4.10, we have verified that the tool itself builds with Java 8 and 11.*

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

# Check your environment

If you are starting from a clean, minimum Ubuntu OS, check your environment.


```bash
$ sudo apt-get update
$ # Get Java
$ java > /dev/null 2>&1
$ if [[ "$?" != "0" ]]; then sudo apt install -y openjdk-11-jre-headless; fi
$ # Get Mvn
$ mvn > /dev/null 2>&1
$ if [[ "$?" != "0" ]]; then sudo apt install -y maven; fi

```

# Compile

The current maven build seems complicated to me because there is a dependency of the project on itself. The runtime tests naturally depend on the current version being available but it won't compile without the current version.  Once you have the generated/installed jar, mvn builds but otherwise there's a dependency on what you are going to build.  You will get this error when you try to clean but you can ignore it:

```
[INFO] ANTLR 4 Runtime Tests (4th generation) ............. FAILURE [  0.073 s]
...
[ERROR] Plugin org.antlr:antlr4-maven-plugin:4.10-SNAPSHOT or one of its dependencies could not be resolved: Could not find artifact org.antlr:antlr4-maven-plugin:jar:4.10-SNAPSHOT -> [Help 1]
```

To be super squeaky clean, you can wipe out the repository cache, then do the build:

```
$ export MAVEN_OPTS="-Xmx1G"   # don't forget this on linux
cd /tmp/antlr4 # or wherever you have the software
rm -rf ~/.m2/repository/org/antlr*
mvn clean
mvn -DskipTests install
```

**NOTE:** We do `install` not `compile` as tool tests and such refer to modules that must be pulled from the maven install local cache.

Once you have completed this process once and there is a jar hanging around in the repository cache.

# Installing libs to mvn cache locally

To skip the tests (which require all the target languages be installed) and **install into local repository** `~/.m2/repository/org/antlr`, do this:

```bash
$ export MAVEN_OPTS="-Xmx1G"     # don't forget this on linux
$ mvn install -DskipTests   # make sure all artifacts are visible on this machine
```

You should see these jars (when building 4.6-SNAPSHOT):

```bash
/Users/parrt/.m2/repository/org/antlr $ find antlr4* -name '*.jar'
antlr4-maven-plugin/4.6-SNAPSHOT/antlr4-maven-plugin-4.6-SNAPSHOT.jar
antlr4-runtime-testsuite/4.6-SNAPSHOT/antlr4-runtime-testsuite-4.6-SNAPSHOT-tests.jar
antlr4-runtime-testsuite/4.6-SNAPSHOT/antlr4-runtime-testsuite-4.6-SNAPSHOT.jar
antlr4-runtime/4.6-SNAPSHOT/antlr4-runtime-4.6-SNAPSHOT.jar
antlr4-tool-testsuite/4.6-SNAPSHOT/antlr4-tool-testsuite-4.6-SNAPSHOT.jar
antlr4/4.6-SNAPSHOT/antlr4-4.6-SNAPSHOT-tests.jar
antlr4/4.6-SNAPSHOT/antlr4-4.6-SNAPSHOT.jar
```

Note that ANTLR is written in itself, which is why maven downloads antlr4-4.5.jar for boostrapping 4.6-SNAPSHOT purposes.

# Testing tool and targets

See [ANTLR project unit tests](antlr-project-testing.md).


# Building without testing

To build without running the tests (saves a lot of time), do this:

```bash
$ mvn -DskipTests install
```

## Building ANTLR in Intellij IDE

After download ANTLR source, just "import project from existing sources" and click on the "Maven Projects" tab in right gutter of IDE. It should build stuff in the background automatically and look like:

<img src=images/intellij-maven.png width=200>
