lexer grammar L;
HexLiteral : '0' 'x' HexDigit+ ;
DecimalLiteral : ('0' | '1'..'9' '0'..'9'*) ;
fragment HexDigit : ('0'..'9'|'a'..'f');
WS : (' '|'\n')+ ;
