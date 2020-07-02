# Cutting an ANTLR Release

## Github

Create a pre-release or full release at github; [Example 4.5-rc-1](https://github.com/antlr/antlr4/releases/tag/4.5-rc-1).

### Delete existing release tag

Wack any existing tag as mvn will create one and it fails if already there.

```
$ git tag -d 4.8
$ git push origin :refs/tags/4.8
$ git push upstream :refs/tags/4.8
```

### Create release candidate tag

```bash
$ git tag -a 4.8-rc1 -m 'heading towards 4.8'
$ git push origin 4.8-rc1
$ git push upstream 4.8-rc1
```

## Update submodules

Make sure you tell git to pull in the submodule (for every clone you do of antlr4):

```bash
git submodule init
```

Also bump version to 4.8 in `runtime/PHP/src/RuntimeMetaData.php`.

Update the runtime submodules by running the following command:

```bash
git submodule update --recursive
git submodule update --remote --merge # might only need this last one but do both
```

Make sure these changes go back to antlr4 repo:

```bash
git add runtime/PHP
git commit -m "Update PHP Runtime to latest version"
```

## Bump version

Edit the repository looking for 4.5 or whatever and update it. Bump version in the following files:

 * runtime/Java/src/org/antlr/v4/runtime/RuntimeMetaData.java
 * runtime/Python2/setup.py
 * runtime/Python2/src/antlr4/Recognizer.py
 * runtime/Python3/setup.py
 * runtime/Python3/src/antlr4/Recognizer.py
 * runtime/CSharp/runtime/CSharp/Antlr4.Runtime/Properties/AssemblyInfo.cs
 * runtime/CSharp/runtime/CSharp/Antlr4.Runtime/Antlr4.Runtime.dotnet.csproj
 * runtime/JavaScript/package.json
 * runtime/JavaScript/src/antlr4/Recognizer.js
 * runtime/Cpp/VERSION
 * runtime/Cpp/runtime/src/RuntimeMetaData.cpp
 * runtime/Cpp/cmake/ExternalAntlr4Cpp.cmake
 * runtime/Cpp/demo/generate.cmd
 * runtime/Go/antlr/recognizer.go
 * runtime/Swift/Antlr4/org/antlr/v4/runtime/RuntimeMetaData.swift
 * tool/src/org/antlr/v4/codegen/target/GoTarget.java
 * tool/src/org/antlr/v4/codegen/target/CppTarget.java
 * tool/src/org/antlr/v4/codegen/target/CSharpTarget.java
 * tool/src/org/antlr/v4/codegen/target/JavaScriptTarget.java
 * tool/src/org/antlr/v4/codegen/target/Python2Target.java
 * tool/src/org/antlr/v4/codegen/target/Python3Target.java
 * tool/src/org/antlr/v4/codegen/target/SwiftTarget.java
 * tool/src/org/antlr/v4/codegen/Target.java
 * tool/resources/org/antlr/v4/tool/templates/codegen/Swift/Swift.stg
 
Here is a simple script to display any line from the critical files with, say, `4.5` in it:

```bash
find tool runtime -type f -exec grep -l '4\.6' {} \;
```

Commit to repository.

## Building

ugh. apparently you have to `mvn install` and then `mvn compile` or some such or subdir pom.xml's won't see the latest runtime build.

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

The goal is to get a snapshot, such as `4.8-SNAPSHOT`, to the staging server: [antlr4 tool](https://oss.sonatype.org/content/repositories/snapshots/org/antlr/antlr4) and [antlr4 java runtime](https://oss.sonatype.org/content/repositories/snapshots/org/antlr/antlr4-runtime).

Do this:

```bash
$ mvn deploy -DskipTests
...
[INFO] --- maven-deploy-plugin:2.7:deploy (default-deploy) @ antlr4-tool-testsuite ---
Downloading: https://oss.sonatype.org/content/repositories/snapshots/org/antlr/antlr4-tool-testsuite/4.8-SNAPSHOT/maven-metadata.xml
Uploading: https://oss.sonatype.org/content/repositories/snapshots/org/antlr/antlr4-tool-testsuite/4.8-SNAPSHOT/antlr4-tool-testsuite-4.8-20161211.173752-1.jar
Uploaded: https://oss.sonatype.org/content/repositories/snapshots/org/antlr/antlr4-tool-testsuite/4.8-SNAPSHOT/antlr4-tool-testsuite-4.8-20161211.173752-1.jar (3 KB at 3.4 KB/sec)
Uploading: https://oss.sonatype.org/content/repositories/snapshots/org/antlr/antlr4-tool-testsuite/4.8-SNAPSHOT/antlr4-tool-testsuite-4.8-20161211.173752-1.pom
Uploaded: https://oss.sonatype.org/content/repositories/snapshots/org/antlr/antlr4-tool-testsuite/4.8-SNAPSHOT/antlr4-tool-testsuite-4.8-20161211.173752-1.pom (3 KB at 6.5 KB/sec)
Downloading: https://oss.sonatype.org/content/repositories/snapshots/org/antlr/antlr4-tool-testsuite/maven-metadata.xml
Downloaded: https://oss.sonatype.org/content/repositories/snapshots/org/antlr/antlr4-tool-testsuite/maven-metadata.xml (371 B at 1.4 KB/sec)
Uploading: https://oss.sonatype.org/content/repositories/snapshots/org/antlr/antlr4-tool-testsuite/4.8-SNAPSHOT/maven-metadata.xml
Uploaded: https://oss.sonatype.org/content/repositories/snapshots/org/antlr/antlr4-tool-testsuite/4.8-SNAPSHOT/maven-metadata.xml (774 B at 1.8 KB/sec)
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
[INFO] ANTLR 4 Runtime Tests (2nd generation) ............. SUCCESS [ 15.276 s]
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
export JAVA_HOME=`/usr/libexec/java_home -v 1.7`; mvn deploy -DskipTests
```

With JDK 1.7 (not 6 or 8), do this:

```bash
export JAVA_HOME=`/usr/libexec/java_home -v 1.7`; mvn release:prepare -Darguments="-DskipTests"
```

Hm...per https://github.com/keybase/keybase-issues/issues/1712 we need this to make gpg work:

```bash
export GPG_TTY=$(tty)
```

Side note to set jdk 1.7 on os x:

```bash
alias java='/Library/Java/JavaVirtualMachines/jdk1.7.0_21.jdk/Contents/Home/bin/java'
alias javac='/Library/Java/JavaVirtualMachines/jdk1.7.0_21.jdk/Contents/Home/bin/javac'
alias javadoc='/Library/Java/JavaVirtualMachines/jdk1.7.0_21.jdk/Contents/Home/bin/javadoc'
alias jar='/Library/Java/JavaVirtualMachines/jdk1.7.0_21.jdk/Contents/Home/bin/jar'
export JAVA_HOME=`/usr/libexec/java_home -v 1.7`
```

But I think just this on front of mvn works:

```
export JAVA_HOME=`/usr/libexec/java_home -v 1.7`; mvn ...
```

You should see 0x33 in generated .class files after 0xCAFEBABE; see [Java SE 7 = 51 (0x33 hex)](https://en.wikipedia.org/wiki/Java_class_file):

```bash
beast:/tmp/org/antlr/v4 $ od -h Tool.class |head -1
0000000      feca    beba    0000    3300    fa04    0207    0ab8    0100
```

It will start out by asking you the version number:

```
...
What is the release version for "ANTLR 4"? (org.antlr:antlr4-master) 4.8: : 4.8
What is the release version for "ANTLR 4 Runtime"? (org.antlr:antlr4-runtime) 4.8: : 
What is the release version for "ANTLR 4 Tool"? (org.antlr:antlr4) 4.8: : 
What is the release version for "ANTLR 4 Maven plugin"? (org.antlr:antlr4-maven-plugin) 4.8: : 
What is the release version for "ANTLR 4 Runtime Test Generator"? (org.antlr:antlr4-runtime-testsuite) 4.8: : 
What is the release version for "ANTLR 4 Tool Tests"? (org.antlr:antlr4-tool-testsuite) 4.8: : 
What is SCM release tag or label for "ANTLR 4"? (org.antlr:antlr4-master) antlr4-master-4.8: : 4.8
What is the new development version for "ANTLR 4"? (org.antlr:antlr4-master) 4.8.1-SNAPSHOT:
...
```

Maven will go through your pom.xml files to update versions from 4.8-SNAPSHOT to 4.8 for release and then to 4.8.1-SNAPSHOT after release, which is done with:

```bash
mvn release:perform -Darguments="-DskipTests"
```

Maven will use git to push pom.xml changes.

Now, go here:

&nbsp;&nbsp;&nbsp;&nbsp;[https://oss.sonatype.org/#welcome](https://oss.sonatype.org/#welcome)

and on the left click "Staging Repositories". You click the staging repo and close it, then you refresh, click it and release it. It's done when you see it here:

&nbsp;&nbsp;&nbsp;&nbsp;[https://oss.sonatype.org/service/local/repositories/releases/content/org/antlr/antlr4-runtime/4.8-1/antlr4-runtime-4.8-1.jar](https://oss.sonatype.org/service/local/repositories/releases/content/org/antlr/antlr4-runtime/4.8-1/antlr4-runtime-4.8-1.jar)

All releases should be here: https://repo1.maven.org/maven2/org/antlr/antlr4-runtime/

Copy the jars to antlr.org site and update download/index.html

```bash
cp ~/.m2/repository/org/antlr/antlr4-runtime/4.8/antlr4-runtime-4.8.jar ~/antlr/sites/website-antlr4/download/antlr-runtime-4.8.jar
cp ~/.m2/repository/org/antlr/antlr4/4.8/antlr4-4.8-complete.jar ~/antlr/sites/website-antlr4/download/antlr-4.8-complete.jar
cd ~/antlr/sites/website-antlr4/download
git add antlr-4.8-complete.jar
git add antlr-runtime-4.8.jar 
```

Update on site:

*   download.html
*   index.html
*   api/index.html
*   download/index.html
*   scripts/topnav.js

```
git commit -a -m 'add 4.8 jars'
git push origin gh-pages
```

## Deploying Targets

### JavaScript

```bash
cd runtime/JavaScript
# git add, commit, push
```

**Push to npm**

```bash
cd runtime/JavaScript
npm login
npm publish antlr4
```

Move target to website

```bash
npm run build
cp /dist/antlr4.js ~/antlr/sites/website-antlr4/download
```

### CSharp

Now we have [appveyor create artifact](https://ci.appveyor.com/project/parrt/antlr4/build/artifacts). Go to [nuget](https://www.nuget.org/packages/manage/upload) to upload the `.nupkg`.

### Publishing to Nuget from Windows

**Install the pre-requisites**

Of course you need Mono and `nuget` to be installed. On mac:

- .NET build tools - can be loaded from [here](https://www.visualstudio.com/downloads/)
- nuget - download [nuget.exe](https://www.nuget.org/downloads)
- dotnet - follow [the instructions here](https://www.microsoft.com/net/core)

Alternatively, you can install Visual Studio 2017 and make sure to check boxes with .NET Core SDK.

You also need to enable .NET Framework 3.5 support in Windows "Programs and Features".

If everything is ok, the following command will restore nuget packages, build Antlr for .NET Standard and .NET 3.5 and create nuget package:

```PS
msbuild /target:restore /target:rebuild /target:pack /property:Configuration=Release .\Antlr4.dotnet.sln /verbosity:minimal
```

This should display something like this: 

**Creating and packaging the assembly**

```
Microsoft (R) Build Engine version 15.4.8.50001 for .NET Framework
Copyright (C) Microsoft Corporation. All rights reserved.

  Restoring packages for C:\Code\antlr4-fork\runtime\CSharp\runtime\CSharp\Antlr4.Runtime\Antlr4.Runtime.dotnet.csproj...
  Generating MSBuild file C:\Code\antlr4-fork\runtime\CSharp\runtime\CSharp\Antlr4.Runtime\obj\Antlr4.Runtime.dotnet.csproj.nuget.g.props.
  Generating MSBuild file C:\Code\antlr4-fork\runtime\CSharp\runtime\CSharp\Antlr4.Runtime\obj\Antlr4.Runtime.dotnet.csproj.nuget.g.targets.
  Restore completed in 427.62 ms for C:\Code\antlr4-fork\runtime\CSharp\runtime\CSharp\Antlr4.Runtime\Antlr4.Runtime.dotnet.csproj.
  Antlr4.Runtime.dotnet -> C:\Code\antlr4-fork\runtime\CSharp\runtime\CSharp\Antlr4.Runtime\lib\Release\netstandard1.3\Antlr4.Runtime.Standard.dll
  Antlr4.Runtime.dotnet -> C:\Code\antlr4-fork\runtime\CSharp\runtime\CSharp\Antlr4.Runtime\lib\Release\net35\Antlr4.Runtime.Standard.dll
  Successfully created package 'C:\Code\antlr4-fork\runtime\CSharp\runtime\CSharp\Antlr4.Runtime\lib\Release\Antlr4.Runtime.Standard.4.8.2.nupkg'.
```

**Publishing to NuGet**

You need to be a NuGet owner for "ANTLR 4 Standard Runtime"
As a registered NuGet user, you can then manually upload the package here: [https://www.nuget.org/packages/manage/upload](https://www.nuget.org/packages/manage/upload)

Alternately, you can publish from the cmd line. You need to get your NuGet key from [https://www.nuget.org/account#](https://www.nuget.org/account#) and then from the cmd line, you can then type:

```cmd
nuget push Antlr4.Runtime.Standard.<version>.nupkg <your-key> -Source https://www.nuget.org/api/v2/package
```

Nuget packages are also accessible as artifacts of [AppVeyor builds](https://ci.appveyor.com/project/parrt/antlr4/build/artifacts). 

### Python

The Python targets get deployed with `setup.py`. First, set up `~/.pypirc` with tight privileges:

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
python2 setup.py sdist upload
```

and do again for Python 3 target

```bash
cd ~/antlr/code/antlr4/runtime/Python3
# assume you have ~/.pypirc set up
python3 setup.py sdist upload
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
cd runtime/Cpp
./deploy-macos.sh
cp antlr4-cpp-runtime-macos.zip ~/antlr/sites/website-antlr4/download/antlr4-cpp-runtime-4.8-macos.zip
```

On any Mac or Linux machine:

```bash
cd runtime/Cpp
./deploy-source.sh
cp antlr4-cpp-runtime-source.zip ~/antlr/sites/website-antlr4/download/antlr4-cpp-runtime-4.8-source.zip
```

On a Windows machine the build scripts checks if VS 2017 and/or VS 2019 are installed and builds binaries for each, if found. This script requires 7z to be installed (http://7-zip.org then do `set PATH=%PATH%;C:\Program Files\7-Zip\` from DOS not powershell).

```bash
cd runtime/Cpp
deploy-windows.cmd Community
cp antlr4-cpp-runtime-vs2019.zip ~/antlr/sites/website-antlr4/download/antlr4-cpp-runtime-4.8-vs2019.zip
```

Move target to website (**_rename to a specific ANTLR version first if needed_**):

```bash
pushd ~/antlr/sites/website-antlr4/download
# vi index.html
git add antlr4cpp-runtime-4.8-macos.zip
git add antlr4cpp-runtime-4.8-windows.zip
git add antlr4cpp-runtime-4.8-source.zip
git commit -a -m 'update C++ runtime'
git push origin gh-pages
popd
```

## Update javadoc for runtime and tool

First, gen javadoc:

```bash
$ cd antlr4
$ mvn -DskipTests javadoc:jar install
```

Then copy to website:

```bash
cd ~/antlr/sites/website-antlr4/api
git checkout gh-pages
git pull origin gh-pages
cd Java
jar xvf ~/.m2/repository/org/antlr/antlr4-runtime/4.8/antlr4-runtime-4.8-javadoc.jar
cd ../JavaTool
jar xvf ~/.m2/repository/org/antlr/antlr4/4.8/antlr4-4.8-javadoc.jar
git commit -a -m 'freshen api doc'
git push origin gh-pages
```

## Update Intellij plug-in

Rebuild antlr plugin with new antlr jar.
