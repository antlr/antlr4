grammar <grammarName>;
s : a ';' a;
// ';' helps us to resynchronize without consuming
// 2nd 'a' reference. We our testing that the DFA also
// throws an exception if the validating predicate fails
a : {<False()>}? ID  {<writeln("\"alt 1\"")>}
  | {<True()>}?  INT {<writeln("\"alt 2\"")>}
  ;
ID : 'a'..'z'+ ;
INT : '0'..'9'+;
WS : (' '|'\n') -> skip ;
