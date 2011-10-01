grammar W;

s : a ';' {System.out.println("done");} ;

a : '[' b ']'
  | '(' b ')'
  ;

b : c '^' INT ;

c : ID
  | INT
  ;

EQ : '=' ;
INT : '0'..'9'+ ;
ID : 'a'..'z'+ ;
WS : (' '|'\n')+ {skip();} ;
