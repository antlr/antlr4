#!/bin/zsh

alias antlr4='java -Xmx500M -cp "./antlr4-4.12.1-SNAPSHOT-complete.jar:$CLASSPATH" org.antlr.v4.Tool'

antlr4 -Dlanguage=Go -visitor -listener -package test -o ../test *.g4