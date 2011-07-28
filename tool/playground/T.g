grammar T;

s : e[9] {true}? ';' ;

e[int i]
  : {$i>=0}? {{System.out.println("i=="+$i);}} ID
  | ID '!'
  ;

foo[int j] : e[8] {$j==2}? '$' ; // not called but in FOLLOW(e)

ID : 'a'..'z'+;

WS : (' '|'\n')+ {skip();} ;
