#!/usr/bin/env python
import sys
import os
# requires support lib:
# 	https://raw.githubusercontent.com/parrt/bild/master/src/python/bilder.py
sys.path.append(os.path.abspath("/Users/parrt/github/bild/src/python"))
import string

from bilder import *

VERSION = "4.4"
JAVA_TARGET	= "."
PYTHON2_TARGET	= "../antlr4-python2"
PYTHON3_TARGET	= "../antlr4-python3"
CSHARP_TARGET	= "../antlr4cs"

# Properties needed to run Python[23] tests
test_properties = {
	"antlr-python2-python":"/usr/local/Cellar/python/2.7.5/bin/python2.7",
	"antlr-python2-runtime":uniformpath(PYTHON2_TARGET)+"/src",
	"antlr-python3-python":"/usr/local/Cellar/python3/3.4.1/bin/python3",
	"antlr-python3-runtime":uniformpath(PYTHON3_TARGET)+"/src",
}

# TARGETS			= {"Java":JAVA_TARGET, "Python2":PYTHON2_TARGET, "Python3":PYTHON3_TARGET, "CSharp":CSHARP_TARGET}
TARGETS	= {"Java":uniformpath(JAVA_TARGET),
		   "Python2":uniformpath(PYTHON2_TARGET),
		   "Python3":uniformpath(PYTHON3_TARGET),
		   "CSharp":uniformpath(CSHARP_TARGET)}

def parsers():
	antlr3("tool/src/org/antlr/v4/parse", "gen", package="org.antlr.v4.parse")
	antlr3("tool/src/org/antlr/v4/codegen", "gen", package="org.antlr.v4.codegen", args=["-lib","tool/src/org/antlr/v4/parse"])
	antlr4("runtime/Java/src/org/antlr/v4/runtime/tree/xpath", "gen", package="org.antlr.v4.runtime.tree.xpath")

def compile():
	require(parsers)
	cp = uniformpath("out")+os.pathsep+ \
		 os.path.join(JARCACHE,"antlr-3.5.1-complete.jar")+os.pathsep+ \
		 "runtime/Java/lib/org.abego.treelayout.core.jar"
	args = ["-Xlint", "-Xlint:-serial", "-g"]
	javac("runtime/JavaAnnotations/src/", "out", version="1.6", cp=cp, args=args)
	javac("runtime/Java/src", "out", version="1.6", cp=cp, args=args)
	javac("tool/src", "out", version="1.6", cp=cp, args=args)
	javac("gen", "out", version="1.6", cp=cp, args=args)
	# pull in targets
	for t in TARGETS:
		javac(TARGETS[t]+"/tool/src", "out", version="1.6", cp=cp, args=args)

def mkjar():
	require(compile)
	copytree(src="tool/resources", dst="out")
	manifest = \
"""Version: %s
Main-Class: org.antlr.v4.Tool
""" % VERSION
	# unjar required libraries
	unjar("runtime/Java/lib/org.abego.treelayout.core.jar", trgdir="out")
	unjar(os.path.join(JARCACHE,"antlr-3.5.1-complete.jar"), trgdir="out")
	# pull in target templates
	for t in TARGETS:
		copyfile(TARGETS[t]+"/tool/resources/org/antlr/v4/tool/templates/codegen/"+t+"/"+t+".stg",
				 "out/org/antlr/v4/tool/templates/codegen/"+t)
	jar("dist/antlr-"+VERSION+"-complete.jar", srcdir="out", manifest=manifest)

def tests():
	require(mkjar)
	junit_jar, hamcrest_jar = load_junitjars()
	cp = uniformpath("dist/antlr-"+VERSION+"-complete.jar")+os.pathsep+ \
		 uniformpath("out/test/Java")+os.pathsep+ \
		 string.join([uniformpath(TARGETS[t]+"/tool/test") for t in TARGETS],os.pathsep)+os.pathsep+ \
		 junit_jar+os.pathsep+hamcrest_jar
	properties = ["-D%s=%s" % (p, test_properties[p]) for p in test_properties]
	args = ["-Xlint", "-Xlint:-serial", "-g"]
	javac("tool/test", "out/test/Java", version="1.6", cp=cp, args=args) # all targets can use org.antlr.v4.test.*
	for t in TARGETS:
		print "Test %7s --------------" % t
		javac(TARGETS[t]+"/tool/test", "out/test/"+t, version="1.6", cp=cp, args=args)
		copytree(src=TARGETS[t]+"/tool/resources", dst="out/test/"+t)
		junit("out/test/"+t, cp=cp, verbose=False, args=properties)

def all():
	mkjar()
	tests()
	mkdoc()

def clean():
	rmdir("out")
	rmdir("gen")
	rmdir("doc")

def mkdoc():
	mkdir("doc/Java")
	mkdir("doc/JavaTool")
	javadoc(srcdir="runtime/Java/src", trgdir="doc/Java", packages="org.antlr.v4.runtime")
	toolsrc = [TARGETS[t]+"/tool/src" for t in TARGETS]
	toolsrc = string.join(toolsrc, ":")
	javadoc(srcdir=toolsrc, trgdir="doc/JavaTool", packages="org.antlr.v4")
	# build stack merge PredictionContext and ATNState images from DOT
	# DOT Images are in runtime/Java/src/main/dot/org/antlr/v4/runtime/atn/images/
	# Gen into E.g., doc/Java/org/antlr/v4/runtime/atn/images/SingletonMerge_DiffRootSamePar.svg
	mkdir("doc/Java/org/antlr/v4/runtime/atn/images")
	for f in glob.glob("runtime/Java/src/main/dot/org/antlr/v4/runtime/atn/images/*.dot"):
		dot(f, "doc/Java/org/antlr/v4/runtime/atn/images", format="svg")
	zip("doc/antlr4-runtime.zip", "doc/Java")
	zip("doc/antlr4-tool.zip", "doc/JavaTool")

processargs(globals()) # E.g., "python bild.py all"
