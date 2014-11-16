grammar <grammarName>;
s : a a;
a : {<False()>}? ID INT {<writeln("\"alt 1\"")>}
  | {<False()>}? ID INT {<writeln("\"alt 2\"")>}
  ;
ID : 'a'..'z'+ ;
INT : '0'..'9'+;
WS : (' '|'\n') -> skip ;
