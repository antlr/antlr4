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
    mvn(command="deploy:deploy-file",
        binjar=binjar,
        srcjar=srcjar,
        docjar=docjar,
        repositoryid="ossrh",
        artifactid="antlr4",
        pomfile="tool/pom.xml",
        url="https://oss.sonatype.org/content/repositories/snapshots")

    binjar = uniformpath("dist/antlr4-%s.jar" % VERSION)
    docjar = uniformpath("dist/antlr4-%s-javadoc.jar" % VERSION)
    srcjar = uniformpath("dist/antlr4-%s-sources.jar" % VERSION)
    mvn(command="deploy:deploy-file",
        binjar=binjar,
        srcjar=srcjar,
        docjar=docjar,
        repositoryid="ossrh", artifactid="antlr4-runtime",
        pomfile="runtime/Java/pom.xml",
        url="https://oss.sonatype.org/content/repositories/snapshots")


# deploy to maven central
def mvn_deploy():  # assumes that you have ~/.m2/settings.xml set up
    """
    mvn gpg:sign-and-deploy-file \
      -Durl=https://oss.sonatype.org/service/local/staging/deploy/maven2/ \
      -DrepositoryId=ossrh \
      -Dpackaging=jar \
      -DpomFile=/Users/parrt/antlr/code/antlr4/tool/pom.xml \
      -Dfile=/Users/parrt/antlr/code/antlr4/dist/antlr4-4.5-complete.jar \
      -Dsources=/Users/parrt/antlr/code/antlr4/dist/antlr4-4.5-complete-sources.jar \
      -Djavadoc=/Users/parrt/antlr/code/antlr4/dist/antlr4-4.5-complete-javadoc.jar

    mvn gpg:sign-and-deploy-file \
      -Durl=https://oss.sonatype.org/service/local/staging/deploy/maven2/ \
      -DrepositoryId=ossrh \
      -Dpackaging=jar \
      -DpomFile=/Users/parrt/antlr/code/antlr4/runtime/Java/pom.xml \
      -Dfile=/Users/parrt/antlr/code/antlr4/dist/antlr4-4.5.jar \
      -Dsources=/Users/parrt/antlr/code/antlr4/dist/antlr4-4.5-sources.jar \
      -Djavadoc=/Users/parrt/antlr/code/antlr4/dist/antlr4-4.5-javadoc.jar
    """
    # deploy the tool and Java runtime, it becomes antlr4 artifact at maven
    binjar = uniformpath("dist/antlr4-%s-complete.jar" % VERSION)
    docjar = uniformpath("dist/antlr4-%s-complete-javadoc.jar" % VERSION)
    srcjar = uniformpath("dist/antlr4-%s-complete-sources.jar" % VERSION)
    mvn(command="gpg:sign-and-deploy-file",
        binjar=binjar,
        srcjar=srcjar,
        docjar=docjar, repositoryid="ossrh",
        pomfile="tool/pom.xml",
        url="https://oss.sonatype.org/service/local/staging/deploy/maven2/")

    # deploy the runtime, it becomes antlr4-runtime artifact at maven
    binjar = uniformpath("dist/antlr4-%s.jar" % VERSION)
    docjar = uniformpath("dist/antlr4-%s-javadoc.jar" % VERSION)
    srcjar = uniformpath("dist/antlr4-%s-sources.jar" % VERSION)
    mvn("gpg:sign-and-deploy-file",
        binjar=binjar,
        srcjar=srcjar,
        docjar=docjar, repositoryid="ossrh",
        pomfile="runtime/Java/pom.xml",
        url="https://oss.sonatype.org/service/local/staging/deploy/maven2/")

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
