grammar T;
s : ID ;
ID : 'a'..'z'+ ;
WS : (' '|'\n') {skip();} ;
