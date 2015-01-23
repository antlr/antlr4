#!/usr/bin/env python
import os

"""
This script uses my experimental build tool http://www.bildtool.org

This script deploys artifacts created by bild.py.

Windows build using this script is not yet supported.

cd /usr/local/antlr/antlr4
./deploy.py maven_snapshot

or

./deploy.py maven

or

./deploy.py pypi

or

./deploy.py # does "all"

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

VERSION = "4.5"
PYTHON2_TARGET = "../antlr4-python2"
PYTHON3_TARGET = "../antlr4-python3"
CSHARP_TARGET = "../antlr4-csharp"
JAVASCRIPT_TARGET = "../antlr4-javascript"


def mvn_snapshot():  # assumes that you have ~/.m2/settings.xml set up
    binjar = uniformpath("dist/antlr4-%s-complete.jar" % VERSION)
    docjar = uniformpath("dist/antlr4-%s-complete-javadoc.jar" % VERSION)
    srcjar = uniformpath("dist/antlr4-%s-complete-sources.jar" % VERSION)
    mvn_deploy(binjar, docjar, srcjar, repositoryid="ossrh", groupid="org.antlr",
               artifactid="antlr4", pomfile="tool/pom.xml", version=VERSION)

    binjar = uniformpath("dist/antlr4-%s.jar" % VERSION)
    docjar = uniformpath("dist/antlr4-%s-javadoc.jar" % VERSION)
    srcjar = uniformpath("dist/antlr4-%s-sources.jar" % VERSION)
    mvn_deploy(binjar, docjar, srcjar, repositoryid="ossrh", groupid="org.antlr",
               artifactid="antlr4-runtime", pomfile="runtime/Java/pom.xml", version=VERSION)


def mvn(): # TODO
    pass


def pypi(): # assumes that you have ~/.pypirc set up
    cmd = ["python", "setup.py", "register", "-r", "pypi"]
    savedir= os.getcwd()
    try:
        os.chdir(uniformpath(PYTHON2_TARGET))
        exec_and_log(cmd)
        os.chdir(uniformpath(PYTHON3_TARGET))
        exec_and_log(cmd)
    finally:
        os.chdir(savedir)

    # Upload the source distribution and the Windows installer to PyPI
    cmd = ["python", "setup.py", "sdist", "bdist_wininst", "upload", "-r", "pypi"]
    savedir= os.getcwd()
    try:
        os.chdir(uniformpath(PYTHON2_TARGET))
        exec_and_log(cmd)
        os.chdir(uniformpath(PYTHON3_TARGET))
        exec_and_log(cmd)
    finally:
        os.chdir(savedir)


def nuget(): # TODO
    pass


def website():
    """
    Push all jars, source, target artifacts etc.
    """
    # There is no JavaScript project i.e; nothing to "build" it's just a bunch of files that I zip.
    pass


def all():  # Note: building artifacts is in a separate file bild.py
    mvn()
    pypi()
    nuget()
    website()


processargs(globals())  # E.g., "python bild.py all"
