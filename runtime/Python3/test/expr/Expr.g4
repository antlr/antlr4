// Taken from "tool-testsuite/test/org/antlr/v4/test/tool/TestXPath.java"
// Builds ExprLexer.py and ExprParser.py

grammar Expr;
prog:   func+ ;
func:  'def' ID '(' arg (',' arg)* ')' body ;
body:  '{' stat+ '}' ;
arg :  ID ;
stat:   expr ';'                 # printExpr
    |   ID '=' expr ';'          # assign
    |   'return' expr ';'        # ret
    |   ';'                      # blank
    ;
expr:   expr ('*'|'/') expr      # MulDiv
    |   expr ('+'|'-') expr      # AddSub
    |   primary                  # prim
    ;
primary
    :   INT                      # int
    |   ID                       # id
    |   '(' expr ')'             # parens
	;
MUL :   '*' ; // assigns token name to '*' used above in grammar
DIV :   '/' ;
ADD :   '+' ;
SUB :   '-' ;
RETURN : 'return' ;
ID  :   [a-zA-Z]+ ;      // match identifiers
INT :   [0-9]+ ;         // match integers
NEWLINE:'\r'? '\n' -> skip;     // return newlines to parser (is end-statement signal)
WS  :   [ \t]+ -> skip ; // toss out whitespace