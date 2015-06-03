grammar <grammarName>;
a : A {<writeln("$A.text")>} ;
A : h=~('b'|'c') ;
