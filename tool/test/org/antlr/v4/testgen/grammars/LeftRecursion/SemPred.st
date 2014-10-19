grammar <grammarName>;
s @after {<ToStringTree("$ctx"):writeln()>} : a ;
a : a {<true>}? ID
  | ID
  ;
ID : 'a'..'z'+ ;
WS : (' '|'\n') -> skip ;
