grammar T;

s : INT+ ;

ID : [a-z]+ ;
INT : [0-9]+ ;
WS : [ \t\n]+ -> skip ;
