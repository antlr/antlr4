grammar T;

s returns [int j=9999] : e[9] {{System.out.println("after-e "+$j);}} {true}? ';' ;

e[int i]
  : {$i>=0}? {{System.out.println("i=="+$i);}} ID
  | ID '!'
  ;

foo[int j] : e[8] {$j==2}? '$' ; // not called but in FOLLOW(e)

ID : 'a'..'z'+;

WS : (' '|'\n')+ {skip();} ;
