grammar T;
options {k=3;}
s : ID ;
ID : 'a'..'z'+ ;
WS : (' '|'\n') {skip();} ;
