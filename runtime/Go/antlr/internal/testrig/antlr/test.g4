grammar test;

stat: expression 
    | IDENTIFIER ';'
    ;

expression
    : expression (AND expression)+
    | IDENTIFIER
    ;

AND : 'and' ;
IDENTIFIER : [a-zA-Z_]+ ;
WS : [ \t\r\n]+ -> skip ;
