grammar <grammarName>;
a : A {<writeln("$A.text")>} ;
a : ('a'|B) ;  // this doesn't collapse to set but works
fragment
B : ~('a'|'c') ;
