grammar <grammarName>;

stat:   ( '(' expr? ')' )? EOF ;
expr:   ID '=' STR ;

ERR :   '~FORCE_ERROR~' ;
ID  :   [a-zA-Z]+ ;
STR :   '"' ~["]* '"' ;
WS  :   [ \t\r\n]+ -> skip ;