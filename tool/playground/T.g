grammar T;
s : FOO;
a : e B ;
b : e A B ;
e : A | ;
ID : 'a'..'z'+ ;
INT : '0'..'9'+;
WS : (' '|'\n') {skip();} ;
