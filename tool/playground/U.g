grammar U;
options {output=AST;}

a : INT ID -> ID INT ;

INT : '0'..'9'+ ;
ID : 'a'..'z'+ ;
WS : (' '|'\n')+ {skip();} ;
