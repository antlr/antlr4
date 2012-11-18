grammar T;
s : A ;

A : {} 'a' ;

B : ('x' {}|{}'y') {} ;

WS : [ \t\r\n]+ -> skip ;
