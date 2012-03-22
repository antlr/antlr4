grammar T;
s : f f EOF;
f : | x;
x : 'a' 'b';
INT : '0'..'9'+;
WS : (' '|'\n') {skip();} ;
