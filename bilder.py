import os
import subprocess
import errno
import re
import sys
import shutil
import urllib2
import glob
from distutils import dir_util
from distutils import file_util
import zipfile
import fnmatch
import inspect
import time

# evil globals
_ = None
bild_completed = set()  # which *targets* have been built.
BILD = os.path.expanduser("~/.bild")
JARCACHE = os.path.join(BILD, "jars")
# CLASSPATH = JARCACHE + "/*" + os.pathsep + os.environ['CLASSPATH']

def findjdks_win():
	return {}


def findjdks_linux():
	"""
	CentOS: /usr/lib/jvm/java-1.7.0-openjdk-1.7.0.55.x86_64/jre/bin/java
					/usr/java/jdk1.7.0_51
	ubuntu: /usr/lib/jvm/java-6-openjdk/ for OpenJDK
					/usr/lib/jvm/* for Oracle JDK
	"""
	versions = {}
	for jdk in glob.glob("/usr/lib/jvm/*") + glob.glob("/usr/java/*"):
		name = os.path.basename(jdk)
		if name.startswith("java-1.6") or name.startswith("jdk1.6"):
			versions["1.6"] = jdk
		if name.startswith("java-1.7") or name.startswith("jdk1.7"):
			versions["1.7"] = jdk
		if name.startswith("java-1.8") or name.startswith("jdk1.8"):
			versions["1.8"] = jdk
	return versions


def findjdks_mac():
	"""find Java installations on a mac"""
	versions = {}
	dirs = []
	for jdk in glob.glob("/Library/Java/JavaVirtualMachines/*"):
		dirs.append(jdk)
	for jdk in glob.glob("/System/Library/Java/JavaVirtualMachines/*"):
		dirs.append(jdk)
	for jdk in dirs:
		name = os.path.basename(jdk)
		if name.startswith("1.6."):
			versions["1.6"] = jdk + "/Contents/Home"
		elif name.startswith("jdk1.7."):
			versions["1.7"] = jdk + "/Contents/Home"
		elif name.startswith("jdk1.8."):
			versions["1.8"] = jdk + "/Contents/Home"
	return versions


def findjdks():
	if sys.platform == 'win32':
		return findjdks_win()
	if sys.platform == 'darwin':
		return findjdks_mac()
	if sys.platform == 'linux2':
		return findjdks_linux()


jdk = findjdks()
#print sys.platform + " java =", jdk


def modtime(fname):
	try:
		return os.path.getmtime(fname)
	except:
		return 0  # mod date of epoch means ancient  sys.float_info.max  # meaning mod date in future if file not there


def uniformpath(dir):
	dir = os.path.expanduser(dir)  # ~parrt -> /Users/parrt on unix
	dir = os.path.abspath(dir)  # expand relative dirs
	return dir


def mkdir(path):
	"""
	From: http://stackoverflow.com/questions/600268/mkdir-p-functionality-in-python
	"""
	try:
		os.makedirs(path)
	except OSError as exc:  # Python >2.5
		if exc.errno == errno.EEXIST and os.path.isdir(path):
			pass
		else:
			raise


def rmdir(dir):
	shutil.rmtree(dir, ignore_errors=True)


def files(pathspec):
	"""
	Get all files matching pathspec (nonrecursive)
	"""
	return [f for f in glob.glob(pathspec)]


def allfiles(dir, pattern="*"):
	"""
	Return list<string> all files in subtree dir, optionally matching
	a pattern spec like "*.java"
	"""
	dir = uniformpath(dir)
	if not os.path.isdir(dir):  # must be file
		return [dir]
	matching_files = []
	for root, subFolders, files in os.walk(dir):
		matching = fnmatch.filter(files, pattern)
		matching_files.extend(os.path.join(root, f) for f in matching)
	return matching_files


def copytree(src, trg, ignore=None):
	if os.path.exists(trg) and not os.path.isdir(trg):
		os.remove(trg)  # can't copy onto a file
	mkdir(trg)
	dir_util.copy_tree(src, trg, preserve_mode=True)


def copyfile(src, trg):
	if not os.path.exists(trg):
		trg_dirname = os.path.dirname(trg)
		if len(trg_dirname.strip()) > 0:
			mkdir(trg_dirname)
	file_util.copy_file(src, trg, preserve_mode=True)


def replsuffix(files, suffix):
	"""
	Return list<string> all files with their .suffix replaced
	"""
	outfiles = []
	if suffix is None: return
	if type(files) is type(""):
		files = [files]
	for f in files:
		fname, ext = os.path.splitext(f)
		newfname = fname + suffix
		outfiles.append(newfname)
	return outfiles


