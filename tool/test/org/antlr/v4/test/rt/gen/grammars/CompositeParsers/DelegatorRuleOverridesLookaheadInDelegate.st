grammar M;
import S;
prog : decl ;
type_ : 'int' | 'float' ;
ID  : 'a'..'z'+ ;
INT : '0'..'9'+ ;
WS : (' '|'\n') -> skip;
