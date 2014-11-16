lexer grammar <grammarName>;
A : '-' I ;
I : '0'..'9'+ ;
WS : (' '|'\n') -> skip ;
