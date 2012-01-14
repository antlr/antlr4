grammar A;

s : q=e {System.out.println("result = "+$e.v);} ;

e returns [int v]
  : a=e op='*' b=e {$v = $a.v * $b.v;}	-> mult
  | a=e '+' b=e {$v = $a.v + $b.v;}	-> add
  | INT 	{$v = $INT.int;}
  | '(' x=e ')' {$v = $x.v;}
  | e '++' 				-> inc
  | e '--'
  | ID					-> anID
  ;

INT : '0'..'9'+ ;
WS : (' '|'\n')+ {skip();} ;
