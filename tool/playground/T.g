grammar T;
s   : expr expr 
    | expr
    ;
expr: '@'
    | ID '@'
    | ID
    ;
ID  : [a-z]+ ;
WS  : [ \r\n\t]+ -> skip ;
