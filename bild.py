# !/usr/bin/env python
import os
import string

"""
This script uses my experimental build tool http://www.bildtool.org

In order to build the complete ANTLR4 product with Java, Python 2, and Python 3
targets, do the following from a UNIX command line.  Windows build using this script
is not yet supported. Please use the mvn build or ant build.

!!!You must set path values in test_properties dictionary below to ensure Python
tests run.!!!

mkdir -p /usr/local/antlr # somewhere appropriate where you want to install stuff
cd /usr/local/antlr
git clone git@github.com:parrt/antlr4.git
git clone git@github.com:parrt/antlr4-python3.git
git clone git@github.com:parrt/antlr4-python2.git
# git clone git@github.com:antlr/antlr4-csharp.git  not quite ready use:
# https://github.com/tunnelvisionlabs/antlr4cs/releases/tag/v4.3.0
cd antlr4
./bild.py tests
"""

# bootstrap by downloading bilder.py if not found
import urllib
import os

if not os.path.exists("bilder.py"):
    print "bootstrapping; downloading bilder.py"
    urllib.urlretrieve(
        "https://raw.githubusercontent.com/parrt/bild/master/src/python/bilder.py",
        "bilder.py")

# assumes bilder.py is in current directory
from bilder import *

VERSION = "4.4"
JAVA_TARGET = "."
PYTHON2_TARGET = "../antlr4-python2"
PYTHON3_TARGET = "../antlr4-python3"
CSHARP_TARGET = "../antlr4-csharp"

# Properties needed to run Python[23] tests
test_properties = {
"antlr-python2-python": "/usr/local/bin/python2.7",
"antlr-python2-runtime": uniformpath(PYTHON2_TARGET) + "/src",
"antlr-python3-python": "/usr/local/bin/python3.4",
"antlr-python3-runtime": uniformpath(PYTHON3_TARGET) + "/src",
}

TARGETS = {"Java": uniformpath(JAVA_TARGET),
           "Python2": uniformpath(PYTHON2_TARGET),
           "Python3": uniformpath(PYTHON3_TARGET),  #"CSharp":uniformpath(CSHARP_TARGET)
}


def parsers():
    antlr3("tool/src/org/antlr/v4/parse", "gen3", package="org.antlr.v4.parse")
    antlr3("tool/src/org/antlr/v4/codegen", "gen3", package="org.antlr.v4.codegen",
           args=["-lib", uniformpath("gen3/org/antlr/v4/parse")])
    antlr4("runtime/Java/src/org/antlr/v4/runtime/tree/xpath", "gen4", package="org.antlr.v4.runtime.tree.xpath")

def compile():
    require(parsers)
    cp = uniformpath("out") + os.pathsep + \
         os.path.join(JARCACHE, "antlr-3.5.1-complete.jar") + os.pathsep + \
         "runtime/Java/lib/org.abego.treelayout.core.jar" + os.pathsep
    if os.path.exists(JARCACHE + "/antlr-" + VERSION + "-complete.jar"):
        cp += JARCACHE + "/antlr-" + VERSION + "-complete.jar"
    srcpath = ["gen3", "gen4", "runtime/JavaAnnotations/src", "runtime/Java/src", "tool/src"]
    args = ["-Xlint", "-Xlint:-serial", "-g", "-sourcepath", string.join(srcpath, os.pathsep)]
    for sp in srcpath:
        javac(sp, "out", version="1.6", cp=cp, args=args)
    # pull in targets
    for t in TARGETS:
        javac(TARGETS[t] + "/tool/src", "out", version="1.6", cp=cp, args=args)


