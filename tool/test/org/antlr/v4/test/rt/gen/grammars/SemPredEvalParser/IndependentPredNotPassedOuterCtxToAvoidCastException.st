grammar <grammarName>;
s : b ';' |  b '.' ;
b : a ;
a
  : {<false>}? ID {<writeln("\"alt 1\"")>}
  | {<true>}? ID {<writeln("\"alt 2\"")>}
 ;
ID : 'a'..'z'+ ;
INT : '0'..'9'+;
WS : (' '|'\n') -> skip ;
