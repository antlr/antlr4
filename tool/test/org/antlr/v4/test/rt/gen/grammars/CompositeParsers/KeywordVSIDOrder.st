grammar M;
import S;
a : A {<Append("\"M.a: \"","$A"):writeln()>};
A : 'abc' {<writeln("\"M.A\"")>};
WS : (' '|'\n') -> skip ;
