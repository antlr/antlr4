grammar T;

s:   expr_or_assign* ;
 
expr_or_assign
    :   expr '++'
    |   expr    
    ;
   
expr : expr_primary ('<-' ID)? ;
 
expr_primary
    : '(' ID ')'
    | ID '(' ID ')'
    | ID
    ;

ID  : [a-z]+ ;
WS : [ \t\r\n]+ -> skip ;
