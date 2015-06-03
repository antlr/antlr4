lexer grammar <grammarName>;
I : [0-9]+ {<writeln("\"I\"")>} ;
WS : [ \u]+ -> skip ;
