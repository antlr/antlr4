lexer grammar M;
import S;
A : 'a' B {<write("\"M.A\"")>};
WS : (' '|'\n') -> skip ;
