lexer grammar <grammarName>;
ACTION2 : '[' (STRING | ~'"')*? ']';
STRING : '"' ('\\"' | .)*? '"';
WS : [ \t\r\n]+ -> skip;