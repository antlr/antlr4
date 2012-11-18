grammar T;
s : A ;

A : 'a' ;

B : 'x' -> skip |'y' ;

WS : [ \t\r\n]+ -> skip ;
