lexer grammar <grammarName>;
A : [z-a9]+ {<writeln("\"A\"")>} ;
WS : [ \u]+ -> skip ;
