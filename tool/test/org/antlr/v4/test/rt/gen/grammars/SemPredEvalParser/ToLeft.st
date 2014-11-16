grammar <grammarName>;
	s : a+ ;
a : {<False()>}? ID {<writeln("\"alt 1\"")>}
  | {<True()>}?  ID {<writeln("\"alt 2\"")>}
  ;
ID : 'a'..'z'+ ;
INT : '0'..'9'+;
WS : (' '|'\n') -> skip ;
