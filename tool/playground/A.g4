grammar A;

s : Q q=e {Object o=$q.v;} -> one
  | z=e {Object o=$z.v;}
  ;

e returns [int v]
  : a=e op='*' b=e {$v = $a.v * $b.v;}  -> mult
  | b=e '+' e
  | '(' x=e ')'
  ;

INT : '9';
/*
a : u=A A -> x
  | B b {Token t=$B;} -> y
  | C+  -> z
  | d=D
  ;

b : B ;
*/
