lexer grammar <grammarName>;
ENUM : 'enum' { <false> }? ;
ID : 'a'..'z'+ ;
WS : (' '|'\n') -> skip;
