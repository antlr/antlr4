lexer grammar M;
import S;
A : 'a' B {<writeln("\"M.A\"")>};
WS : (' '|'\n') -> skip ;
