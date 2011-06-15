lexer grammar T;

STRING_START : '"' {pushMode(STRING_MODE); more();} ;

WS : ' ' | '\n' {skip();} ;

mode STRING_MODE;

STRING : '"' {popMode();} ;

ANY : . {more();} ;
