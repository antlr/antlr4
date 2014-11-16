grammar <grammarName>;
a : b b* ';' ;
b : ID val+=(INT | FLOAT)*;
ID : 'a'..'z'+ ;
INT : '0'..'9'+;
FLOAT : [0-9]+ '.' [0-9]+;
WS : (' '|'\n') -> skip ;
