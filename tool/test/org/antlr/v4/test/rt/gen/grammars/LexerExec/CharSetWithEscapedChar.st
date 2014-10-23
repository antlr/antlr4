lexer grammar <grammarName>;
DASHBRACK : [\\-\]]+ {<writeln("\"DASHBRACK\"")>} ;
WS : [ \u]+ -> skip ;
