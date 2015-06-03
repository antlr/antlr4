lexer grammar <grammarName>;
ACTION : '{' (ACTION | ~[{}])* '}';
WS : [ \r\n\t]+ -> skip;
