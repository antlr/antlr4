grammar T;
options {output=AST;}

s : e EOF ;

e : e_[0] ;

e_[int _p]
    :   e_primary
        ( {$_p <= 5}? '*'^ e_[6]{} 
        | {$_p <= 4}? '+'^ e_[5]{} 
        | {$_p <= 2}? '='<assoc=right>^ e_[2]{} 
        | {$_p <= 3}? '?'<assoc=right>^ e ':'! e_[3]{} 
        )*
    ;

e_primary
    : ID 
    ;

ID : 'a'..'z'+;

WS : (' '|'\n')+ {skip();} ;
