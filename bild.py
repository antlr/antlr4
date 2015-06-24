#!/usr/bin/env python
from collections import OrderedDict

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

# Base templates specific to targets needed by tests in TestFolders
RUNTIME_TEST_TEMPLATES = {
	"Java"     : uniformpath(JAVA_TARGET)+"/tool/test/org/antlr/v4/test/runtime/java/Java.test.stg",
	"CSharp"   : uniformpath(CSHARP_TARGET)+"/tool/test/org/antlr/v4/test/runtime/csharp/CSharp.test.stg",
	"Python2"  : uniformpath(PYTHON2_TARGET)+"/tool/test/org/antlr/v4/test/runtime/python2/Python2.test.stg",
	"Python3"  : uniformpath(PYTHON3_TARGET)+"/tool/test/org/antlr/v4/test/runtime/python3/Python3.test.stg",
	"JavaScript"   : uniformpath(JAVASCRIPT_TARGET)+"/tool/test/org/antlr/v4/test/runtime/javascript/node/Node.test.stg",
}


def parsers():
    antlr3("tool/src/org/antlr/v4/parse", "gen3", version="3.5.2", package="org.antlr.v4.parse")
    antlr3("tool/src/org/antlr/v4/codegen", "gen3", version="3.5.2", package="org.antlr.v4.codegen",
           args=["-lib", uniformpath("gen3/org/antlr/v4/parse")])
    antlr4("runtime/Java/src/org/antlr/v4/runtime/tree/xpath", "gen4",
           version=BOOTSTRAP_VERSION, package="org.antlr.v4.runtime.tree.xpath")

def compile():
    """
    Compile tool, runtime, tool tests, runtime tests into ./out dir
    Depends on treelayout jar, antlr v3, junit/hamcrest
    """
    require(parsers)
    require(regen_tests)
    junit_jar, hamcrest_jar = load_junitjars()
    cp = uniformpath("out") + os.pathsep + \
         os.path.join(JARCACHE, "antlr-3.5.2-complete.jar") + os.pathsep + \
         "runtime/Java/lib/org.abego.treelayout.core.jar" + os.pathsep + \
         os.pathsep + uniformpath("out") + \
         os.pathsep + junit_jar + \
         os.pathsep + hamcrest_jar
    srcpath = ["gen3", "gen4", "runtime/Java/src", "tool/src", "tool/test"]
    args = ["-Xlint", "-Xlint:-serial", "-g", "-sourcepath", string.join(srcpath, os.pathsep)]
    for sp in srcpath:
        javac(sp, "out", version="1.6", cp=cp, args=args, skip=['org/antlr/v4/test/rt'])
    # pull in targets' code gen
    for t in TARGETS:
        javac(TARGETS[t] + "/tool/src",  "out", version="1.6", cp=cp, args=args)
    # pull in generated runtime tests and runtime test support code
    # Special case: python2 needs code from python3
    javac(uniformpath(TARGETS['Python3'])+"/tool/test/org/antlr/v4/test/runtime/python/BasePythonTest.java",
          "out", version="1.6", cp=cp, args=args)
    skip = ['org/antlr/v4/test/rt',
            'org/antlr/v4/test/runtime/javascript/chrome',
            'org/antlr/v4/test/runtime/javascript/explorer',
            'org/antlr/v4/test/runtime/javascript/firefox',
            'org/antlr/v4/test/runtime/javascript/safari']
    for t in RUNTIME_TEST_TEMPLATES:
        javac(TARGETS[t] + "/tool/test", "out", version="1.6", cp=cp, args=args, skip=skip)
        javac('runtime-testsuite/test',  "out", version="1.6", cp=cp)


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
    download("http://www.antlr3.org/download/antlr-3.5.2-runtime.jar", JARCACHE)
    unjar(os.path.join(JARCACHE, "antlr-3.5.2-runtime.jar"), trgdir="out")
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
    os.remove(jarfile)              # delete target for Windows compatibility
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
    """
    Generate all runtime Test*.java files for all targets into ./runtime-testsuite/test/org/antlr/v4/test/runtime/targetname
    They will all get compiled in compile() so we have all together but
    can drop from final jar in mkjar().
    """
    # first compile runtime-testsuite; only needs ST and junit
    junit_jar, hamcrest_jar = load_junitjars()
    download("http://www.stringtemplate.org/download/ST-4.0.8.jar", JARCACHE)
    cp = os.path.join(JARCACHE, "ST-4.0.8.jar") \
         + os.pathsep + junit_jar \
         + os.pathsep + hamcrest_jar
    args = ["-nowarn", "-Xlint", "-Xlint:-serial", "-g"]
    javac("runtime-testsuite/src", "out/testsuite", version="1.6", cp=cp, args=args)

    # now use TestGenerator to generate Test*.java for each target using
    # runtime templates and test templates themselves:
    #     runtime-testsuite/resources/org/antlr/v4/test/runtime/templates
    # generate into runtime-testsuite/test/org/antlr/v4/test/runtime/python2 etc...
    for targetName in RUNTIME_TEST_TEMPLATES:
        java(classname="org.antlr.v4.testgen.TestGenerator", cp="out/testsuite:"+cp,
             progargs=['-o', 'runtime-testsuite/test', '-templates', RUNTIME_TEST_TEMPLATES[targetName]])
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
    require(_mkjar)
    juprops = ["-D%s=%s" % (p, test_properties[p]) for p in test_properties]
    args = ["-nowarn", "-Xlint", "-Xlint:-serial", "-g"]
    print_and_log("Testing %s ..." % t)
    try:
        test(t, juprops, args)
        print t + " tests complete"
    except Exception as e:
        print t + " tests failed: ", e


