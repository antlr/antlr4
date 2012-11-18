grammar T;
s : A ;

A : 'a' ;

B : ('x' -> skip |'y') ;

C : 'd' {} | 'e' {} ;

WS : [ \t\r\n]+ -> skip ;
