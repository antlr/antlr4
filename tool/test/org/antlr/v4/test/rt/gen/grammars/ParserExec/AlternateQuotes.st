parser grammar ModeTagsParser;
options { tokenVocab=ModeTagsLexer; } // use tokens from ModeTagsLexer.g4
file_: (tag | TEXT)* ;
tag : '«' ID '»'
    | '«' '/' ID '»'
    ;
