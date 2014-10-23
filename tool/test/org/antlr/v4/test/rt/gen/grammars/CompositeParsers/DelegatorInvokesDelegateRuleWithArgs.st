grammar M;
import S;
s : label=a[3] {<writeln("$label.y")>} ;
B : 'b' ; // defines B from inherited token space
WS : (' '|'\n') -> skip ;
