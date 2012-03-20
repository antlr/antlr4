grammar T;
s : ID | b ;
b : INT | VOID ;

VOID : 'void';
ID : 'a'..'z'+;
INT : '0'..'9'+;
WS : (' '|'\n') {skip();} ;
