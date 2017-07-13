#!/bin/bash

# Tries to determine what JAR the user just finished packaging

JARFILE=`ls -t1 /opt/project/tool/target/antlr4-*-complete.jar`;
java -Xmx500M -cp "$JARFILE:$CLASSPATH" "org.antlr.v4.Tool" $@;