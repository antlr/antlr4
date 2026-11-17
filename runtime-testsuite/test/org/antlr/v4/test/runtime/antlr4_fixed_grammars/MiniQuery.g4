grammar MiniQuery;
query : 'SELECT' columns 'FROM' table ('WHERE' condition)? EOF ;
columns : '*' | ID (',' ID)* ;
table : ID ;
condition : expr ;
expr
    : ID OP value
    | expr AND expr
    | expr OR expr
    | '(' expr ')'
    ;
value : STRING | NUMBER ;
ID : [a-zA-Z_][a-zA-Z0-9_]* ;
STRING : '\'' (~['\\\r\n])* '\'' ;
NUMBER : [0-9]+ ;
AND : 'AND' ;
OR : 'OR' ;
OP : '=' | '<>' | '<' | '>' | '<=' | '>=' ;
WS : [ \t\r\n]+ -> skip ;