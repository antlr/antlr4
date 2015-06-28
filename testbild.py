#!/usr/bin/env python
from collections import OrderedDict

"""
This script uses my experimental build tool http://www.bildtool.org

In order to build the complete ANTLR4 product with Java, CSharp, Python 2/3, and JavaScript
targets, do the following from a UNIX command line.  Windows build using this script
is not yet supported.

You will also need python 2.7, python 3.4, node.js and mono (on Mac/Linux)

mkdir -p /usr/local/antlr # somewhere appropriate where you want to install stuff
cd /usr/local/antlr
git clone git@github.com:antlr/antlr4.git
cd antlr4
./testbild.py tests

This script must be run from the main antlr4 directory.
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

BOOTSTRAP_VERSION = "4.5"
VERSION = "4.5.1"

TARGETS = ["Java", "CSharp", "Python2", "Python3", "JavaScript"]

TOOL_PATH = []
TEST_PATH = []

BUILD = "build"

JUNIT = ["junit-4.11.jar", "hamcrest-core-1.3.jar"]

class Goal:
    def __init__(self,name,srcdirs,dependencies=[],usesmods=[],resources=[]):
        self.name = name
        self.srcdirs = srcdirs
        self.dependencies = dependencies
        self.usesmods = usesmods
        self.resources = resources

    def compile(self):
        mycompile(self.name,
                  self.srcdirs,
                  [os.path.join(JARCACHE,d) for d in self.dependencies] +
                  [os.path.join(BUILD,d) for d in self.usesmods])


goals = []

def goal(name,srcdirs,dependencies=[],usesmods=[],resources=[]):
    global goals
    goals += [Goal(name,srcdirs,dependencies,usesmods,resources)]


goal(name="runtime",
     srcdirs=["runtime/Java/src","gen4"],
     dependencies=["antlr-"+BOOTSTRAP_VERSION+"-complete.jar"])

goal(name="runtime-test",
     srcdirs=["runtime-testsuite/test"],
     dependencies=["antlr-"+BOOTSTRAP_VERSION+"-complete.jar"]+JUNIT,
     usesmods=["runtime", "tool"],
     resources=["runtime/CSharp/Antlr4.Runtime/Antlr4.Runtime.mono.csproj",
                "runtime/JavaScript/src",
                "runtime/Python2/src",
                "runtime/Python3/src",
                "runtime/Java/src"
                ])

goal(name="tool",
     srcdirs=["gen3", "tool/src"],
     dependencies=["antlr-3.5.2-runtime.jar", "ST-4.0.8.jar"],
     usesmods=["runtime"],
     resources=["tool/resources"])

RUNTIME_SRC = ["runtime/Java/src","gen4"]
RUNTIME_DEP = ["antlr-"+BOOTSTRAP_VERSION+"-complete.jar"]
RUNTIME_RES = []

RUNTIME_TEST_SRC = ["runtime-testsuite/test"]
RUNTIME_TEST_DEP = RUNTIME_DEP + JUNIT
RUNTIME_TEST_RES = [
    "runtime/CSharp/Antlr4.Runtime/Antlr4.Runtime.mono.csproj",
    "runtime/JavaScript/src",
    "runtime/Python2/src",
    "runtime/Python3/src",
    "runtime/Java/src"
]
RUNTIME_TEST_MOD_DEP = ["runtime", "tool"]
RUNTIME_TEST_SKIP = [
    'org/antlr/v4/test/runtime/javascript/firefox',
    'org/antlr/v4/test/runtime/javascript/chrome',
    'org/antlr/v4/test/runtime/javascript/explorer',
    'org/antlr/v4/test/runtime/javascript/safari',
]

TOOL_SRC = ["gen3", "tool/src"]
TOOL_DEP = ["antlr-3.5.2-runtime.jar", "ST-4.0.8.jar"]
TOOL_RES = ["tool/resources"]
TOOL_MOD_DEP = ["runtime"]

TOOLTEST_SRC = ["tool-testsuite/test"]
TOOLTEST_DEP = ["antlr-3.5.2-runtime.jar", "ST-4.0.8.jar"] + JUNIT
TOOLTEST_RES = ["tool-testsuite/test/org/antlr/v4/test/tool"] + TOOL_RES # some grammars in the code area
TOOLTEST_MOD_DEP = ["tool", "runtime", "runtime-tests"]

def download_libs():
    global junit_jar, hamcrest_jar
    junit_jar, hamcrest_jar = load_junitjars()
    depends("http://www.antlr3.org/download/antlr-3.5.2-runtime.jar")
    depends("http://www.stringtemplate.org/download/ST-4.0.8.jar")
    copyfile(src="runtime/Java/lib/org.abego.treelayout.core.jar", trg=JARCACHE)


def mycompile(goal,srcpaths,dependencies,skip=[]):
    args = ["-Xlint", "-Xlint:-serial", "-g"]
    jars=None
    trgdir = os.path.join(BUILD, goal)
    if len(dependencies)>0:
        dependencies = [uniformpath(d) for d in dependencies] # need absolute paths for CLASSPATH
        jars = string.join(dependencies, os.pathsep)
    jars += os.pathsep+trgdir # we can also see the code we're generating
    args += ["-sourcepath", string.join(srcpaths, ":")] # javac says always ':'
    for src in srcpaths:
        javac(srcdir=src, trgdir=trgdir, version="1.6", cp=jars, args=args, skip=skip)

def parsers():
    antlr3("tool/src/org/antlr/v4/parse", "gen3", version="3.5.2", package="org.antlr.v4.parse")
    antlr3("tool/src/org/antlr/v4/codegen", "gen3", version="3.5.2", package="org.antlr.v4.codegen",
           args=["-lib", uniformpath("gen3/org/antlr/v4/parse")])
    antlr4("runtime/Java/src/org/antlr/v4/runtime/tree/xpath", "gen4",
           version=BOOTSTRAP_VERSION, package="org.antlr.v4.runtime.tree.xpath")

def runtime():
    require(parsers)
    mycompile("runtime",
              RUNTIME_SRC,
              [os.path.join(JARCACHE,d) for d in RUNTIME_DEP])

def tool():
    require(parsers)
    require(runtime)
    mycompile("tool",
              TOOL_SRC,
              [os.path.join(JARCACHE,d) for d in TOOL_DEP] +
              [os.path.join(BUILD,d) for d in TOOL_MOD_DEP])


def runtime_tests():
    require(runtime)
    mycompile("runtime-tests",
              RUNTIME_TEST_SRC,
              [os.path.join(JARCACHE,d) for d in RUNTIME_TEST_DEP] +
              [os.path.join(BUILD,d) for d in RUNTIME_TEST_MOD_DEP],
              skip=RUNTIME_TEST_SKIP)
    cp = [os.path.join(JARCACHE,d) for d in RUNTIME_TEST_DEP] +\
         RUNTIME_TEST_RES +\
         [os.path.join(BUILD,d) for d in RUNTIME_TEST_MOD_DEP]
    cp = [uniformpath(p) for p in cp]
    for target in TARGETS:
        junit(os.path.join(BUILD,"runtime-tests/org/antlr/v4/test/runtime/"+target.lower()), cp=string.join(cp,os.pathsep), verbose=False)


def tool_tests():
    require(runtime_tests)
    require(tool)
    mycompile("tool-tests",
              TOOLTEST_SRC,
              [os.path.join(JARCACHE,d) for d in TOOLTEST_DEP] +
              [os.path.join(BUILD,d) for d in TOOLTEST_MOD_DEP])
    cp = [os.path.join(JARCACHE,d) for d in TOOLTEST_DEP] +\
         TOOLTEST_RES +\
         [os.path.join(BUILD,d) for d in TOOLTEST_MOD_DEP]
    cp = [uniformpath(p) for p in cp]
    junit(os.path.join(BUILD,"tool-tests"), cp=string.join(cp,os.pathsep), verbose=False)

def regen_tests():
    """
    Generate all runtime Test*.java files for all targets into
    runtime-testsuite/test/org/antlr/v4/test/runtime/targetname
    """


def clean(dist=True):
    if dist:
        rmdir("dist")
    rmdir(BUILD)
    rmdir("gen3")
    rmdir("gen4")
    rmdir("doc")


def all():
    download_libs()
    runtime()
    tool()


def depends(url):
    download(url, JARCACHE)

processargs(globals())  # E.g., "python bild.py all"
