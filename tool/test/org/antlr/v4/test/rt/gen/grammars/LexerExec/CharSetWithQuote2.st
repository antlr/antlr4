lexer grammar <grammarName>;
A : ["\\ab]+ {<writeln("\"A\"")>} ;
WS : [ \n\t]+ -> skip ;
