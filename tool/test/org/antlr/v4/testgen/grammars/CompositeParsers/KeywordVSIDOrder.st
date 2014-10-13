grammar M;
import S;
a : A {<writeln("\"M.a: \"+$A")>};
A : 'abc' {<writeln("\"M.A\"")>};
WS : (' '|'\n') -> skip ;
