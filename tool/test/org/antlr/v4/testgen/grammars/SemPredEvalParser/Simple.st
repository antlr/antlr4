grammar <grammarName>;
s : a a a; // do 3x: once in ATN, next in DFA then INT in ATN
a : {<false>}? ID {<writeln("\"alt 1\"")>}
  | {<true>}?  ID {<writeln("\"alt 2\"")>}
  | INT         {<writeln("\"alt 3\"")>}
  ;
ID : 'a'..'z'+ ;
INT : '0'..'9'+;
WS : (' '|'\n') -> skip ;
