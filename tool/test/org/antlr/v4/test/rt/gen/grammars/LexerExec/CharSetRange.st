lexer grammar <grammarName>;
I : [0-9]+ {<writeln("\"I\"")>} ;
ID : [a-zA-Z] [a-zA-Z0-9]* {<writeln("\"ID\"")>} ;
WS : [ \n\u0009\r]+ -> skip ;
