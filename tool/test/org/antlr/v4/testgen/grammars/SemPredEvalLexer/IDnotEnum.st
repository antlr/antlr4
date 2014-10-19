lexer grammar <grammarName>;
ENUM : [a-z]+  { <false> }? ;
ID : [a-z]+  ;
WS : (' '|'\n') -> skip;
