"""
Process descriptors and replace annotations with block text.
"""
import re
import os
import glob

pattern = r"/\*\*(.+?)\*/\s+@CommentHasStringValue\s+(public String \w+);"

def update(filename, java):
    matches = re.finditer(pattern, java, re.DOTALL)

    for matchNum, match in enumerate(matches, start=1):
        span = match.span()
    #     print("span", span)
    #     print("COMMENT")#,match.group(1))
        span = match.span(1)
        string = java[span[0]:span[1]].lstrip('\n')
        string = string.rstrip()
    #     print("span", span, ":", f"!{string}!")
    #     print("VAR")#,match.group(2))
        span = match.span(2)
        field = java[span[0]:span[1]]
    #     print("span", span, ":", field)

        print(f'''
{field} = """
{string}
""";
        ''')


for filename in glob.glob("*.java"):
    print(filename+"--------------------------")
    with open(filename, "r") as f:
        java = f.read()
    java = java[java.index("package org.antlr.v4.test.runtime.descriptors"):] # skip copyright comment
    update(filename, java)

