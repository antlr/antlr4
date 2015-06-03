lexer grammar <grammarName>;
I : .*? ('a' | 'ab') {<Text():writeln()>} ;
WS : (' '|'\n') -> skip ;
J : . {<Text():writeln()>};
