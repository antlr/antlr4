grammar <grammarName>;
s : {<LL_EXACT_AMBIG_DETECTION()>} a ';' a ';' a;
a : INT {<writeln("\"alt 1\"")>}
  | ID {<writeln("\"alt 2\"")>} // must pick this one for ID since pred is false
  | ID {<writeln("\"alt 3\"")>}
  | {<False()>}? ID {<writeln("\"alt 4\"")>}
  ;
ID : 'a'..'z'+ ;
INT : '0'..'9'+;
WS : (' '|'\n') -> skip ;
