grammar T;
options {output=AST;}

s : e_[0] EOF ;

e_[int _p]
    :   e_primary {  }
        ( {19 >= $_p}? '['^ e_[0] ']'! )*
    ;
e_primary
    : INT
	| 'new'^ ID ('[' INT ']')+ 
    ;

ID : ('a'..'z'|'A'..'Z'|'_'|'$')+;
INT : '0'..'9'+ ;
WS : (' '|'\n') {skip();} ;
