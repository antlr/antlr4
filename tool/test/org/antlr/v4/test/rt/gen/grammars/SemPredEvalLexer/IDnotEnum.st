lexer grammar <grammarName>;
ENUM : [a-z]+  { <False()> }? ;
ID : [a-z]+  ;
WS : (' '|'\n') -> skip;
