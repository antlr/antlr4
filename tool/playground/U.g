grammar U;
options {output=AST;}
tokens {DECL;}
a : ID '=' INT -> ^(DECL ID INT) ;

INT : '0'..'9'+ ;
ID : 'a'..'z'+ ;
WS : (' '|'\n')+ {skip();} ;
