# Deploy targets, update version number at website, update Javadoc

import sys

WEBSITE_ROOT = '/Users/parrt/antlr/sites/website-antlr4'

website_files_to_update = ['download.html',
                           'index.html',
                           'api/index.html',
                           'scripts/topnav.js']

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


if __name__ == '__main__':
    before = sys.argv[1]
    after = sys.argv[2]
    print(f"Updating ANTLR version from {before} to {after}")

    root = input(f"Set ANTLR repo root (default {WEBSITE_ROOT}): ")
    if len(root.strip())>0:
        WEBSITE_ROOT = root

    for fname in website_files_to_update:
        qfname = WEBSITE_ROOT + "/" + fname
        update_file(qfname, True, before, after)

    print(f"WARNING: Manually update '{WEBSITE_ROOT}/download/index.html'")
    print("THEN, run:")
    print(f"cd {WEBSITE_ROOT}")
    print(f"git commit -a -m 'add {after} jars'")
    print("git push origin gh-pages")