def javac_targets(srcdir, trgdir):
	"""
	Return a map<string,string> of files javac would create given a subdir of java
	files and a target dir. E.g.,
	javac_targets("/Users/parrt/mantra/code/compiler/src/java", "out")
	generates
		{".../src/java/mantra/Tool.java":"out/mantra/Tool.class", ...}
	"""
	srcdir = uniformpath(srcdir)
	trgdir = uniformpath(trgdir)
	mapping = {}
	javafiles = allfiles(srcdir, "*.java")
	classfiles = replsuffix(javafiles, ".class")
	if not os.path.isdir(srcdir):  # must be a Java file
		srcdir = os.path.dirname(srcdir)
	classfiles = [f.replace(srcdir, trgdir) for f in classfiles]  # shift to trg dir
	for i in range(len(javafiles)):
		mapping[javafiles[i]] = classfiles[i]
	return mapping


def grep(file, regex):
	matches = []
	with open(file) as f:
		contents = f.read()
		m = re.search(regex, contents)
		if m:
			matches.append(m.group())
	return matches


def antlr3_targets(srcdir, trgdir, package=None):
	"""
	Return a map<string,string> of files antlr3 would create given a subdir of grammars
	files and a target dir. E.g.,
	antlr3_targets("tool/src/org/antlr/v4/codegen", "gen")
	gives:
	{'/Volumes/SSD2/Users/parrt/antlr/code/antlr4/tool/src/org/antlr/v4/codegen/SourceGenTriggers.g':
	 '/Volumes/SSD2/Users/parrt/antlr/code/antlr4/gen/SourceGenTriggers.java'}
	"""
	srcdir = uniformpath(srcdir)
	if package is not None:
		package = re.sub('[.]', '/', package)
		trgdir = uniformpath(os.path.join(trgdir, package))
	else:
		trgdir = uniformpath(trgdir)
	mapping = {}
	gfiles = allfiles(srcdir, "*.g")
	for f in gfiles:
		fdir, fsuffix = os.path.splitext(f)
		gname = os.path.basename(fdir)
		fullgname = os.path.join(trgdir, gname)
		lexer = grep(f, r"lexer\s+grammar")
		parser = grep(f, r"parser\s+grammar")
		tree = grep(f, r"tree\s+grammar")
		if len(lexer) > 0 or len(parser) > 0 or len(tree) > 0:
			# print "a lexer or parser or tree parser"
			mapping[f] = fullgname + ".java"
		else:
			# must be combined grammar
			# print "a combined"
			mapping[f] = [fullgname + "Parser.java", fullgname + "Lexer.java"]
	return mapping


def antlr4_targets(srcdir, trgdir, package=None):
	"""
	Return a map<string,string> of files antlr4 would create given a subdir of grammars
	files and a target dir. E.g.,
	antlr4_targets("tests", "gen")
	gives:
	{'/Volumes/SSD2/Users/parrt/github/bild/tests/sample1/src/grammars/org/foo/T.g4':
	  ['/Volumes/SSD2/Users/parrt/github/bild/gen/TParser.java',
	   '/Volumes/SSD2/Users/parrt/github/bild/gen/TLexer.java']
	}
	"""
	srcdir = uniformpath(srcdir)
	if package is not None:
		package = re.sub('[.]', '/', package)
		trgdir = uniformpath(os.path.join(trgdir, package))
	else:
		trgdir = uniformpath(trgdir)
	mapping = {}
	gfiles = allfiles(srcdir, "*.g4")
	for f in gfiles:
		fdir, fsuffix = os.path.splitext(f)
		gname = os.path.basename(fdir)
		fullgname = os.path.join(trgdir, gname)
		lexer = grep(f, r"lexer\s+grammar")
		parser = grep(f, r"parser\s+grammar")
		if len(lexer) > 0 or len(parser) > 0:
			# print "a lexer or parser"
			mapping[f] = fullgname + ".java"
		else:
			# must be combined grammar
			# print "a combined"
			mapping[f] = [fullgname + "Parser.java", fullgname + "Lexer.java"]
	return mapping


def newer(a, b):
	"""
	Return true if a newer than b
	"""
	return modtime(a) < modtime(b)  # smaller is earlier


def older(a, b):
	"""
	Return true if a older or same as b
	"""
	return not newer(a, b)


