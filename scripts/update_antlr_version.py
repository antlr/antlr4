"""
$ python update_antlr_version.py 4.9.3 4.10

Read file
"""
import sys
import os
import subprocess


def runme(cmd):
    return subprocess.check_output(cmd.split(' '))


def freshen():
    ok = input("Perform antlr4 `mvn clean` and wipe build dirs Y/N? (default no): ")
    ok = ok.lower()
    if ok.lower() != 'y' and ok !='yes':
        print("Ok, not cleaning antlr4 dir")
        return
    # runme("mvn clean")
    runme(f"rm -rf {ROOT}/runtime/CSharp/src/bin")
    runme(f"rm -rf {ROOT}/runtime/CSharp/src/obj")
    runme(f"rm -rf {ROOT}/runtime/Cpp/runtime/build")
    runme(f"rm -rf {ROOT}/runtime/gen")
    runme(f"rm -rf {ROOT}/runtime/JavaScript/dist")


def get_change_list(fname):
    files = []
    with open(fname, "r") as f:
        for line in f.readlines():
            line = line.strip()
            if len(line)>0 and not line.startswith("#"):
                files.append(line)
    return files


def update_file(qfname, before, after):
    with open(qfname, "r", encoding="UTF-8") as f:
        text = f.read()
    if before not in text:
        print(f"{before} not in {qfname}")
        return
    base = os.path.basename(qfname)
    print(f"{before} => {after} in {base}")
    text = text.replace(before, after)
    # with open(qfname, "w", encoding="UTF-8") as f:
    #     f.write(text)


def update_files(ROOT, TARGET_ROOT, before, after):
    files = get_change_list(f"{TARGET_ROOT}/scripts/files-to-update.txt")
    for fname in files:
        update_file(f"{ROOT}/{fname}", before, after)


if __name__ == '__main__':
    # This is where parrt puts antlr
    ROOT = f"{os.path.expanduser('~')}/antlr/code/antlr4"
    TARGET_ROOT = f"/tmp/antlr4" # for testing, it's nice to have diff target

    before = sys.argv[1]
    after = sys.argv[2]
    print(f"Updating ANTLR version from {before} to {after}")

    root = input(f"Set ANTLR repo root (default {ROOT}): ")
    if len(root.strip())>0:
        ROOT = root

    troot = input(f"Set _target_ ANTLR repo root (default {TARGET_ROOT}): ")
    if len(troot.strip())>0:
        TARGET_ROOT = troot

    update_files(ROOT, TARGET_ROOT, before, after)
    freshen()