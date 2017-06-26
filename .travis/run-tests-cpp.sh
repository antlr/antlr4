#!/bin/bash

if [ $GROUP == "LEXER" ]; then GROUPS=-Dgroups="org.antlr.v4.test.runtime.category.LexerTests"
elif [ $GROUP == "PARSER" ]; then GROUPS=-Dgroups="org.antlr.v4.test.runtime.category.ParserTests"
elif [ $GROUP == "RECURSION" ]; then GROUPS=-Dgroups="org.antlr.v4.test.runtime.category.LeftRecursionTests"
else GROUPS=
fi

mvn -q $GROUPS -Dtest=cpp.* test # timeout due to no output for 10 min on travis if in parallel
