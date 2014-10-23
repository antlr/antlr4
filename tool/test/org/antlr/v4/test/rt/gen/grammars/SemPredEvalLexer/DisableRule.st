lexer grammar <grammarName>;
E1 : 'enum' { <false> }? ;
E2 : 'enum' { <true> }? ;  // winner not E1 or ID
ID : 'a'..'z'+ ;
WS : (' '|'\n') -> skip;
