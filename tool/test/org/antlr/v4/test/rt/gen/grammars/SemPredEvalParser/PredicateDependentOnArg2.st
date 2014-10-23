grammar <grammarName>;
@members {i=0}
s : a[2] a[1];
a[int i]" +
  : {<ValEquals("$i","1")>}? ID {<writeln("\"alt 1\"")>}
  | {<ValEquals("$i","2")>}? ID {<writeln("\"alt 2\"")>}
  ;
ID : 'a'..'z'+ ;
INT : '0'..'9'+;
WS : (' '|'\n') -> skip ;
