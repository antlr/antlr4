#!/usr/bin/env python
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
JAVA_TARGET	= "."
PYTHON2_TARGET	= "../antlr4-python2"
PYTHON3_TARGET	= "../antlr4-python3"
CSHARP_TARGET	= "../antlr4-csharp"

# Properties needed to run Python[23] tests
test_properties = {
	"antlr-python2-python":"/usr/local/Cellar/python/2.7.5/bin/python2.7",
	"antlr-python2-runtime":uniformpath(PYTHON2_TARGET)+"/src",
	"antlr-python3-python":"/usr/local/Cellar/python3/3.4.1/bin/python3",
	"antlr-python3-runtime":uniformpath(PYTHON3_TARGET)+"/src",
}

TARGETS	= {"Java":uniformpath(JAVA_TARGET),
		   "Python2":uniformpath(PYTHON2_TARGET),
		   "Python3":uniformpath(PYTHON3_TARGET),
		   #"CSharp":uniformpath(CSHARP_TARGET)
		  }

def parsers():
	antlr3("tool/src/org/antlr/v4/parse", "gen3", package="org.antlr.v4.parse")
	antlr3("tool/src/org/antlr/v4/codegen", "gen3", package="org.antlr.v4.codegen", args=["-lib",uniformpath("gen3/org/antlr/v4/parse")])
	antlr4("runtime/Java/src/org/antlr/v4/runtime/tree/xpath", "gen4", version="4.4", package="org.antlr.v4.runtime.tree.xpath")

def compile():
	require(parsers)
	cp = uniformpath("out")+os.pathsep+ \
		 os.path.join(JARCACHE,"antlr-3.5.1-complete.jar")+os.pathsep+ \
		 "runtime/Java/lib/org.abego.treelayout.core.jar"+os.pathsep
	if os.path.exists(JARCACHE+"/antlr-4.4-complete.jar"):
		 cp += JARCACHE+"/antlr-4.4-complete.jar"
	srcpath = ["gen3", "gen4", "runtime/JavaAnnotations/src", "runtime/Java/src", "tool/src"]
	args = ["-Xlint", "-Xlint:-serial", "-g", "-sourcepath", string.join(srcpath, os.pathsep)]
	for sp in srcpath:
		javac(sp, "out", version="1.6", cp=cp, args=args)
	# pull in targets
	for t in TARGETS:
		javac(TARGETS[t]+"/tool/src", "out", version="1.6", cp=cp, args=args)

def mkjar_complete():
	require(compile)
	copytree(src="tool/resources", trg="out") # messages, Java code gen, etc...
	manifest = \
"""Main-Class: org.antlr.v4.Tool
Implementation-Vendor: ANTLR
Implementation-Title: ANTLR 4 Tool
Implementation-Version: %s
Built-By: %s
Build-Jdk: 1.6
Created-By: http://www.bildtool.org
""" % (VERSION,os.getlogin())
	# unjar required libraries
	unjar("runtime/Java/lib/org.abego.treelayout.core.jar", trgdir="out")
	v3runtime = "antlr-3.5.2-runtime.jar"
	download("http://www.antlr3.org/download/"+v3runtime, JARCACHE)
	unjar(os.path.join(JARCACHE, v3runtime), trgdir="out")
	STruntime = "ST-4.0.8-no-antlr3-runtime.jar"
	download("http://www.stringtemplate.org/download/"+STruntime, JARCACHE)
	unjar(os.path.join(JARCACHE, STruntime), trgdir="out")
	# pull in target templates
	for t in TARGETS:
		trgdir = "out/org/antlr/v4/tool/templates/codegen/"+t
		mkdir(trgdir)
		copyfile(TARGETS[t]+"/tool/resources/org/antlr/v4/tool/templates/codegen/"+t+"/"+t+".stg",
				 trgdir)
	jarfile = "dist/antlr-"+VERSION+"-complete.jar"
	jar(jarfile, srcdir="out", manifest=manifest)
	print "Generated "+jarfile

