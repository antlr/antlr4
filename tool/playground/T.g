grammar T;

s : x B ;
x : A B | A | A ;
s2 : x B ;


A : 'a';
B : 'b' ;
WS : [ \t\n\r]+ -> channel(HIDDEN) ;