def stale(map):  # accept map<string,string> or map<string,list<string>>
	"""
	Return map<string,string-or-list> with files to build as they are out of date
	"""
	out = {}
	for src in map:
		trg = map[src]
		fstale = False
		if type(trg) == type([]):
			for t in trg:
				# print src,"->",t
				# print modtime(src), modtime(t)
				if isstale(src, t):
					fstale = True
					break
		else:
			# print src,"->",trg
			# print modtime(src), modtime(trg)
			if isstale(src, trg):
				# print "target newer so no build"
				fstale = True
		if fstale:
			out[src] = trg
	return out


def isstale(src, trg):
	return modtime(trg) < modtime(src)  # smaller is earlier


def require(target):
	print "require", target.__name__
	if id(target) in bild_completed:
		return
	bild_completed.add(id(target))
	target()
	caller = inspect.currentframe().f_back.f_code.co_name
	print "build", caller


def antlr3(srcdir, trgdir=".", package=None, version="3.5.1", args=[]):
	srcdir = uniformpath(srcdir)
	trgdir = uniformpath(trgdir)
	map = antlr3_targets(srcdir, trgdir, package)
	tobuild = stale(map).keys()
	if len(tobuild) == 0:
		return
	jarname = "antlr-" + version + "-complete.jar"
	# if jarname not in filelist(JARCACHE):
	download("http://www.antlr3.org/download/" + jarname, JARCACHE)
	if package is not None:
		packageAsDir = re.sub('[.]', '/', package)
		cmd = ["java", "-cp", os.path.join(JARCACHE, jarname),
			   "org.antlr.Tool",
			   "-o", os.path.join(trgdir, packageAsDir)] + args + tobuild
	else:
		cmd = ["java", "org.antlr.Tool", "-o", trgdir] + args + tobuild
	# print cmd
	subprocess.call(cmd)


def antlr4(srcdir, trgdir=".", package=None, version="4.3", args=[]):
	srcdir = uniformpath(srcdir)
	trgdir = uniformpath(trgdir)
	map = antlr4_targets(srcdir, trgdir, package)
	tobuild = stale(map).keys()
	if len(tobuild) == 0:
		return
	jarname = "antlr-" + version + "-complete.jar"
	# if jarname not in filelist(JARCACHE):
	download("http://www.antlr.org/download/" + jarname, JARCACHE)
	if package is not None:
		packageAsDir = re.sub('[.]', '/', package)
		cmd = ["java", "-cp", os.path.join(JARCACHE, jarname),
			   "org.antlr.v4.Tool",
			   "-o", os.path.join(trgdir, packageAsDir),
			   "-package", package] + args + tobuild
	else:
		cmd = ["java", "org.antlr.v4.Tool", "-o", trgdir] + args + tobuild
	# print string.join(cmd, " ")
	subprocess.call(cmd)


def javac(srcdir, trgdir=".", cp=None, version=None, args=[]):
	srcdir = uniformpath(srcdir)
	trgdir = uniformpath(trgdir)
	mkdir(trgdir)
	map = javac_targets(srcdir, trgdir)
	tobuild = stale(map).keys()
	# print "build",stale(map)
	if len(tobuild) == 0:
		return
	if cp is None:
		cp = trgdir + os.pathsep + JARCACHE + "/*"
	# cmd = ["javac", "-sourcepath", srcdir, "-d", trgdir, "-cp", cp] + args + tobuild
	javac = "javac"
	if version is not None:
		javac = os.path.join(jdk[version], "bin/javac")
	cmd = [javac, "-d", trgdir, "-cp", cp] + args + tobuild
	# print string.join(cmd, " ")
	subprocess.call(cmd)


def jar(jarfile, inputfiles=".", srcdir=".", manifest=None):
	trgdir = os.path.dirname(jarfile)
	mkdir(trgdir)
	if type(inputfiles) == type(""):
		inputfiles = [inputfiles]
	contents_with_C = []
	for f in inputfiles:
		contents_with_C.append("-C")
		contents_with_C.append(srcdir)
		contents_with_C.append(f)
	# write manifest
	metadir = os.path.join(srcdir, "META-INF")
	mkdir(metadir)
	with open(os.path.join(metadir, "MANIFEST.MF"), "w") as mf:
		mf.write(manifest)
	mfile = os.path.join(srcdir, "META-INF/MANIFEST.MF")
	cmd = ["jar", "cmf", mfile, jarfile] + contents_with_C
	subprocess.call(cmd)


def unjar(jarfile, trgdir="."):
	jar = zipfile.ZipFile(jarfile)
	jar.extractall(path=trgdir)
	jar.close()


def javadoc(srcdir, trgdir, packages, recursive=True):
	if type(packages) == type(""):
		packages = [packages]
	cmd = ["javadoc", "-quiet", "-d", trgdir, "-sourcepath", srcdir]
	if recursive:
		cmd += ["-subpackages"]
	cmd += packages
	print cmd
	subprocess.call(cmd)


