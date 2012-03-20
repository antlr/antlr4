grammar T;
s : a | 'x';
a : 'a' s ('b' s)?;

VOID : 'void';
ID : 'a'..'z'+;
INT : '0'..'9'+;
WS : (' '|'\n') {skip();} ;
