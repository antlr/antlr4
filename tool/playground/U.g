grammar U;
s : '{' stat* '}' ;
// if x then break else if y then return else break
// still ambig: if x then if y then break else break
stat: 'if' ID 'then' stat ('else' stat)?
    | 'break'
    | 'return'
    ;

INT : '0'..'9'+ ;
ID : 'a'..'z'+ ;
WS : (' '|'\n')+ {skip();} ;
