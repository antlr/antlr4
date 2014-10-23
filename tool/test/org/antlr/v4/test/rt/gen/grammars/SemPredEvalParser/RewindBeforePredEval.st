grammar <grammarName>;
s : a a;
a : {<LTEquals("1", "\"x\"")>}? ID INT {<writeln("\"alt 1\"")>}
  | {<LTEquals("1", "\"y\"")>}? ID INT {<writeln("\"alt 2\"")>}
  ;
ID : 'a'..'z'+ ;
INT : '0'..'9'+;
WS : (' '|'\n') -> skip ;
