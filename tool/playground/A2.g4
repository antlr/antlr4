grammar A2;

s : e {System.out.println($e.v);} ;

e returns [int v]
  : a=e '*' b=e {$v = $a.v * $b.v;}		-> Mult
  | a=e '+' b=e {$v = $a.v + $b.v;}		-> Add
  | INT         {$v = $INT.int;} 		-> Int
  | '(' x=e ')' {$v = $x.v;} 			-> Parens
  ;

INT : [0-9]+ ;
WS : [ \t\n]+ -> skip ;
