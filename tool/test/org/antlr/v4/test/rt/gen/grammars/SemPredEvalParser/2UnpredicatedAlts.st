grammar <grammarName>;
s : {<LL_EXACT_AMBIG_DETECTION()>} a ';' a; // do 2x: once in ATN, next in DFA
a : ID {<writeln("\"alt 1\"")>}
  | ID {<writeln("\"alt 2\"")>}
  | {<False()>}? ID {<writeln("\"alt 3\"")>}
  ;
ID : 'a'..'z'+ ;
INT : '0'..'9'+;
WS : (' '|'\n') -> skip ;
