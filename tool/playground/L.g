lexer grammar L;
I : '0'..'9'+ {System.out.println("I");} ;
WS : (' '|'\n') {skip();} ;
