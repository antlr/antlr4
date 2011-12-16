grammar T;
s@after {dumpDFA();}
    : '{' stat* '}'    ;
stat: 'if' ID 'then' stat ('else' 'foo')?
    | 'return'
    ;ID : 'a'..'z'+ ;
WS : (' '|'\t'|'\n')+ {skip();} ;
