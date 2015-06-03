lexer grammar <grammarName>;
I : '0'..'9'+ {<writeln("\"I\"")>} ;
WS : [ \n\u000D]+ -> skip ;
