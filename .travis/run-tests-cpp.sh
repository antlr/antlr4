#!/bin/bash

if [ $GROUP == "LEXER" ]; then
    mvn -q -Dgroups="org.antlr.v4.test.runtime.category.LexerTests" -Dtest=cpp.* test 
elif [ $GROUP == "PARSER" ]; then
    mvn -q -Dgroups="org.antlr.v4.test.runtime.category.ParserTests" -Dtest=cpp.* test 
elif [ $GROUP == "RECURSION" ]; then
    mvn -q -Dgroups="org.antlr.v4.test.runtime.category.LeftRecursionTests" -Dtest=cpp.* test 
else
    mvn -q -Dtest=cpp.* test 
fi