def mkjar_runtime():
	# out/... dir is full of tool-related stuff, make special dir out/runtime
	# unjar required library
	unjar("runtime/Java/lib/org.abego.treelayout.core.jar", trgdir="out/runtime")
	cp = uniformpath("out/runtime")+os.pathsep+ \
		 "runtime/Java/lib/org.abego.treelayout.core.jar"
	args = ["-Xlint", "-Xlint:-serial", "-g"]
	srcpath = ["gen4", "runtime/JavaAnnotations/src", "runtime/Java/src"]
	args = ["-Xlint", "-Xlint:-serial", "-g", "-sourcepath", string.join(srcpath, os.pathsep)]
	for sp in srcpath:
		javac(sp, "out/runtime", version="1.6", cp=cp, args=args)
	manifest = \
"""Implementation-Vendor: ANTLR
Implementation-Title: ANTLR 4 Runtime
Implementation-Version: %s
Built-By: %s
Build-Jdk: 1.6
Created-By: http://www.bildtool.org
""" % (VERSION,os.getlogin())
	jarfile = "dist/antlr-runtime-" + VERSION + ".jar"
	jar(jarfile, srcdir="out/runtime", manifest=manifest)
	print "Generated "+jarfile

def mkjar():
	mkjar_complete()
	# put it in JARCARCHE too so bild can find it during antlr4()
	copyfile(src="dist/antlr-4.4-complete.jar", trg=JARCACHE)
	# rebuild/bootstrap XPath with 4.4 so it can use 4.4 runtime (gen'd with 4.3 at this point)
	rmdir("gen4/org/antlr/v4/runtime/tree/xpath") # kill 4.3-generated version
	antlr4("runtime/Java/src/org/antlr/v4/runtime/tree/xpath", "gen4", version="4.4",  package="org.antlr.v4.runtime.tree.xpath")
	compile()
	mkjar_complete() # make it again with up to date XPath lexer
	mkjar_runtime()	 # now build the runtime jar

def tests():
	require(mkjar)
	junit_jar, hamcrest_jar = load_junitjars()
	cp = uniformpath("dist/antlr-"+VERSION+"-complete.jar")+os.pathsep+ \
		 uniformpath("out/test/Java")+os.pathsep+ \
		 junit_jar+os.pathsep+hamcrest_jar
	properties = ["-D%s=%s" % (p, test_properties[p]) for p in test_properties]
	args = ["-Xlint", "-Xlint:-serial", "-g"]
	javac("tool/test", "out/test/Java", version="1.6", cp=cp, args=args) # all targets can use org.antlr.v4.test.*
	for t in TARGETS:
		print "Test %7s --------------" % t
		# Prefix CLASSPATH with individual target tests
		cp = uniformpath(TARGETS[t]+"/tool/test") + os.pathsep + cp
		javac(TARGETS[t]+"/tool/test", "out/test/"+t, version="1.6", cp=cp, args=args)
		junit("out/test/"+t, cp=cp, verbose=False, args=properties)

def mkdoc():
	mkdir("doc/Java")
	mkdir("doc/JavaTool")
	javadoc(srcdir=["runtime/Java/src","runtime/JavaAnnotations/src"],
			trgdir="doc/Java", classpath=JARCACHE+"/antlr-4.4-complete.jar",
			title="ANTLR 4.4 Runtime",
			packages="org.antlr.v4.runtime")
	toolsrc = [TARGETS[t]+"/tool/src" for t in TARGETS]
	toolsrc = string.join(toolsrc, ":")
	javadoc(srcdir=toolsrc, trgdir="doc/JavaTool",
			classpath=JARCACHE+"/antlr-4.4-complete.jar",
			title="ANTLR 4.4 Tool",
			packages="org.antlr.v4")
	# build stack merge PredictionContext and ATNState images from DOT
	# DOT Images are in runtime/Java/src/main/dot/org/antlr/v4/runtime/atn/images/
	# Gen into E.g., doc/Java/org/antlr/v4/runtime/atn/images/SingletonMerge_DiffRootSamePar.svg
	mkdir("doc/Java/org/antlr/v4/runtime/atn/images")
	for f in glob.glob("runtime/Java/src/main/dot/org/antlr/v4/runtime/atn/images/*.dot"):
		dot(f, "doc/Java/org/antlr/v4/runtime/atn/images", format="svg")
	zip("doc/antlr4-runtime.zip", "doc/Java")
	zip("doc/antlr4-tool.zip", "doc/JavaTool")

def all():
	mkjar()
	tests()
	mkdoc()

def clean():
	rmdir("out")
	rmdir("gen3")
	rmdir("gen4")
	rmdir("doc")

processargs(globals()) # E.g., "python bild.py all"
