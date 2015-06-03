lexer grammar <grammarName>;
HexLiteral : '0' ('x'|'X') HexDigit+ ;
DecimalLiteral : ('0' | '1'..'9' '0'..'9'*) ;
FloatingPointLiteral : ('0x' | '0X') HexDigit* ('.' HexDigit*)? ;
DOT : '.' ;
ID : 'a'..'z'+ ;
fragment HexDigit : ('0'..'9'|'a'..'f'|'A'..'F') ;
WS : (' '|'\n')+;
