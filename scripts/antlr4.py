import os
import sys
import subprocess
from shutil import which
from pathlib import Path
from urllib.request import urlopen
import json


# def cached_mvn_versions():
#     files = [name for name in os.listdir(mvn_repo) if name.startswith("4.")]
#     return sorted(files)

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
        download_antlr4(jar, version)
    return jar


def download_antlr4(jar, version):
    os.makedirs(os.path.join(mvn_repo, version))
    with urlopen(f"https://repo1.maven.org/maven2/org/antlr/antlr4/{version}/antlr4-{version}-complete.jar") as response:
        s = response.read()
        with open(jar, "wb") as f:
            f.write(s)


if __name__ == '__main__':
    args = sys.argv[1:]
    homedir = Path.home()
    mvn_repo = os.path.join(homedir, '.m2', 'repository', 'org', 'antlr', 'antlr4')

    version = None
    if args[0]=='-v':
        version = args[1]
        args = args[2:]

    if version is None:
        version = latest_version()

    jar = antlr4_jar(version)

    java = which("java")
    if java is None:
        print("No java available to run ANTLR")
        exit(1)

    p = subprocess.Popen(['java', '-cp', jar, 'org.antlr.v4.Tool']+args, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    out, err = p.communicate()
    out = out.decode("UTF-8")
    err = err.decode("UTF-8")

    if err: print(err)
    if out: print(out)
