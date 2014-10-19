grammar <grammarName>;
s : a a a;
a : {<false>}? ID  {<writeln("\"alt 1\"")>}
  | {<true>}?  INT {<writeln("\"alt 2\"")>}
  ;
ID : 'a'..'z'+ ;
INT : '0'..'9'+;
WS : (' '|'\n') -> skip ;
