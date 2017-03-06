#! /usr/bin/python

"""
Find all .g4 files and generate parsers in the same directory.
the antlr used should be the one located at user's mvn directory
the filename is antlr4-ANTLR_VERSION-SNAPSHOT.jar. You can get it
by running: "mvn install"

NOTE: In case of customized location of .m2 folder, you can change the
USER_M2 constant below.

the java version is used according to environment variable $JAVA_HOME.
"""

import fnmatch
import os.path
from subprocess import call

ANTLR_VERSION = '4.7'
USER_M2 = os.path.expanduser('~') + '/.m2/'
ANTLR4_FOLDER = USER_M2 + 'repository/org/antlr/antlr4/' + ANTLR_VERSION + '-SNAPSHOT/'
ANTLR4_JAR = ANTLR4_FOLDER + 'antlr4-' + ANTLR_VERSION + '-SNAPSHOT-complete.jar'


def jar_exists():
    """
    Finds the antlr4 jar.
    """
    return os.path.exists(ANTLR4_JAR)


def find_g4():
    """
    Find all g4 files and return a list of them.
    The recursive search starts from the directory containing
    this python file.
    """
    file_path = os.path.realpath(__file__)
    parent_folder = file_path[0:file_path.rindex('/')+1]
    res = []
    for cur, _, filenames in os.walk(parent_folder):
        cur_files = fnmatch.filter(filenames, "*.g4")
        res += [cur+'/'+cur_file for cur_file in cur_files]
    return res


def gen_parser(grammar):
    """
    Generate parser for the input g4 file.
    """
    java_home = os.environ['JAVA_HOME']
    java = java_home + '/bin/java'
    if not os.path.exists(java):
        print 'Cannot find java. Check your JAVA_HOME setting.'
        return

    call([java, '-jar', ANTLR4_JAR, '-Dlanguage=Swift', grammar])


def swift_test():
    """
    Run unit tests.
    """
    call(['swift', 'test'])


if __name__ == '__main__':
    if not jar_exists():
        print 'Run "mvn install" in antlr4 project root' + \
                'first or check mvn settings'
        exit()

    _ = [gen_parser(f) for f in find_g4()]
    swift_test()
