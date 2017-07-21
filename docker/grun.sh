#!/bin/bash

# You can set JVM options for this command by putting them into the ANTLR_JVM_OPTS environment variable.

if [ -z "${ANTLR_JVM_OPTS}" ]; then
    ANTLR_JVM_OPTS="-Xmx500M"
fi;

# Tries to determine what JAR the user just finished packaging
JARFILE=`ls -t1 /opt/project/tool/target/antlr4-*-complete.jar`;

java -cp "$JARFILE:$CLASSPATH" \
"org.antlr.v4.gui.TestRig" \
$@;
