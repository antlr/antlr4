grammar T;
s   :   stat ;
stat:   expr[0] NEWLINE               
    |   ID '=' expr[0] NEWLINE       
    |   NEWLINE                     
    ;

expr[int _p]
    :   ( INT
        | ID
        | '(' expr[0] ')'
        )
        ( {5 >= $_p}? ('*'|'/') expr[6]
        | {4 >= $_p}? ('+'|'-') expr[5]
        )*
    ;

/*
expr:   expr ('*'|'/') expr      # MulDiv
    |   expr ('+'|'-') expr      # AddSub
    |   INT                      # int
    |   ID                       # id
    |   '(' expr ')'             # parens
    ;
*/

MUL :   '*' ; // assigns token name to '*' used above in grammar
DIV :   '/' ;
ADD :   '+' ;
SUB :   '-' ;
ID  :   [a-zA-Z]+ ;      // match identifiers
INT :   [0-9]+ ;         // match integers
NEWLINE:'\r'? '\n' ;     // return newlines to parser (is end-statement signal)
WS  :   [ \t]+ -> skip ; // toss out whitespace
