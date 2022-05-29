# Deploy targets, update version number at website, update Javadoc

import sys
import os
import subprocess


WEBSITE_ROOT  = '/Users/parrt/antlr/sites/website-antlr4'
ANTLR_M2_ROOT = '/Users/parrt/.m2/repository/org/antlr'

website_files_to_update = ['download.html',
                           'index.html',
                           'api/index.html',
                           'scripts/topnav.js']


def runme(cmd):
    return subprocess.check_output(cmd.split(' '))


def update_file(qfname, multi, before, after):
    with open(qfname, "r", encoding="UTF-8") as f:
        text = f.read()

    if before not in text:
        print(f"{before} not in {qfname}")
        return

    # Don't update if on > 1 line; too complex for tool
    lines = text.split('\n')
    count = sum(before in line for line in lines)
    if count>1 and not multi:
        print(f"{before} appears on {count} lines so _not_ updating {qfname}")

    # print(f"{before} => {after} in {qfname}")
    text = text.replace(before, after)
    with open(qfname, "w", encoding="UTF-8") as f:
        f.write(text)


def copy_javadoc(release_version):
    # release_version = release_version+"-SNAPSHOT" # testing
    os.chdir(WEBSITE_ROOT+"/api/Java")
    print("Javadoc copied:")
    runme(f"jar xf {ANTLR_M2_ROOT}/antlr4-runtime/{release_version}/antlr4-runtime-{release_version}-javadoc.jar")
    print(f"\tapi/Java updated from antlr4-runtime-{release_version}-javadoc.jar")
    os.chdir(WEBSITE_ROOT+"/api/JavaTool")
    runme(f"jar xf {ANTLR_M2_ROOT}/antlr4/{release_version}/antlr4-{release_version}-javadoc.jar")
    print(f"\tapi/JavaTool updated from antlr4-{release_version}-javadoc.jar")
    os.chdir(WEBSITE_ROOT+"/api/maven-plugin/latest")
    runme(f"jar xf {ANTLR_M2_ROOT}/antlr4-maven-plugin/{release_version}/antlr4-maven-plugin-{release_version}-javadoc.jar")
    print(f"\tapi/JavaTool updated from antlr4-maven-plugin-{release_version}-javadoc.jar")


def copy_jars(release_version):
    # release_version = release_version+"-SNAPSHOT" # testing
    print("Jars copied:")
    runme(f"cp {ANTLR_M2_ROOT}/antlr4-runtime/{release_version}/antlr4-runtime-{release_version}.jar {WEBSITE_ROOT}/download/antlr-runtime-{release_version}.jar")
    runme(f"cp {ANTLR_M2_ROOT}/antlr4/{release_version}/antlr4-{release_version}-complete.jar {WEBSITE_ROOT}/download/antlr-{release_version}-complete.jar")
    os.chdir(WEBSITE_ROOT+"/download")
    runme(f"git add antlr-{release_version}-complete.jar")
    runme(f"git add antlr-runtime-{release_version}.jar")
    print(f"\tantlr-{release_version}-complete.jar")
    print(f"\tantlr-runtime-{release_version}.jar")


def update_version():
    for fname in website_files_to_update:
        qfname = WEBSITE_ROOT + "/" + fname
        update_file(qfname, True, before, after)
    print("Version string updated. Please commit/push:")


if __name__ == '__main__':
    before = sys.argv[1]
    after = sys.argv[2]
    print(f"Updating ANTLR version from {before} to {after}")

    root = input(f"Set ANTLR website root (default {WEBSITE_ROOT}): ")
    if len(root.strip())>0:
        WEBSITE_ROOT = root

    update_version()
    os.chdir(WEBSITE_ROOT)

    copy_javadoc(release_version=after)
    copy_jars(release_version=after)

    print()
    print("Please look for and add new api files!!")
    print("Then MANUALLY commit/push:")
    print()
    print(f"cd {WEBSITE_ROOT}")
    print(f"git commit -a -m 'Update website, javadoc, jars to {after}'")
    print("git push origin gh-pages")
