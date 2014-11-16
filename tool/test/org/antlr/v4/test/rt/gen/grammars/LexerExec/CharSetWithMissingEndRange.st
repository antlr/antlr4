lexer grammar <grammarName>;
I : [0-]+ {<writeln("\"I\"")>} ;
WS : [ \n\u000D]+ -> skip ;
