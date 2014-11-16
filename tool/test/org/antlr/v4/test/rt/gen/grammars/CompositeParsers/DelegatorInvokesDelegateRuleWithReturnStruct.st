grammar M;
import S;
s : a {<write("$a.text")>} ;
B : 'b' ; // defines B from inherited token space
WS : (' '|'\n') -> skip ;
