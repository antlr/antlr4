grammar MiniConfig;
config : (pair | section)* EOF ;
section : '[' NAME ']' (pair)* ;
pair : NAME '=' VALUE NEWLINE? ;
NAME : [a-zA-Z_][a-zA-Z0-9_]* ;
VALUE : ~[\r\n]+ ;
NEWLINE : ('\r'? '\n')+ ;
WS : [ \t]+ -> skip ;