grammar T;
s : A+ ;
A : {true}? 'a' ;
WS : (' '|'\n')+ {skip();} ;
