lexer grammar E;
I : '0'..'9'+ {System.out.println("I");} ;
ID : [a-zA-Z] [a-zA-Z0-9]* ;
WS : [ \n\u000D] -> skip ;
