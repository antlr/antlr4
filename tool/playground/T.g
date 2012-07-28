lexer grammar T;

RBRACE : '}' ;

mode Action;

END_ACTION
    :   '}' -> popMode
    ;

