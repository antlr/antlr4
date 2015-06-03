grammar <grammarName>;
s @after {<ToStringTree("$ctx"):writeln()>} : a ;
a : a ID
  | ID
  ;
ID : 'a'..'z'+ ;
WS : (' '|'\n') -> skip ;