def mkjar_complete():
    require(compile)
    copytree(src="tool/resources", trg="out")  # messages, Java code gen, etc...
    manifest = \
        """Main-Class: org.antlr.v4.Tool
Implementation-Vendor: ANTLR
Implementation-Title: ANTLR 4 Tool
Implementation-Version: %s
Built-By: %s
Build-Jdk: 1.6
Created-By: http://www.bildtool.org
        """ % (VERSION, os.getlogin())
    # unjar required libraries
    unjar("runtime/Java/lib/org.abego.treelayout.core.jar", trgdir="out")
    unjar(os.path.join(JARCACHE, "antlr-3.5.1-complete.jar"), trgdir="out")
    # pull in target templates
    for t in TARGETS:
        trgdir = "out/org/antlr/v4/tool/templates/codegen/" + t
        mkdir(trgdir)
        copyfile(TARGETS[t] + "/tool/resources/org/antlr/v4/tool/templates/codegen/" + t + "/" + t + ".stg",
                 trgdir)
    jarfile = "dist/antlr4-" + VERSION + "-complete.jar"
    jar(jarfile, srcdir="out", manifest=manifest)
    print "Generated " + jarfile


def mkjar_runtime():
    # out/... dir is full of tool-related stuff, make special dir out/runtime
    # unjar required library
    unjar("runtime/Java/lib/org.abego.treelayout.core.jar", trgdir="out/runtime")
    cp = uniformpath("out/runtime") + os.pathsep + \
         "runtime/Java/lib/org.abego.treelayout.core.jar"
    args = ["-Xlint", "-Xlint:-serial", "-g"]
    srcpath = ["gen4", "runtime/JavaAnnotations/src", "runtime/Java/src"]
    args = ["-Xlint", "-Xlint:-serial", "-g", "-sourcepath", string.join(srcpath, os.pathsep)]
    for sp in srcpath:
        javac(sp, "out", version="1.6", cp=cp, args=args)
    manifest = \
        """Implementation-Vendor: ANTLR
Implementation-Title: ANTLR 4 Runtime
Implementation-Version: %s
Built-By: %s
Build-Jdk: 1.6
Created-By: http://www.bildtool.org
        """ % (VERSION, os.getlogin())
    jarfile = "dist/antlr4-" + VERSION + ".jar"
    jar(jarfile, srcdir="out/runtime", manifest=manifest)
    print "Generated " + jarfile

def mkjar():
    mkjar_complete()
    # put it in JARCARCHE too so bild can find it during antlr4()
    copyfile(src="dist/antlr4-" + VERSION + "-complete.jar", trg=JARCACHE)
    # rebuild/bootstrap XPath with 4.4 so it can use 4.4 runtime (gen'd with 4.3 at this point)
    rmdir("gen4/org/antlr/v4/runtime/tree/xpath")  # kill 4.3-generated version
    antlr4("runtime/Java/src/org/antlr/v4/runtime/tree/xpath", "gen4", version=VERSION,
           package="org.antlr.v4.runtime.tree.xpath")
    compile()
    mkjar_complete()  # make it again with up to date XPath lexer
    mkjar_runtime()  # now build the runtime jar


def tests():
    require(mkjar)
    junit_jar, hamcrest_jar = load_junitjars()
    cp = uniformpath("dist/antlr4-" + VERSION + "-complete.jar") + os.pathsep + \
         uniformpath("out/test/Java") + os.pathsep + \
         junit_jar + os.pathsep + hamcrest_jar
    properties = ["-D%s=%s" % (p, test_properties[p]) for p in test_properties]
    args = ["-Xlint", "-Xlint:-serial", "-g"]
    javac("tool/test", "out/test/Java", version="1.6", cp=cp, args=args)  # all targets can use org.antlr.v4.test.*
    for t in TARGETS:
        print "Test %7s --------------" % t
        # Prefix CLASSPATH with individual target tests
        cp = uniformpath(TARGETS[t] + "/tool/test") + os.pathsep + cp
        javac(TARGETS[t] + "/tool/test", "out/test/" + t, version="1.6", cp=cp, args=args)
        junit("out/test/" + t, cp=cp, verbose=False, args=properties)

def all():
    clean(True)
    mkjar()
    tests()
    mkdoc()
    mksrc()
    install()
    clean()

def install():
    mvn_install("dist/antlr4-" + VERSION + "-complete.jar",
        "dist/antlr4-" + VERSION + "-complete-sources.jar",
        "dist/antlr4-" + VERSION + "-complete-javadoc.jar",
        "org.antlr",
        "antlr4",
        VERSION)
    mvn_install("dist/antlr4-" + VERSION + ".jar",
        "dist/antlr4-" + VERSION + "-sources.jar",
        "dist/antlr4-" + VERSION + "-javadoc.jar",
        "org.antlr",
        "antlr4-runtime",
        VERSION)

