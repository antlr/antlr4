"""
Process descriptors and replace annotations with block text.
"""
import re
import os
import glob

output_dir = "/tmp/output/"
output_dir = "."

pattern = r"/\*\*(.+?)\*/\s+@CommentHasStringValue\s+(public String \w+);"

def update(filename, java):
    # skip copyright comment
    package_idx = java.index("package org.antlr.v4.test.runtime.descriptors")
    java_ = java[package_idx:]
    # Match one at a time so we can replace and not track altered indexes
    match = re.search(pattern, java_, re.DOTALL)
    while match is not None:
        # for matchNum, match in enumerate(matches, start=1):
        a,b = match.span()
        # print("span", a, b, java_[a:b])
        #     print("COMMENT")#,match.group(1))
        span = match.span(1)
        string = java_[span[0]:span[1]].lstrip('\n')
        string = string.rstrip()
        #     print("span", span, ":", f"!{string}!")
        #     print("VAR")#,match.group(2))
        span = match.span(2)
        field = java_[span[0]:span[1]]
        #     print("span", span, ":", field)

        repl = f'''{field} = """
{string}
""";'''
        java_ = java_[0:a] + repl + java_[b:]
        match = re.search(pattern, java_, re.DOTALL)
    # print(java[0:package_idx] + java_)
    # print("-----------------------")
    return java[0:package_idx] + java_


for filename in glob.glob("*.java"):
# for filename in glob.glob("SetsDescriptors.java"):
    output_filename = output_dir+"/"+filename
    print(filename+"->"+output_filename)
    with open(filename, "r", encoding='UTF-8') as f:
        java = f.read()
    java_ = update(filename, java)
    # os.unlink("/tmp/output")
    # os.mkdir("/tmp/output")
    with open(output_filename, "w", encoding='UTF-8') as f:
        f.write(java_)