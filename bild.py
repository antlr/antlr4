import sys
import os
sys.path.append(os.path.abspath("/Users/parrt/github/bild"))

from bilder import *

VERSION = 4.4

#CLASSPATH = JARCACHE+"/*" +os.pathsep+ os.environ['CLASSPATH']

def parsers():
    antlr3("tool/src/org/antlr/v4/parse", "gen", package="org.antlr.v4.parse")
    antlr3("tool/src/org/antlr/v4/codegen", "gen", package="org.antlr.v4.codegen", args=["-lib","tool/src/org/antlr/v4/parse"])
    antlr4("runtime/Java/src/org/antlr/v4/runtime/tree/xpath", "gen", package="org.antlr.v4.runtime.tree.xpath")

def compile():
	require(parsers)
	javac("src/java", "out")
	javac("gen", "out")

def mkjar():
	require(compile)
	copytree(src="resources", dst="out/resources")
	manifest = """Version: %s
Main-Class: org.foo.Blort
""" % VERSION
	jar("dist/app.jar", srcdir="out", manifest=manifest)

def all():
	mkjar()

def clean():
	rmdir("out")
	rmdir("gen")

parsers()
