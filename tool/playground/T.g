grammar T;
/* This is ambig too.
s_ : s EOF ;
s : a s
  |
  ;
*/

s : (a)* EOF ; // ambig; can match A B in alt 3 or alt 2 then alt 1
a : e '!'
  | e
  ;
e : B
  | A		// both alts 2,3 can reach end of s upon abEOF
  | A B
  ;
A : 'a' ;
B : 'b' ;
WS : (' '|'\n')+ {skip();} ;
