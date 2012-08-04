/*
dead end configs: 
(12,1,[$]):Atom 'b'<1>
(8,2,[$],up=1):Atom '!'<4>
(20,2,[$],up=1):Atom 'a'<2>
line 1:1 no viable alternative at input 'a.'
line 1:1 mismatched input '.' expecting '!'
*/
grammar T;

s : e '!' ;

e : 'a' 'b'
  | 'a'
  ;

//x : e 'a' 'c' ;

DOT : '.' ;
WS : [ \t\r\n]+ -> skip;
