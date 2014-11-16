grammar M;
import S, T;
b : 'b'|'c' {<writeln("\"M.b\"")>}|B|A;
WS : (' '|'\n') -> skip ;
