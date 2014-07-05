#!/usr/bin/env python
import sys
import os
sys.path.append(os.path.abspath("/Users/parrt/github/bild"))

from bilder import *

VERSION = "4.4"
PYTHON2_TARGET	= "../antlr4-python2"
PYTHON3_TARGET	= "../antlr4-python3"
CSHARP_TARGET	= "../antlr4cs"

TARGETS			= {"Python2":PYTHON2_TARGET, "Python3":PYTHON3_TARGET, "CSharp":CSHARP_TARGET}

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
	require(compile)
	junit_jar, hamcrest_jar = load_junitjars()
	cp = uniformpath("out")+os.pathsep+ \
		 os.path.join(JARCACHE,"antlr-3.5.1-complete.jar")+os.pathsep+ \
		 "runtime/Java/lib/org.abego.treelayout.core.jar"+os.pathsep+junit_jar+ \
		 os.pathsep+hamcrest_jar
	args = ["-Xlint", "-Xlint:-serial", "-g"]
	javac("tool/test", "out/test/Java", version="1.6", cp=cp, args=args)
	junit("out/test/Java", cp=cp)
	# for t in TARGETS:
	# 	javac(TARGETS[t]+"/tool/test", "out/test", version="1.6", cp=cp, args=args)

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
	toolsrc = ["tool/src"]+ [TARGETS[t]+"/tool/src" for t in TARGETS]
	toolsrc = string.join(toolsrc, ":")
	javadoc(srcdir=toolsrc, trgdir="doc/JavaTool", packages="org.antlr.v4")
	# for t in TARGETS:
	# 	javadoc(srcdir=TARGETS[t]+"/tool/src",
	# 			trgdir="doc/JavaTool",
	# 			packages="org.antlr.v4.codegen")
	# build stack merge PredictionContext and ATNState images from DOT
	# DOT Images are in runtime/Java/src/main/dot/org/antlr/v4/runtime/atn/images/
	# Gen into E.g., doc/Java/org/antlr/v4/runtime/atn/images/SingletonMerge_DiffRootSamePar.svg
	mkdir("doc/Java/org/antlr/v4/runtime/atn/images")
	for f in glob.glob("runtime/Java/src/main/dot/org/antlr/v4/runtime/atn/images/*.dot"):
		dot(f, "doc/Java/org/antlr/v4/runtime/atn/images", format="svg")
	zip("doc/antlr4-runtime.zip", "doc/Java")
	zip("doc/antlr4-tool.zip", "doc/JavaTool")

processargs(globals()) # E.g., "python bild.py all"
