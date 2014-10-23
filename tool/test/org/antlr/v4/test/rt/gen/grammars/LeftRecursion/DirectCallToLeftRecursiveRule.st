grammar <grammarName>;
a @after {<ToStringTree("$ctx"):writeln()>} : a ID
  | ID
  ;
ID : 'a'..'z'+ ;
WS : (' '|'\n') -> skip ;
