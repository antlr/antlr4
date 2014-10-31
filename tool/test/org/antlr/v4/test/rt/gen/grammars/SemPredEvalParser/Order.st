grammar <grammarName>;
s : a {} a; // do 2x: once in ATN, next in DFA;
// action blocks lookahead from falling off of 'a'
// and looking into 2nd 'a' ref. !ctx dependent pred
a : ID {<writeln("\"alt 1\"")>}
  | {<True()>}?  ID {<writeln("\"alt 2\"")>}
  ;
ID : 'a'..'z'+ ;
INT : '0'..'9'+;
WS : (' '|'\n') -> skip ;
