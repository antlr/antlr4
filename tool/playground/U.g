grammar U;
s : a ;
a : ID (',' ID)* ';' ;

INT : '0'..'9'+ ;
ID : 'a'..'z'+ ;
WS : (' '|'\n')+ {skip();} ;
