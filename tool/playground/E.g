lexer grammar E;
I : ~[ab] ~[cd]* {System.out.println("I");} ;
WS : [ \n\u000D]+ -> skip ;
