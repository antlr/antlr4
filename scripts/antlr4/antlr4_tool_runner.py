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
        print("latest", latest)
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


def main():
    global mvn_repo, homedir

    args = sys.argv[1:]
    homedir = Path.home()
    mvn_repo = os.path.join(homedir, '.m2', 'repository', 'org', 'antlr', 'antlr4')

    version = None
    if len(args)>0 and args[0]=='-v':
        version = args[1]
        args = args[2:]

    if version is None:
        version = latest_version()

    jar = antlr4_jar(version)

    java = which("java")
    if java is None:
        java_version = '11'
        r = input(f"ANTLR tool needs Java to run; install Java JRE 11 yes/no (default yes)? ")
        if len(r.strip())>0:
            java_version = r.strip()
        install_dir = jdk.install(java_version, jre=True)
        print(f"Installed Java in {install_dir}; remove that dir to uninstall")
        for root, dirs, files in os.walk('/Users/parrt/.jre/jdk-11.0.15+10-jre'):
            if root.endswith("/bin"):
                java = os.path.join(root, 'java')
        if java is None:
            print(f"Can't find bin/java in {install_dir}; installation failed")
            exit(1)

    if jar is None:
        exit(1)

    p = subprocess.Popen(['java', '-cp', jar, 'org.antlr.v4.Tool']+args, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    out, err = p.communicate()
    out = out.decode("UTF-8")
    err = err.decode("UTF-8")

    if err: print(err)
    if out: print(out)


if __name__ == '__main__':
    main()