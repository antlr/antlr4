grammar M;
import S, T;
b : 'b'|'c' {<write("\"M.b\"")>}|B|A;
WS : (' '|'\n') -> skip ;
