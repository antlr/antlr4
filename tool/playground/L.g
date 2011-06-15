lexer grammar L;

WS : ' '+ {skip();} ;

StringLiteral
    :  '"' ( ~('\\'|'"') )* '"'
    ;
