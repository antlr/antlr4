lexer grammar <grammarName>;
ENUM : 'enum' { <False()> }? ;
ID : 'a'..'z'+ ;
WS : (' '|'\n') -> skip;
