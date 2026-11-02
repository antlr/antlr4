// Vendored tiny lexer fixture for the Kotoba runtime v1.
// The Java tool is not invoked; runtime/Kotoba implements an
// ATN-less scanner that matches this grammar by hand.
lexer grammar Tiny;

ID     : [a-zA-Z_] [a-zA-Z_0-9]* ;
INT    : [0-9]+ ;
PLUS   : '+' ;
STAR   : '*' ;
LPAREN : '(' ;
RPAREN : ')' ;
WS     : [ \t\r\n]+ -> skip ;
