# Cutting an ANTLR Release

## Github

### Get dev merged into master

Do this or make a PR:

```bash
cd ~/antlr/code/antlr4
git checkout master
git merge dev
```

### Turn on DCO Enforcement

As of 4.10.1, we will be using the Linux DCO not the previous contributors license agreement that required signing the file. Now, we use the DCO and contributors must use `-s` on each commit to the branch associated with a pull request.

See [GitHub App DCO](https://github.com/apps/dco).

Make sure this feature is turned on for the `antlr4` repo upon release.

### Delete existing release tag

Wack any existing tag as mvn will create one and it fails if already there.

```
$ git tag -d 4.11.1
$ git push origin :refs/tags/4.11.1
$ git push upstream :refs/tags/4.11.1
```

### Go release tags

It seems that [Go needs a `v` in the release git tag](https://go.dev/ref/mod#glos-version) so make sure that we double up with 4.11.1 and v4.11.1.

```
$ git tag -a runtime/Go/antlr/v4/v4.11.1 -m "Go runtime module only" 
$ git push upstream runtime/Go/antlr/v4/v4.11.1
$ git push origin runtime/Go/antlr/v4/v4.11.1
```


## Bump version in code and other files

There are a number of files that require inversion number be updated.


Here is a simple script to display any line from the critical files with, say, `4.11.1` in it.  Here's an example run of the script:

```bash
~/antlr/code/antlr4 $ python scripts/update_antlr_version.py 4.10 4.11.0
Updating ANTLR version from 4.10 to 4.11.0
Set ANTLR repo root (default ~/antlr/code/antlr4): 
Perform antlr4 `mvn clean` and wipe build dirs Y/N? (default no): 
Ok, not cleaning antlr4 dir
4.10 appears on 2 lines so _not_ updating /tmp/antlr4/runtime/JavaScript/package-lock.json
4.10 not in /tmp/antlr4/doc/releasing-antlr.md
```

Make sure this file doesn't have `-SNAPSHOT` when releasing!

```
runtime/Java/src/org/antlr/v4/runtime/RuntimeMetaData.java
```

It's also worth doing a quick check to see if you find any other references to a version:

```bash
mvn clean
find . -type f -exec grep -l '4\.10.1' {} \; | grep -v -E '\.o|\.a|\.jar|\.dylib|node_modules/|\.class|tests/|CHANGELOG|\.zip|\.gz|.iml|.svg'
```

Commit to repository.

### PHP runtime

We only have to copy the PHP runtime into the ANTLR repository to run the unittests. But, we still need to bump the version to 4.11.1 in `~/antlr/code/antlr-php-runtime/src/RuntimeMetaData.php` in the separate repository, commit, and push.

```
cd ~/antlr/code/antlr-php-runtime/src
git checkout dev # Should be the default
git pull origin dev
... vi RuntimeMetaData.php ...
git commit -a -m "Update PHP Runtime to latest version"
git push origin dev
git checkout master
git pull origin master
git merge dev
git push origin master
```

## Build XPath parsers

This section addresses a [circular dependency regarding XPath](https://github.com/antlr/antlr4/issues/3600). In the java target I avoided a circular dependency (gen 4.11.0 parser for XPath using 4.11.0 which needs it to build) by hand building the parser: runtime/Java/src/org/antlr/v4/runtime/tree/xpath/XPath.java.  Probably we won't have to rerun this for the patch releases, just major ones that alter the ATN serialization.

```
cd ~/antlr/code/antlr4/runtime/CSharp/src/Tree/Xpath
java -cp ":/Users/parrt/.m2/repository/org/antlr/antlr4/4.11.1-SNAPSHOT/antlr4-4.11.1-SNAPSHOT-complete.jar:$CLASSPATH" org.antlr.v4.Tool -Dlanguage=CSharp XPathLexer.g4

cd ~/antlr/code/antlr4/runtime/Python3/tests/expr
java -cp ":/Users/parrt/.m2/repository/org/antlr/antlr4/4.11.1-SNAPSHOT/antlr4-4.11.1-SNAPSHOT-complete.jar:$CLASSPATH" org.antlr.v4.Tool -Dlanguage=Python2 Expr.g4
java -cp ":/Users/parrt/.m2/repository/org/antlr/antlr4/4.11.1-SNAPSHOT/antlr4-4.11.1-SNAPSHOT-complete.jar:$CLASSPATH" org.antlr.v4.Tool -Dlanguage=Python2 XPathLexer.g4

cd ~/antlr/code/antlr4/runtime/Python3/tests/expr
java -cp ":/Users/parrt/.m2/repository/org/antlr/antlr4/4.11.1-SNAPSHOT/antlr4-4.11.1-SNAPSHOT-complete.jar:$CLASSPATH" org.antlr.v4.Tool -Dlanguage=Python3 Expr.g4
java -cp ":/Users/parrt/.m2/repository/org/antlr/antlr4/4.11.1-SNAPSHOT/antlr4-4.11.1-SNAPSHOT-complete.jar:$CLASSPATH" org.antlr.v4.Tool -Dlanguage=Python3 XPathLexer.g4
```

## Maven Repository Settings

First, make sure you have maven set up to communicate with staging servers etc...  Create file `~/.m2/settings.xml` with appropriate username/password for staging server and gpg.keyname/passphrase for signing. Make sure it has strict visibility privileges to just you. On unix, it looks like:

```bash
beast:~/.m2 $ ls -l settings.xml 
-rw-------  1 parrt  staff  914 Jul 15 14:42 settings.xml
```

Here is the file template

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!--
  User-specific configuration for maven. Includes things that should not
  be distributed with the pom.xml file, such as developer identity, along with
  local settings, like proxy information.
-->
<settings>
   <servers>
        <server>
          <id>sonatype-nexus-staging</id>
          <username>sonatype-username</username>
          <password>XXX</password>
        </server>
        <server>
          <id>sonatype-nexus-snapshots</id>
          <username>sonatype-username</username>
          <password>XXX</password>
        </server>
   </servers>
    <profiles>
            <profile>
              <activation>
                    <activeByDefault>false</activeByDefault>
              </activation>
              <properties>
                    <gpg.keyname>UUU</gpg.keyname>
                    <gpg.passphrase>XXX</gpg.passphrase>
              </properties>
            </profile>
    </profiles>
</settings>
```

## Maven deploy snapshot

The goal is to get a snapshot, such as `4.11.1-SNAPSHOT`, to the staging server: [antlr4 tool](https://oss.sonatype.org/content/repositories/snapshots/org/antlr/antlr4/4.11.1-SNAPSHOT/) and [antlr4 java runtime](https://oss.sonatype.org/content/repositories/snapshots/org/antlr/antlr4-runtime/4.11.1-SNAPSHOT/).

Do this:

```bash
$ mvn install -DskipTests  # seems required to get the jar files visible to maven
$ mvn deploy -DskipTests
...
Uploading: https://oss.sonatype.org/content/repositories/snapshots/org/antlr/antlr4-tool-testsuite/maven-metadata.xml
Uploaded: https://oss.sonatype.org/content/repositories/snapshots/org/antlr/antlr4-tool-testsuite/maven-metadata.xml (388 B at 0.9 KB/sec)
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
[INFO] 
[INFO] ANTLR 4 ............................................ SUCCESS [  4.073 s]
[INFO] ANTLR 4 Runtime .................................... SUCCESS [ 13.828 s]
[INFO] ANTLR 4 Tool ....................................... SUCCESS [ 14.032 s]
[INFO] ANTLR 4 Maven plugin ............................... SUCCESS [  6.547 s]
[INFO] ANTLR 4 Runtime Test Annotations ................... SUCCESS [  2.519 s]
[INFO] ANTLR 4 Runtime Test Processors .................... SUCCESS [  2.385 s]
[INFO] ANTLR 4 Runtime Tests (4th generation) ............. SUCCESS [ 15.276 s]
[INFO] ANTLR 4 Tool Tests ................................. SUCCESS [  2.233 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 01:01 min
[INFO] Finished at: 2016-12-11T09:37:54-08:00
[INFO] Final Memory: 44M/470M
[INFO] ------------------------------------------------------------------------
```

## Maven release

The maven deploy lifecycle phased deploys the artifacts and the poms for the ANTLR project to the [sonatype remote staging server](https://oss.sonatype.org/content/repositories/snapshots/).

```bash
mvn deploy -DskipTests
```

Make sure `gpg` is installed (`brew install gpg` on mac). Also must [create a key and publish it](https://blog.sonatype.com/2010/01/how-to-generate-pgp-signatures-with-maven/) then update `.m2/settings` to use that public key.

Then:

```bash
mvn release:prepare -Darguments="-DskipTests"
```

Hmm...per https://github.com/keybase/keybase-issues/issues/1712 we need this to make gpg work:

```bash
export GPG_TTY=$(tty)
```

You should see 0x37 in generated .class files after 0xCAFEBABE; see [Java SE 11 = 55 (0x37 hex)](https://en.wikipedia.org/wiki/Java_class_file):

```bash
~/antlr/code/antlr4 $ od -h tool/target/classes/org/antlr/v4/Tool.class |head -1
0000000      feca    beba    0000    3700    ed04    0207    0a9d    0100
                                     ^^
```

Also verify run time is 1.8:

```bash
od -h runtime/Java/target/classes/org/antlr/v4/runtime/Token.class | head -1
0000000      feca    beba    0000    3400    2500    0007    0722    2300
```

It will start out by asking you the version number:

```
...
What is the release version for "ANTLR 4"? (org.antlr:antlr4-master) 4.11.1: : 4.11.1
What is the release version for "ANTLR 4 Runtime"? (org.antlr:antlr4-runtime) 4.11.1: : 
What is the release version for "ANTLR 4 Tool"? (org.antlr:antlr4) 4.11.1: : 
What is the release version for "ANTLR 4 Maven plugin"? (org.antlr:antlr4-maven-plugin) 4.11.1: : 
What is the release version for "ANTLR 4 Runtime Test Generator"? (org.antlr:antlr4-runtime-testsuite) 4.11.1: : 
What is the release version for "ANTLR 4 Tool Tests"? (org.antlr:antlr4-tool-testsuite) 4.11.1: : 
What is SCM release tag or label for "ANTLR 4"? (org.antlr:antlr4-master) antlr4-master-4.11.1: : 4.11.1
What is the new development version for "ANTLR 4"? (org.antlr:antlr4-master) 4.11.2-SNAPSHOT:
...
```

Maven will go through your pom.xml files to update versions from 4.11.1-SNAPSHOT to 4.11.1 for release and then to 4.11.2-SNAPSHOT after release, which is done with:

```bash
mvn release:perform -Darguments="-DskipTests"
```

Maven will use git to push pom.xml changes.

Now, go here:

&nbsp;&nbsp;&nbsp;&nbsp;[https://oss.sonatype.org/#welcome](https://oss.sonatype.org/#welcome)

and on the left click "Staging Repositories". You click the staging repo and close it, then you refresh, click it and release it. It's done when you see it here:

&nbsp;&nbsp;&nbsp;&nbsp;[https://oss.sonatype.org/service/local/repositories/releases/content/org/antlr/antlr4-runtime/4.11.1/antlr4-runtime-4.11.1.jar](https://oss.sonatype.org/service/local/repositories/releases/content/org/antlr/antlr4-runtime/4.11.1/antlr4-runtime-4.11.1.jar)

All releases should be here: [https://repo1.maven.org/maven2/org/antlr/antlr4-runtime](https://repo1.maven.org/maven2/org/antlr/antlr4-runtime).

## Deploying Targets

### JavaScript

**Push to npm**

```bash
cd ~/antlr/code/antlr4/runtime/JavaScript
npm update
npm install
rm -rf node_modules
npm run build 
npm login     # asks for username/password/2FA (npmjs.com)
npm publish   # don't put antlr4 on there or it will try to push the old version for some reason
```

Move (and zip) target to website:

```bash
cd src
zip -r ~/antlr/sites/website-antlr4/download/antlr-javascript-runtime-4.11.1.zip .
```

### CSharp

As of writing, you can only release from a Windows box, because Visual Studio for Mac can only build the netstandard2.0 version

**Install the pre-requisites**

You need 'msbuild' and `nuget` to be installed. 

**Creating the signed assembly**

cd ~/antlr/code/antlr4/runtime/CSharp/src
dotnet build -c Release Antlr4.csproj

check that the bin/Release folder contains both the netstandard2.0 and the net45 builds
the binaries are already signed, but it's worth double checking

sn -v bin/Release/netstandard2.0/Antlr4.Runtime.Standard.dll
sn -v bin/Release/net45/Antlr4.Runtime.Standard.dll

both should say the dll is valid

**Publishing to NuGet**

You need to be a NuGet owner for "ANTLR 4 Standard Runtime"
As a registered NuGet user, you can then manually upload the package here: [https://www.nuget.org/packages/manage/upload](https://www.nuget.org/packages/manage/upload)

Alternately, you can publish from the cmd line. You need to get your NuGet key from [https://www.nuget.org/account#](https://www.nuget.org/account#) and then from the cmd line, you can then type:

```cmd
nuget push Antlr4.Runtime.Standard.<version>.nupkg <your-key> -Source https://www.nuget.org/api/v2/package
```

### Python

The Python targets get deployed with `setup.py` (Python 2), or `build` and `twine` (Python 3).
Install them by
```sh
pip3 install build twine
```

First, set up `~/.pypirc` with tight privileges:

```bash
beast:~ $ ls -l ~/.pypirc
-rw-------  1 parrt  staff  267 Jul 15 17:02 /Users/parrt/.pypirc
```

```
[distutils] # this tells distutils what package indexes you can push to
index-servers =
    pypi
    pypitest

[pypi]
username: parrt
password: xxx

[pypitest]
username: parrt
password: xxx
```

Then run the usual python set up stuff:

```bash
cd ~/antlr/code/antlr4/runtime/Python2
# assume you have ~/.pypirc set up
python setup.py sdist upload
```

For Python 3 target, do

```bash
cd ~/antlr/code/antlr4/runtime/Python3
python3 -m build
# assume you have ~/.pypirc set up
twine upload dist/antlr4-python3-runtime-<version>.tar.gz dist/antlr4_python3_runtime-<version>-py3-none-any.whl
```

There are links to the artifacts in [download.html](http://www.antlr.org/download.html) already.

### C++

The C++ target is the most complex one, because it addresses multiple platforms, which require individual handling. We have 4 scenarios to cover:

* **Windows**: static and dynamic libraries for the VC++ runtime 2017 or 2019 (corresponding to Visual Studio 2017 or 2019) + header files. All that in 32 and 64bit, debug + release.
* **MacOS**: static and dynamic release libraries + header files.
* **iOS**: no prebuilt binaries, but just a zip of the source, including the XCode project to build everything from source.
* **Linux**: no prebuilt binaries, but just a zip of the source code, including the cmake file to build everything from source there.

In theory we could also create a library for iOS, but that requires to sign it, which depends on an active iOS developer account. So we leave this up to the ANTLR user to build the iOS lib, like we do for Linux builds.

For each platform there's a deployment script which generates zip archives and copies them to the target folder. The Windows deployment script must be run on a machine with VS 2013 + VS 2015 installed. The Mac script must be run on a machine with XCode 7+ installed. The source script can be executed on any Linux or Mac box.

On a Mac (with XCode 7+ installed):

```bash
cd ~/antlr/code/antlr4/runtime/Cpp
./deploy-macos.sh
cp antlr4-cpp-runtime-macos.zip ~/antlr/sites/website-antlr4/download/antlr4-cpp-runtime-4.11.1-macos.zip
```

On any Mac or Linux machine:

```bash
cd ~/antlr/code/antlr4/runtime/Cpp
./deploy-source.sh
cp antlr4-cpp-runtime-source.zip ~/antlr/sites/website-antlr4/download/antlr4-cpp-runtime-4.11.1-source.zip
```

On a Windows machine the build scripts checks if VS 2017 and/or VS 2019 are installed and builds binaries for each, if found. This script requires 7z to be installed (http://7-zip.org then do `set PATH=%PATH%;C:\Program Files\7-Zip\` from DOS not powershell).

```bash
cd ~/antlr/code/antlr4/runtime/Cpp
deploy-windows.cmd Community
cp antlr4-cpp-runtime-vs2019.zip ~/antlr/sites/website-antlr4/download/antlr4-cpp-runtime-4.11.1-vs2019.zip
```

Move target to website (**_rename to a specific ANTLR version first if needed_**):

```bash
pushd ~/antlr/sites/website-antlr4/download
git add antlr4-cpp-runtime-4.11.1-macos.zip
git add antlr4-cpp-runtime-4.11.1-windows.zip
git add antlr4-cpp-runtime-4.11.1-source.zip
git commit -a -m 'update C++ runtime'
git push origin gh-pages
popd
```

### Dart

Install Dart SDK from https://dart.dev/get-dart

Push to pub.dev

```bash
cd ~/antlr/code/antlr4/runtime/Dart
dart pub publish
```

It will warn that no change log found for the new version.
Otherwise enter `N` to ignore the warning.

## Update website

### javadoc for runtime and tool

Jars are in:

```
~/.m2/repository/org/antlr/antlr4-runtime/4.11.1/antlr4-runtime-4.11.1
```

### Update version and copy jars / api

Copy javadoc and java jars to website using this script:

```bash
cd ~/antlr/code/antlr4
python scripts/deploy_to_website.py 4.11.0 4.11.1
```

Output:

```bash
Updating ANTLR version from 4.11.0 to 4.11.1
Set ANTLR website root (default /Users/parrt/antlr/sites/website-antlr4): 
Version string updated. Please commit/push:
Javadoc copied:
	api/Java updated from antlr4-runtime-4.11.1-javadoc.jar
	api/JavaTool updated from antlr4-4.11.1-javadoc.jar
Jars copied:
	antlr-4.11.1-complete.jar
	antlr-runtime-4.11.1.jar

Please look for and add new api files!!
Then MANUALLY commit/push:

git commit -a -m 'Update website, javadoc, jars to 4.11.1'
git push origin gh-pages
```

<!--
```bash
cp ~/.m2/repository/org/antlr/antlr4-runtime/4.11.1/antlr4-runtime-4.11.1.jar ~/antlr/sites/website-antlr4/download/antlr-runtime-4.11.1.jar
cp ~/.m2/repository/org/antlr/antlr4/4.11.1/antlr4-4.11.1-complete.jar ~/antlr/sites/website-antlr4/download/antlr-4.11.1-complete.jar
cd ~/antlr/sites/website-antlr4/download
git add antlr-4.11.1-complete.jar
git add antlr-runtime-4.11.1.jar 
```
-->

Once it's done, you must do the following manually:

```
cd ~/antlr/sites/website-antlr4
git commit -a -m 'Update website, javadoc, jars to 4.11.1'
git push origin gh-pages
```

<!--
Then copy to website:

```bash
cd ~/antlr/sites/website-antlr4/api
git checkout gh-pages
git pull origin gh-pages
cd Java
jar xvf ~/.m2/repository/org/antlr/antlr4-runtime/4.11.1/antlr4-runtime-4.11.1-javadoc.jar
cd ../JavaTool
jar xvf ~/.m2/repository/org/antlr/antlr4/4.11.1/antlr4-4.11.1-javadoc.jar
git commit -a -m 'freshen api doc'
git push origin gh-pages
```
-->

## Get fresh dev branch

```bash
git checkout master
git pull upstream master
git checkout dev
git pull upstream dev
git merge master
git push origin dev
git push upstream dev
```

## Update Intellij plug-in

Rebuild antlr plugin with new antlr jar.
