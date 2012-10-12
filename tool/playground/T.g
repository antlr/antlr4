grammar T;
s   :   expr[0] ;

expr[int _p]
    :   ID
        ( {5 >= $_p}? '*' expr[6]
        | {4 >= $_p}? '+' expr[5]
        )*
    ;

ID  :   [a-zA-Z]+ ;      // match identifiers
WS  :   [ \t\r\n]+ -> skip ; // toss out whitespace
