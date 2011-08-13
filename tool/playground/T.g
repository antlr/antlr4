grammar T;
s : ifstat '.' {System.out.println(input.toString(0,input.index()-1));} ;
ifstat : 'if' '(' INT ')' .* ;
EQ : '=' ;
INT : '0'..'9'+ ;
ID : 'a'..'z'+ ;
WS : (' '|'\n')+ {skip();} ;
