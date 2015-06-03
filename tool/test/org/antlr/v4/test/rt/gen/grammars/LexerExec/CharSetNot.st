lexer grammar <grammarName>;
I : ~[ab \n] ~[ \ncd]* {<writeln("\"I\"")>} ;
WS : [ \n\u000D]+ -> skip ;
