"""
$ python update_antlr_version.py 4.9.3 4.10

Read file
"""
import sys
import os
import subprocess


def runme(cmd):
    return subprocess.check_output(cmd.split(' '))


def freshen(ROOT):
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
    files = {}
    with open(fname, "r") as f:
        for line in f.readlines():
            line = line.strip()
            if len(line)>0 and not line.startswith("#"):
                if line.startswith('*'): # '*' implies change multiple lines
                    files[line[1:].strip()] = True
                else:
                    files[line] = False

    return files


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
    else:
        # print(f"{before} => {after} in {qfname}")
        text = text.replace(before, after)
        with open(qfname, "w", encoding="UTF-8") as f:
            f.write(text)


def update_files(ROOT, before, after):
    files = get_change_list(f"{ROOT}/scripts/files-to-update.txt")
    for fname,multi in files.items():
        update_file(f"{ROOT}/{fname}", multi, before, after)


def find_remaining(ROOT, before):
    return


if __name__ == '__main__':
    # This is where parrt puts antlr
    ROOT = f"{os.path.expanduser('~')}/antlr/code/antlr4"
    # ROOT = f"/tmp/antlr4" # for testing, it's nice to have diff target

    before = sys.argv[1]
    after = sys.argv[2]
    print(f"Updating ANTLR version from {before} to {after}")

    root = input(f"Set ANTLR repo root (default {ROOT}): ")
    if len(root.strip())>0:
        ROOT = root

    freshen(ROOT)

    update_files(ROOT, before, after)

    find_remaining(ROOT, before)

    print("Warning: manually update runtime/Cpp/runtime/src/Version.h has non-standard version mechanism")
    print("Warning: some targets can't handle 4.x; must be 4.x.0:")
    print("\truntime/Dart/pubspec.yaml")
    print("\truntime/JavaScript/package.json")
