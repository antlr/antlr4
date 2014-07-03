#!/usr/local/bin/python
import sys
import os
sys.path.append(os.path.abspath("/Users/parrt/github/bild"))

from bilder import *

VERSION = "4.4"
PYTHON2_TARGET = "../antlr4-python2"
PYTHON3_TARGET = "../antlr4-python3"

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

def mkjar():
    require(compile)
    copytree(src="tool/resources", dst="out")
    manifest = """Version: %s
Main-Class: org.antlr.v4.Tool
""" % VERSION
    unjar("runtime/Java/lib/org.abego.treelayout.core.jar", trgdir="out")
    unjar(os.path.join(JARCACHE,"antlr-3.5.1-complete.jar"), trgdir="out")
    jar("dist/antlr-"+VERSION+"-complete.jar", srcdir="out", manifest=manifest)

def all():
    mkjar()

def clean():
    rmdir("out")
    rmdir("gen")
    rmdir("doc")

def mkdoc():
    mkdir("doc/Java")
    mkdir("doc/JavaTool")
    javadoc(srcdir="runtime/Java/src", trgdir="doc/Java", packages="org.antlr.v4.runtime")
    javadoc(srcdir="tool/src", trgdir="doc/JavaTool", packages="org.antlr.v4")
    # build stack merge PredictionContext and ATNState images from DOT
    # DOT Images are in runtime/Java/src/main/dot/org/antlr/v4/runtime/atn/images/
    # Gen into E.g., doc/Java/org/antlr/v4/runtime/atn/images/SingletonMerge_DiffRootSamePar.svg
    mkdir("doc/Java/org/antlr/v4/runtime/atn/images")
    for f in glob.glob("runtime/Java/src/main/dot/org/antlr/v4/runtime/atn/images/*.dot"):
        dot(f, "doc/Java/org/antlr/v4/runtime/atn/images", format="svg")
    zip("doc/antlr4-runtime.zip", "doc/Java")
    zip("doc/antlr4-tool.zip", "doc/JavaTool")

processargs(globals()) # E.g., "python bild.py all"
