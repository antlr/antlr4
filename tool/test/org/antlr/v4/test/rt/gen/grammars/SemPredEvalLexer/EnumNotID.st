lexer grammar <grammarName>;
ENUM : [a-z]+  { <TextEquals("enum")> }? ;
ID : [a-z]+  ;
WS : (' '|'\n') -> skip;
