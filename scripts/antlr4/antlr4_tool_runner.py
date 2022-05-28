import os
import sys
import subprocess
from shutil import which
from pathlib import Path
from urllib.request import urlopen
from urllib import error
import json

import jdk  # requires install-jdk package


def latest_version():
    with urlopen(f"https://search.maven.org/solrsearch/select?q=a:antlr4-master+g:org.antlr") as response:
        s = response.read().decode("UTF-8")
        searchResult = json.loads(s)['response']
        # searchResult = s.json()['response']
        antlr_info = searchResult['docs'][0]
        # print(json.dump(searchResult))
        latest = antlr_info['latestVersion']
        return latest

def antlr4_jar(version):
    jar = os.path.join(mvn_repo, version, f'antlr4-{version}-complete.jar')
    if not os.path.exists(jar):
        return download_antlr4(jar, version)
    return jar


def download_antlr4(jar, version):
    s = None
    try:
        with urlopen(f"https://repo1.maven.org/maven2/org/antlr/antlr4/{version}/antlr4-{version}-complete.jar") as response:
            print(f"Downloading antlr4-{version}-complete.jar")
            os.makedirs(os.path.join(mvn_repo, version), exist_ok=True)
            s = response.read()
    except error.URLError as e:
        print(f"Could not find antlr4-{version}-complete.jar at maven.org")
        ResponseData = e.read().decode("utf8", 'ignore')

    if s is None:
        return None
    with open(jar, "wb") as f:
        f.write(s)
    return jar


def find_bin_dir(install_dir):
    for root, dirs, files in os.walk(install_dir):
        if root.endswith("bin"):
            return root
    return None


def install_jre(java_version='11'):
    USER_DIR = os.path.expanduser("~")
    JRE_DIR = os.path.join(USER_DIR, ".jre")
    if os.path.exists(JRE_DIR):
        for f in os.listdir(JRE_DIR):
            if f.startswith(f"jdk-{java_version}"):
                install_dir = os.path.join(JRE_DIR, f)
                bindir = find_bin_dir(install_dir)
                java = os.path.join(bindir, 'java')
                return java

    r = input(f"ANTLR tool needs Java to run; install Java JRE 11 yes/no (default yes)? ")
    if r.strip().lower() not in {'yes','y',''}:
        exit(1)
    install_dir = jdk.install(java_version, jre=True)
    print(f"Installed Java in {install_dir}; remove that dir to uninstall")
    bindir = find_bin_dir(install_dir)
    if bindir is None:
        print(f"Can't find bin/java in {install_dir}; installation failed")
        return None
    java = os.path.join(bindir, 'java')
    return java


def install_jre_and_antlr(version):
    global mvn_repo, homedir
    homedir = Path.home()
    mvn_repo = os.path.join(homedir, '.m2', 'repository', 'org', 'antlr', 'antlr4')

    jar = antlr4_jar(version)
    java = which("java")
    if java is None:
        java = install_jre()
    if jar is None or java is None:
        exit(1)
    CHECK_JRE_VERSION = False
    if CHECK_JRE_VERSION:
        p = subprocess.Popen([java, '--version'], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        out, err = p.communicate()
        out = out.decode("UTF-8").split('\n')[0]
        print(f"Running {out}")
    return jar, java


def get_version_arg(args):
    version = None
    if len(args) > 0 and args[0] == '-v':
        version = args[1]
        args = args[2:]
    if version is None:
        version = latest_version()
    return args, version


def tool():
    """Entry point to run antlr4 tool itself"""
    args = sys.argv[1:]
    args, version = get_version_arg(args)
    jar, java = install_jre_and_antlr(version)

    p = subprocess.Popen([java, '-cp', jar, 'org.antlr.v4.Tool']+args, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    out, err = p.communicate()
    out = out.decode("UTF-8")
    err = err.decode("UTF-8")

    if err: print(err)
    if out: print(out)


def prof():
    """Entry point to run antlr4 profiling using grammar and input file"""
    args = sys.argv[1:]
    args, version = get_version_arg(args)
    jar, java = install_jre_and_antlr(version)


if __name__ == '__main__':
    tool()