def clean(dist=False):
    if dist:
        rmdir("dist")
    rmdir("out")
    rmdir("gen3")
    rmdir("gen4")
    rmdir("doc")


def mksrc():
    srcpath = "runtime/Java/src/org"
    jarfile = "dist/antlr4-" + VERSION + "-sources.jar"
    zip(jarfile, srcpath)
    print "Generated " + jarfile
    jarfile = "dist/antlr4-" + VERSION + "-complete-sources.jar"
    srcpaths = [ srcpath, "gen3/org", "gen4/org", "runtime/JavaAnnotations/src/org", "tool/src/org"]
    zip(jarfile, srcpaths)
    print "Generated " + jarfile


def mkdoc():
    # add a few source dirs to reduce the number of javadoc errors
    # JavaDoc needs antlr annotations source code
    mkdir("out/Annotations")
    download("http://search.maven.org/remotecontent?filepath=org/antlr/antlr4-annotations/4.3/antlr4-annotations-4.3-sources.jar", "out/Annotations")
    unjar("out/Annotations/antlr4-annotations-4.3-sources.jar", trgdir="out/Annotations")
    # JavaDoc needs abego treelayout source code
    mkdir("out/TreeLayout")
    download("http://search.maven.org/remotecontent?filepath=org/abego/treelayout/org.abego.treelayout.core/1.0.1/org.abego.treelayout.core-1.0.1-sources.jar", "out/TreeLayout")
    unjar("out/TreeLayout/org.abego.treelayout.core-1.0.1-sources.jar", trgdir="out/TreeLayout")
    # JavaDoc needs antlr runtime 3.5.2 source code
    mkdir("out/Antlr352Runtime")
    download("http://search.maven.org/remotecontent?filepath=org/antlr/antlr-runtime/3.5.2/antlr-runtime-3.5.2-sources.jar", "out/Antlr352Runtime")
    unjar("out/Antlr352Runtime/antlr-runtime-3.5.2-sources.jar", trgdir="out/Antlr352Runtime")
    # JavaDoc needs antlr ST4 source code
    mkdir("out/ST4")
    download("http://search.maven.org/remotecontent?filepath=org/antlr/ST4/4.0.8/ST4-4.0.8-sources.jar", "out/ST4")
    unjar("out/ST4/ST4-4.0.8-sources.jar", trgdir="out/ST4")
    # go!
    mkdir("doc/Java")
    mkdir("doc/JavaTool")
    dirs = ["runtime/Java/src"]
    dirs += ["out/Annotations"]
    dirs += ["out/TreeLayout"]
    exclude = ["org/antlr/runtime",
            "org/abego",
            "org/stringtemplate",
            "org/antlr/stringtemplate"]
    javadoc(srcdir=dirs, trgdir="doc/Java", packages="org.antlr.v4.runtime", exclude=exclude)
    dirs += ["gen3"]
    dirs += [TARGETS[t] + "/tool/src" for t in TARGETS]
    dirs += ["out/Antlr352Runtime"]
    dirs += ["out/ST4"]
    javadoc(srcdir=dirs, trgdir="doc/JavaTool", packages="org.antlr.v4", exclude=exclude)
    # build stack merge PredictionContext and ATNState images from DOT
    # DOT Images are in runtime/Java/src/main/dot/org/antlr/v4/runtime/atn/images/
    # Gen into E.g., doc/Java/org/antlr/v4/runtime/atn/images/SingletonMerge_DiffRootSamePar.svg
    mkdir("doc/Java/org/antlr/v4/runtime/atn/images")
    for f in glob.glob("runtime/Java/src/main/dot/org/antlr/v4/runtime/atn/images/*.dot"):
        dot(f, "doc/Java/org/antlr/v4/runtime/atn/images", format="svg")
    zip("dist/antlr4-" + VERSION + "-javadoc.jar", "doc/Java")
    zip("dist/antlr4-" + VERSION + "-complete-javadoc.jar", "doc/JavaTool")



processargs(globals())  # E.g., "python bild.py all"
