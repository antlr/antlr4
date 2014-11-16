grammar <grammarName>;
s : a a a; // do 3x: once in ATN, next in DFA then INT in ATN
a : {<False()>}? ID {<writeln("\"alt 1\"")>}
  | {<True()>}?  ID {<writeln("\"alt 2\"")>}
  | INT         {<writeln("\"alt 3\"")>}
  ;
ID : 'a'..'z'+ ;
INT : '0'..'9'+;
WS : (' '|'\n') -> skip ;
