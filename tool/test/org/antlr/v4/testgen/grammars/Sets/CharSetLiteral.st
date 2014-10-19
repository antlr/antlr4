grammar <grammarName>;
a : (A {<writeln("$A.text")>})+ ;
a : [AaBb] ;
WS : (' '|'\n')+ -> skip ;
