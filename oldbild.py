#!/usr/bin/env python
import os
import string
from collections import OrderedDict
import shutil

"""
This script uses my experimental build tool http://www.bildtool.org

In order to build the complete ANTLR4 product with Java, CSharp, Python 2/3, and JavaScript
targets, do the following from a UNIX command line.  Windows build using this script
is not yet supported.

You will also need python 2.7, python 3.4, node.js and mono (on Mac/Linux)

!!!You might need to set path values in test_properties dictionary below to ensure non Java targets tests run.!!!

mkdir -p /usr/local/antlr # somewhere appropriate where you want to install stuff
cd /usr/local/antlr
git clone git@github.com:antlr/antlr4.git
git clone git@github.com:antlr/antlr4-python3.git
git clone git@github.com:antlr/antlr4-python2.git
git clone git@github.com:antlr/antlr4-csharp.git
git clone git@github.com:antlr/antlr4-javascript.git
cd antlr4
./bild.py tests

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
JAVA_TARGET = "."
PYTHON2_TARGET = "../antlr4-python2"
PYTHON3_TARGET = "../antlr4-python3"
CSHARP_TARGET = "../antlr4-csharp"
JAVASCRIPT_TARGET = "../antlr4-javascript"

# Properties needed to run Python[23] tests
test_properties = {
"antlr-python2-runtime": uniformpath(PYTHON2_TARGET) + "/src",
"antlr-python3-runtime": uniformpath(PYTHON3_TARGET) + "/src",
"antlr-javascript-runtime": uniformpath(JAVASCRIPT_TARGET) + "/src",
"antlr-csharp-runtime-project": uniformpath(CSHARP_TARGET) + "/runtime/CSharp/Antlr4.Runtime/Antlr4.Runtime.mono.csproj"
}

TARGETS = OrderedDict([
    ("Java", uniformpath(JAVA_TARGET)),
    ("CSharp",uniformpath(CSHARP_TARGET)),
    ("Python2", uniformpath(PYTHON2_TARGET)),
    ("Python3", uniformpath(PYTHON3_TARGET)),
    ("JavaScript",uniformpath(JAVASCRIPT_TARGET))
])


def parsers():
    antlr3("tool/src/org/antlr/v4/parse", "gen3", package="org.antlr.v4.parse")
    antlr3("tool/src/org/antlr/v4/codegen", "gen3", package="org.antlr.v4.codegen",
           args=["-lib", uniformpath("gen3/org/antlr/v4/parse")])
    antlr4("runtime/Java/src/org/antlr/v4/runtime/tree/xpath", "gen4",
           version=BOOTSTRAP_VERSION, package="org.antlr.v4.runtime.tree.xpath")

def compile():
    require(parsers)
    cp = uniformpath("out") + os.pathsep + \
         os.path.join(JARCACHE, "antlr-3.5.1-complete.jar") + os.pathsep + \
         "runtime/Java/lib/org.abego.treelayout.core.jar" + os.pathsep
    srcpath = ["gen3", "gen4", "runtime/Java/src", "tool/src"]
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
        "Main-Class: org.antlr.v4.Tool\n" +\
        "Implementation-Title: ANTLR 4 Tool\n" +\
        "Implementation-Version: %s\n" +\
        "Implementation-Vendor: ANTLR\n" +\
        "Implementation-Vendor-Id: org.antlr\n" +\
        "Built-By: %s\n" +\
        "Build-Jdk: 1.6\n" +\
        "Created-By: http://www.bildtool.org\n" +\
        "\n"
    manifest = manifest % (VERSION, os.getlogin())
    # unjar required libraries
    unjar("runtime/Java/lib/org.abego.treelayout.core.jar", trgdir="out")
    download("http://www.antlr3.org/download/antlr-3.5.1-runtime.jar", JARCACHE)
    unjar(os.path.join(JARCACHE, "antlr-3.5.1-runtime.jar"), trgdir="out")
    download("http://www.stringtemplate.org/download/ST-4.0.8.jar", JARCACHE)
    unjar(os.path.join(JARCACHE, "ST-4.0.8.jar"), trgdir="out")
    # pull in target templates
    for t in TARGETS:
        trgdir = "out/org/antlr/v4/tool/templates/codegen/" + t
        mkdir(trgdir)
        copyfile(TARGETS[t] + "/tool/resources/org/antlr/v4/tool/templates/codegen/" + t + "/" + t + ".stg",
                 trgdir)
    jarfile = "dist/antlr4-" + VERSION + "-complete.jar"
    jar(jarfile, srcdir="out", manifest=manifest)
    print_and_log("Generated " + jarfile)


def mkjar_runtime():
    # out/... dir is full of tool-related stuff, make special dir out/runtime
    # unjar required library
    unjar("runtime/Java/lib/org.abego.treelayout.core.jar", trgdir="out/runtime")
    cp = uniformpath("out/runtime") + os.pathsep + \
         "runtime/Java/lib/org.abego.treelayout.core.jar"
    srcpath = ["gen4", "runtime/Java/src"]
    args = ["-nowarn", "-Xlint", "-Xlint:-serial", "-g", "-sourcepath", string.join(srcpath, os.pathsep)]
    for sp in srcpath:
        javac(sp, "out/runtime", version="1.6", cp=cp, args=args)
    # Prefix of Bundle- is OSGi cruft; it's not everything so we wrap with make_osgi_ready()
    # Declan Cox describes osgi ready jar https://github.com/antlr/antlr4/pull/689.
    manifest = \
        "Implementation-Vendor: ANTLR\n" +\
        "Implementation-Vendor-Id: org.antlr\n" +\
        "Implementation-Title: ANTLR 4 Runtime\n" +\
        "Implementation-Version: %s\n" +\
        "Built-By: %s\n" +\
        "Build-Jdk: %s\n" +\
        "Created-By: http://www.bildtool.org\n" +\
        "Bundle-Description: The ANTLR 4 Runtime\n" +\
        "Bundle-DocURL: http://www.antlr.org\n" +\
        "Bundle-License: http://www.antlr.org/license.html\n" +\
        "Bundle-Name: ANTLR 4 Runtime\n" +\
        "Bundle-SymbolicName: org.antlr.antlr4-runtime-osgi\n" +\
        "Bundle-Vendor: ANTLR\n" +\
        "Bundle-Version: %s\n" +\
        "\n"
    manifest = manifest % (VERSION, os.getlogin(), get_java_version(), VERSION)
    jarfile = "dist/antlr4-" + VERSION + ".jar"
    jar(jarfile, srcdir="out/runtime", manifest=manifest)
    print "Generated " + jarfile
    osgijarfile = "dist/antlr4-" + VERSION + "-osgi.jar"
    make_osgi_ready(jarfile, osgijarfile)
    os.rename(osgijarfile, jarfile) # copy back onto old jar
    print_and_log("Made jar OSGi-ready " + jarfile)


def mkjar(): # if called as root target
    rmdir("out")
    _mkjar()


def _mkjar(): # don't wipe out out dir if we know it's done like from all()
    mkjar_complete()
    # put it in JARCARCHE too so bild can find it during antlr4()
    copyfile(src="dist/antlr4-" + VERSION + "-complete.jar", trg=JARCACHE+"/antlr-"+VERSION+"-complete.jar") # note mvn wants antlr4-ver-... but I want antlr-ver-...

    # rebuild/bootstrap XPath with this version so it can use current runtime (gen'd with previous ANTLR at this point)
    log("rebuilding XPath with "+VERSION)
    print("rebuilding XPath with "+VERSION)
    # kill previous-version-generated code
    os.remove("out/org/antlr/v4/runtime/tree/xpath/XPathLexer.class")
    os.remove("gen4/org/antlr/v4/runtime/tree/xpath/XPathLexer.java")
    antlr4("runtime/Java/src/org/antlr/v4/runtime/tree/xpath", "gen4", version=VERSION,
           package="org.antlr.v4.runtime.tree.xpath")
    args = ["-Xlint", "-Xlint:-serial", "-g", "-sourcepath", "gen4"]
    javac("gen4", "out", version="1.6", cp=uniformpath("out"), args=args) # force recompile of XPath stuff

    mkjar_complete()  # make it again with up to date XPath lexer
    mkjar_runtime()   # now build the runtime jar


def javascript():
    # No build to do. Just zip up the sources
    srcpath = uniformpath(JAVASCRIPT_TARGET+"/src")
    srcfiles = allfiles(srcpath, "*.js") + allfiles(srcpath, "*.json")
    zipfile = "dist/antlr-javascript-runtime-"+VERSION+".zip"
    if not isstale(src=newest(srcfiles), trg=zipfile):
        return
    zip(zipfile, srcpath)
    print_and_log("Generated " + zipfile)


def csharp():
    # For C#, there are 2 equivalent projects: a VisualStudio one and a Xamarin one.
    # You can build on windows using msbuild and on mono using xbuild and pointing to the corresponding runtime project.
    # kill previous ones manually as "xbuild /t:Clean" didn't seem to do it
    bindll=uniformpath(CSHARP_TARGET)+"/runtime/CSharp/Antlr4.Runtime/bin/net20/Release/Antlr4.Runtime.dll"
    objdll=uniformpath(CSHARP_TARGET)+"/runtime/CSharp/Antlr4.Runtime/obj/net20/Release/Antlr4.Runtime.dll"
    rmfile(bindll)
    rmfile(objdll)
    # now build
    projfile = uniformpath(CSHARP_TARGET)+"/runtime/CSharp/Antlr4.Runtime/Antlr4.Runtime.mono.csproj"
    cmd = ["xbuild", "/p:Configuration=Release", projfile]
    exec_and_log(cmd)
    # zip it up to get a version number in there
    zipfile = "dist/antlr-csharp-runtime-"+VERSION+".zip"
    rmfile(zipfile)
    cmd = ["zip", "--junk-paths", zipfile, bindll]
    exec_and_log(cmd)
    print_and_log("Generated " + zipfile)


def python_sdist():
    cmd = ["python", "setup.py", "sdist"]
    savedir= os.getcwd()
    try:
        os.chdir(uniformpath(PYTHON2_TARGET))
        exec_and_log(cmd)
        os.chdir(uniformpath(PYTHON3_TARGET))
        exec_and_log(cmd)
    finally:
        os.chdir(savedir)

    # copy over Python 2
    gzfile = "antlr4-python2-runtime-" + VERSION + ".tar.gz"
    artifact = uniformpath(PYTHON2_TARGET) + "/dist/"+gzfile
    copyfile(artifact, "dist/"+gzfile)
    print_and_log("Generated " + "dist/"+gzfile)

    # copy over Python 3
    gzfile = "antlr4-python3-runtime-" + VERSION + ".tar.gz"
    artifact = uniformpath(PYTHON3_TARGET) + "/dist/"+gzfile
    copyfile(artifact, "dist/"+gzfile)
    print_and_log("Generated " + "dist/"+gzfile)


def regen_tests():
    require(_mkjar)
    junit_jar, hamcrest_jar = load_junitjars()
    cp = uniformpath("dist/antlr4-" + VERSION + "-complete.jar") \
         + os.pathsep + uniformpath("out/test") \
         + os.pathsep + junit_jar \
         + os.pathsep + hamcrest_jar
    args = ["-nowarn", "-Xlint", "-Xlint:-serial", "-g"]
    javac("tool/test", "out/test", version="1.6", cp=cp, args=args)  # all targets can use org.antlr.v4.test.*
    java(classname="org.antlr.v4.test.rt.gen.Generator", cp="out/test:dist/antlr4-4.5-complete.jar")
    print_and_log("test generation complete")


def tests():
    require(regen_tests)
    for t in TARGETS:
        test_target(t)


def test_java():
    test_target("Java")


def test_python2():
    test_target("Python2")


def test_python3():
    test_target("Python3")


def test_csharp():
    test_target("CSharp")


def test_javascript():
    test_target("JavaScript")


def test_target(t):
    require(regen_tests)
    cp = uniformpath("dist/antlr4-" + VERSION + "-complete.jar") \
         + os.pathsep + uniformpath("out/test")
    juprops = ["-D%s=%s" % (p, test_properties[p]) for p in test_properties]
    args = ["-nowarn", "-Xlint", "-Xlint:-serial", "-g"]
    print_and_log("Testing %s ..." % t)
    try:
        test(t, cp, juprops, args)
        print t + " tests complete"
    except:
        print t + " tests failed"


def test(t, cp, juprops, args):
    junit_jar, hamcrest_jar = load_junitjars()
    srcdir = uniformpath(TARGETS[t] + "/tool/test")
    dstdir = uniformpath( "out/test/" + t)
    # Prefix CLASSPATH with individual target tests
    thiscp = dstdir + os.pathsep + cp
    thisjarwithjunit = thiscp + os.pathsep + hamcrest_jar + os.pathsep + junit_jar
    skip = []
    if t=='Java':
        # don't test generator
        skip = [ "/org/antlr/v4/test/rt/gen/", "TestPerformance" ]
    elif t=='Python2':
        # need BaseTest located in Py3 target
        base = uniformpath(TARGETS['Python3'] + "/tool/test")
        skip = [ "/org/antlr/v4/test/rt/py3/" ]
        javac(base, "out/test/" + t, version="1.6", cp=thisjarwithjunit, args=args, skip=skip)
        skip = []
    elif t=='JavaScript':
        # don't test browsers automatically, this is overkilling and unreliable
        browsers = ["safari","chrome","firefox","explorer"]
        skip = [ uniformpath(srcdir + "/org/antlr/v4/test/rt/js/" + b) for b in browsers ]
    javac(srcdir, trgdir="out/test/" + t, version="1.6", cp=thisjarwithjunit, args=args, skip=skip)
    # copy resource files required for testing
    files = allfiles(srcdir)
    for src in files:
        if not ".java" in src and not ".stg" in src and not ".DS_Store" in src:
            dst = src.replace(srcdir, uniformpath("out/test/" + t))
            # only copy files from test dirs
            if os.path.exists(os.path.split(dst)[0]):
                shutil.copyfile(src, dst)
    junit("out/test/" + t, cp=thiscp, verbose=False, args=juprops)


def install(): # mvn installed locally in ~/.m2, java jar to /usr/local/lib if present
    require(_mkjar)
    require(mksrc)
    require(mkdoc)
    jarfile = "dist/antlr4-" + VERSION + "-complete.jar"
    print_and_log("Installing "+jarfile+" and *-sources.jar, *-javadoc.jar")
    mvn_install(jarfile,
        "dist/antlr4-" + VERSION + "-complete-sources.jar",
        "dist/antlr4-" + VERSION + "-complete-javadoc.jar",
        "org.antlr",
        "antlr4",
        VERSION)
    runtimejarfile = "dist/antlr4-" + VERSION + ".jar"
    print_and_log("Installing "+runtimejarfile+" and *-sources.jar, *-javadoc.jar")
    mvn_install(runtimejarfile,
        "dist/antlr4-" + VERSION + "-sources.jar",
        "dist/antlr4-" + VERSION + "-javadoc.jar",
        "org.antlr",
        "antlr4-runtime",
        VERSION)
    if os.path.exists("/usr/local/lib"):
        libjar = "/usr/local/lib/antlr-" + VERSION + "-complete.jar"
        print_and_log("Installing "+libjar)
        shutil.copyfile(jarfile, libjar)

def mksrc():
    srcpath = "runtime/Java/src/org"
    srcfiles = allfiles(srcpath, "*.java")
    jarfile = "dist/antlr4-" + VERSION + "-sources.jar"
    if not isstale(src=newest(srcfiles), trg=jarfile):
        return
    zip(jarfile, srcpath)
    print_and_log("Generated " + jarfile)

    srcpaths = [ srcpath, "gen3/org", "gen4/org", "tool/src/org"]
    srcfiles = allfiles(srcpaths, "*.java")
    jarfile = "dist/antlr4-" + VERSION + "-complete-sources.jar"
    if not isstale(src=newest(srcfiles), trg=jarfile):
        return
    zip(jarfile, srcpaths)
    print_and_log("Generated " + jarfile)


def mkdoc():
    require(mksrc)
    # add a few source dirs to reduce the number of javadoc errors
    # JavaDoc needs antlr annotations source code
    runtimedoc = "dist/antlr4-" + VERSION + "-javadoc.jar"
    tooldoc = "dist/antlr4-" + VERSION + "-complete-javadoc.jar"
    runtime_source_jarfile = "dist/antlr4-" + VERSION + "-sources.jar"
    tool_source_jarfile = "dist/antlr4-" + VERSION + "-complete-sources.jar"
    if not isstale(src=runtime_source_jarfile, trg=runtimedoc) and \
       not isstale(src=tool_source_jarfile, trg=tooldoc):
        return
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
    zip(runtimedoc, "doc/Java")
    zip(tooldoc, "doc/JavaTool")


def target_artifacts():
    javascript()
    python_sdist()
    csharp()


def clean(dist=False):
    if dist:
        rmdir("dist")
    rmdir("out")
    rmdir("gen3")
    rmdir("gen4")
    rmdir("doc")


def all():
    clean(True)
    _mkjar()
    target_artifacts()
    tests()
    mkdoc()
    mksrc()
    install()
    clean()


processargs(globals())  # E.g., "python bild.py all"
