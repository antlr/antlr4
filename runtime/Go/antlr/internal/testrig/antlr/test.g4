grammar test;


// Rules
query
	 : LPAREN query RPAREN         # embbedExpr
	 | query AND query             # andExpr
	 | query OR query              # orExpr
	 | leftexpr op=operations      # simpleExpr
	 ;

operations
            : op=(EQ | NEQ) r=(INT | STRING)         # equalExp
            | op=(GT | LT | GE | LE) INT		  	 # compareIntExp
            | op=(   IN   | NIN
                   | HALL | HANY | HNONE
                   | WAO
                   | ITM
                  )
                     list	  					      # listExp
            | op=( VNT | VNTE | VOT | VOTE | VE
                       | WAO
                 )
                     STRING	  					      # versionExp
            ;

leftexpr  : ATTRNAME;

list      : LBRACKET element (COMMA element)* RBRACKET;

element   : INT | STRING | list;

// Tokens/keywords
EQ       : '=';
LT       : '<';
GT       : '>';
LE       : '<=';
GE       : '>=';
LPAREN   : '(';
RPAREN   : ')';
COMMA    : ',';
RBRACKET : ']';
LBRACKET : '[';
NEQ      : 'not=';
IN       : 'in';
NIN      : 'not-in';
ITM      : 'in-time-range';
HALL     : 'has-all';
HNONE    : 'has-none';
HANY     : 'has-any';
AND      : 'and';
OR       : 'or';
TRUE     : 'true';
FALSE    : 'false';

VNT      : 'newer-than';
VNTE     : 'newer-than-or-equal-to';
VOT      : 'older-than';
VOTE     : 'older-than-or-equal-to';
VE       : 'equals-version';
WAO      : 'within-any-of';

INT      : [0-9] [0-9]*;
FLOAT    : ('+' | '-')? DIGIT+ '.' DIGIT*;
STRING   : '"' (ESC | ~ ["\\])* '"';

// Skip all whitespace
//
WS       : [ \t\r\n]+ -> skip;

ATTRNAME : ALPHA ATTR_NAME_CHAR*;

// This is a catch for any character that the lexer does not cover. You can give a senisble error message here
ERRCHAR  : . { /* Register a lexer problem here */} -> skip;

fragment ESC            : '\\' (["\\/bfnrt] | UNICODE);
fragment UNICODE        : 'u' HEX HEX HEX HEX;
fragment HEX            : [0-9a-fA-F];
fragment ATTR_NAME_CHAR : '-' | '_' | DIGIT | ALPHA;
fragment DIGIT          : ('0' ..'9');
fragment ALPHA          : ( 'A' ..'Z' | 'a' ..'z');