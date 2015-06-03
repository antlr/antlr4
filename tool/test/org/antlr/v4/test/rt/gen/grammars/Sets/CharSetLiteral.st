grammar <grammarName>;
a : (A {<writeln("$A.text")>})+ ;
A : [AaBb] ;
WS : (' '|'\n')+ -> skip ;
