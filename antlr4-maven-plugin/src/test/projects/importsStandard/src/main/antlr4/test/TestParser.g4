parser grammar TestParser;

options { tokenVocab=TestLexer; }

document  :  (Comment | Name) EOF ;