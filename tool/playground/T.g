grammar T;
s : (ID | ID ID?) ';' ;
ID : 'a'..'z'+ ;
INT : '0'..'9'+;
WS : (' '|'\n') {skip();} ;
