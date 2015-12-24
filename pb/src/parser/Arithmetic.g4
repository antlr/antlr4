grammar Arithmetic;

options { language = Go; }

equation  
    : expression relop expression
    ;

expression 
    : multiplyingExpression ((PLUS|MINUS) multiplyingExpression)*
    ;

multiplyingExpression  
    : powExpression ((TIMES|DIV) powExpression)*
    ;

powExpression
    : atom (POW expression)?
    ;

atom 
    : scientific
    | variable
    | LPAREN expression RPAREN
    ;

scientific
    : number (E number)?
    ;

relop 
    : EQ | GT | LT
    ;

number
    : MINUS? DIGIT+ (POINT DIGIT+)?
    ;

variable
    : MINUS? LETTER (LETTER | DIGIT)*;
            
LPAREN 
    : '('
    ;

RPAREN 
    : ')'
    ;

PLUS 
    : '+'
    ;

MINUS 
    : '-'
    ;

TIMES 
    : '*'
    ;

DIV 
    : '/'
    ;

GT 
    : '>'
    ;

LT 
    : '<'
    ;

EQ
    : '='
    ;

POINT
    : '.'
    ;

E
    : 'e'
    | 'E'
    ;

POW
    : '^'
    ;

LETTER
    : ('a'..'z') | ('A'..'Z')
    ;

DIGIT
    : ('0'..'9')
    ;

WS 
    : [ \r\n\t]+ -> channel(HIDDEN)
    ;
