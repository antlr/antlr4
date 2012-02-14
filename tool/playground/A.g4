grammar A;

/*
Auto gen:
public Token ID() { }
public List<Token> x() { }
public Token x(int i) { }
public eContext e() { }
or: public List<eContext> e() { }
*/
z : ID x+=INT+ e {List x = $INT; Token t = $ID;} ;

/*
s : Q q=e {Object o=$q.v;} -> one
  | z=e {Object o=$z.v;}
  ;

e returns [int v]
  : a=e op='*' b=e {$v = $a.v * $b.v;}  -> mult
  | b=e '+' e
  | '(' x=e ')'
  ;
*/
/*
INT : '9';
a : u=A A -> x
  | B b {Token t=$B;} -> y
  | C+  -> z
  | d=D
  ;

b : B ;
*/
