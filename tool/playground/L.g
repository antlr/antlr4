lexer grammar L;
STRING_START : '"' {pushMode(STRING_MODE); more();} ;
WS : ' '|'
' {skip();} ;
mode STRING_MODE;
STRING : '"' {popMode();} ;
ANY : . {more();} ;
