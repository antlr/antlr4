lexer grammar <grammarName>;
A : ["a-z]+ {<writeln("\"A\"")>} ;
WS : [ \n\t]+ -> skip ;