def test(target, juprops, args):
    junit_jar, hamcrest_jar = load_junitjars()
    srcdir = uniformpath('runtime-testsuite/test/'+target)
    dstdir = uniformpath("out/test/"+target)
    # Prefix CLASSPATH with individual target tests
    cp = dstdir + os.pathsep + uniformpath("dist/antlr4-" + VERSION + "-complete.jar")
    thisjarwithjunit = cp + os.pathsep + hamcrest_jar + os.pathsep + junit_jar
    skip = []
    if target=='Java':
        # don't test generator
        skip = [ "TestPerformance.java", "TestGenerator.java" ]
    elif target=='JavaScript':
        # don't test browsers automatically, this is overkill and unreliable
        browsers = ["safari","chrome","firefox","explorer"]
        skip = [ uniformpath(srcdir + "/org/antlr/v4/test/rt/js/" + b) for b in browsers ]
        skip += [ uniformpath(srcdir + "/org/antlr/v4/test/runtime/javascript/" + b) for b in browsers ]
    javac(srcdir, trgdir="out/test", version="1.6", cp=thisjarwithjunit, args=args, skip=skip)
    # copy any resource files required for testing
    for t in TARGETS:
        root = TARGETS[t] + "/tool/test"
        files = allfiles(root)
        for src in files:
            if not ".java" in src and not ".stg" in src and not os.path.basename(src).startswith("."):
                dst = uniformpath(src.replace(root, "out"))
                # print src, dst
                if os.path.exists(os.path.split(dst)[0]):
                    shutil.copyfile(src, dst)
    junit("out/test", cp='/Users/parrt/antlr/code/antlr4/out:'+uniformpath("dist/antlr4-" + VERSION + "-complete.jar"),
          verbose=False, args=juprops)


def install(): # mvn installed locally in ~/.m2, java jar to /usr/local/lib if present
    require(_mkjar)
    require(mksrc)
    require(mkdoc)
    jarfile = "dist/antlr4-" + VERSION + "-complete.jar"
    print_and_log("Maven installing "+jarfile+" and *-sources.jar, *-javadoc.jar")
    mvn_install(binjar=jarfile,
                srcjar="dist/antlr4-" + VERSION + "-complete-sources.jar",
                docjar="dist/antlr4-" + VERSION + "-complete-javadoc.jar",
                groupid="org.antlr",
                artifactid="antlr4",
                version=VERSION)
    runtimejarfile = "dist/antlr4-" + VERSION + ".jar"
    print_and_log("Maven installing "+runtimejarfile+" and *-sources.jar, *-javadoc.jar")
    mvn_install(binjar=runtimejarfile,
                srcjar="dist/antlr4-" + VERSION + "-sources.jar",
                docjar="dist/antlr4-" + VERSION + "-javadoc.jar",
                groupid="org.antlr",
                artifactid="antlr4-runtime",
                version=VERSION)
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
    dirs += ["gen4"]
    exclude = [
        "org/antlr/runtime",
        "org/abego",
        "org/stringtemplate",
        "org/antlr/stringtemplate"]
    javadoc(srcdir=dirs, trgdir="doc/Java", packages="org.antlr.v4.runtime", exclude=exclude)
    dirs += ["gen3"]
    dirs += [TARGETS[t] + "/tool/src" for t in TARGETS]
    javadoc(srcdir=dirs, trgdir="doc/JavaTool", packages="org.antlr.v4", exclude=exclude)
    # build stack merge PredictionContext and ATNState images from DOT
    # DOT Images are in runtime/Java/src/main/dot/org/antlr/v4/runtime/atn/images/
    # Gen into E.g., doc/Java/org/antlr/v4/runtime/atn/images/SingletonMerge_DiffRootSamePar.svg
    mkdir("doc/Java/org/antlr/v4/runtime/atn/images")
    mkdir("doc/JavaTool/org/antlr/v4/runtime/atn/images")
    for f in glob.glob("runtime/Java/src/main/dot/org/antlr/v4/runtime/atn/images/*.dot"):
        dot(f, "doc/Java/org/antlr/v4/runtime/atn/images", format="svg")
        dot(f, "doc/JavaTool/org/antlr/v4/runtime/atn/images", format="svg")
    zip(runtimedoc, "doc/Java")
    zip(tooldoc, "doc/JavaTool")


def target_artifacts():
    javascript()
    python_sdist()
    csharp()


def clean(dist=True):
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

# def duh():
#     for t in TARGETS:
#         root = TARGETS[t] + "/tool/test"
#         files = allfiles(root)
#         print t
#         for src in files:
#             if not ".java" in src and not ".stg" in src and not os.path.basename(src).startswith("."):
#                 dst = uniformpath(src.replace(root, "out"));
#                 print src, dst
#                 if os.path.exists(os.path.split(dst)[0]):
#                     shutil.copyfile(src, dst)

processargs(globals())  # E.g., "python bild.py all"