def load_junitjars():
	junit_version = '4.11'
	junit_jar = 'junit-' + junit_version + '.jar'
	hamcrest_version = '1.3'
	hamcrest_jar = 'hamcrest-core-' + hamcrest_version + '.jar'
	download("http://search.maven.org/remotecontent?filepath=junit/junit/" + junit_version + "/" + junit_jar,
			 JARCACHE)
	download(
		"http://search.maven.org/remotecontent?filepath=org/hamcrest/hamcrest-core/" + hamcrest_version + "/" + hamcrest_jar,
		JARCACHE)
	return JARCACHE + "/" + junit_jar, JARCACHE + "/" + hamcrest_jar


def junit(srcdir, cp=None, verbose=False, args=[]):
	hamcrest_jar, junit_jar = load_junitjars()
	download("https://github.com/parrt/bild/raw/master/lib/bild-junit.jar", JARCACHE)
	srcdir = uniformpath(srcdir)
	testfiles = allfiles(srcdir, "*.class")
	testfiles = [f[len(srcdir) + 1:] for f in testfiles]
	testclasses = replsuffix(testfiles, '')
	testclasses = [c for c in testclasses if os.path.basename(c).startswith("Test") and '$' not in os.path.basename(c)]
	testclasses = [c.replace('/', '.') for c in testclasses]
	cp_ = srcdir + os.pathsep + junit_jar + os.pathsep + hamcrest_jar + os.pathsep + JARCACHE + "/bild-junit.jar"
	if cp is not None:
		cp_ = cp + os.pathsep + cp_
	processes = []
	# launch all tests in srcdir in parallel
	for c in testclasses:
		cmd = ['java'] + args + ['-cp', cp_, 'org.bild.JUnitLauncher', c]
		if verbose:
			cmd = ['java'] + args + ['-cp', cp_, 'org.bild.JUnitLauncher', '-verbose', c]
		# print string.join(cmd, ' ')
		p = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
		processes.append(p)
	# busy wait with sleep for any results
	while len(processes) > 0:
		for p in processes:
			r = p.poll()
			if r is not None:  # p is done
				processes.remove(p)
				stdout, stderr = p.communicate()  # hush output
				print stdout,
		time.sleep(0.200)
	print "Tests complete"


def dot(src, trgdir=".", format="pdf"):
	if not src.endswith(".dot"):
		return
	nosuffix = src[0:-4]
	base = os.path.basename(nosuffix)
	cmd = ["dot", "-T" + format, "-o" + os.path.join(trgdir, base + "." + format), src]
	subprocess.call(cmd)


def download(url, trgdir=".", force=False):
	file_name = url.split('/')[-1]
	mkdir(trgdir)
	target_name = os.path.join(trgdir, file_name)
	if os.path.exists(target_name) and not force:
		return
	try:
		response = urllib2.urlopen(url)
	except urllib2.HTTPError, e:
		sys.stderr.write("can't download %s: %s\n" % (url, str(e)))
	else:
		output = open(target_name, 'wb')
		output.write(response.read())
		output.close()


def scp(src, user, machine, trg):
	subprocess.check_call(
		["scp", src, "%s@%s:%s" % (user, machine, trg)]
	)


def zip(zipfilename, srcdir):  # , recursive=True):
	"""
	Last element of srcdir is considered root of zip'd content. E.g.,
	srcdir="doc/Java" gives zip file with Java as the root element.

	srcdir might expand to /Volumes/SSD2/Users/parrt/antlr/code/antlr4/doc/Java
	"""
	srcdir = uniformpath(srcdir)
	rootdir = os.path.dirname(srcdir)  # "...doc/Java" gives doc
	rootnameindex = len(rootdir) + 1  # "...doc/Java" gives start of "Java"
	with zipfile.ZipFile(zipfilename, mode="w", compression=zipfile.ZIP_DEFLATED) as z:
		for f in allfiles(srcdir):
			z.write(f, f[rootnameindex:])


def python(filename, workingdir=".", args=[]):
	savedcwd = os.getcwd()
	os.chdir(workingdir)
	try:
		if type(args) is not type([]):
			args = [args]
		cmd = [sys.executable, filename] + args
		subprocess.call(cmd)
	finally:
		os.chdir(savedcwd)


def processargs(globals):
	if len(sys.argv) == 1:
		target = globals["all"]
	else:
		target = globals[sys.argv[1]]
	if target is not None:
		print "target", target.__name__
		target()
	else:
		sys.stderr.write("unknown target: %s\n" % sys.argv[1])
