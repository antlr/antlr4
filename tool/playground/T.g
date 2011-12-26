grammar T;
s : A+ ;
A : [AaBb] ;
WS : (' '|'\n')+ {skip();} ;
