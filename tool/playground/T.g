grammar T;
s : ~(ID|X)* X ;
b : ~(ID|X)*? X ;
A : 'a' ;
B : 'b' ;
X : 'x' ;
ID : 'id' ;
WS : [ \t\r\n]+ -> skip ;
