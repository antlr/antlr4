lexer grammar <grammarName>;
E1 : 'enum' { <False()> }? ;
E2 : 'enum' { <True()> }? ;  // winner not E1 or ID
ID : 'a'..'z'+ ;
WS : (' '|'\n') -> skip;
