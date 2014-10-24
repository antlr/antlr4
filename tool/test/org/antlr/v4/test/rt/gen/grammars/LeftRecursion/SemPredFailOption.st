grammar <grammarName>;
s @after {<ToStringTree("$ctx"):writeln()>} : a ;
a : a ID {false}?\<fail='custom message'>
  | ID
  ;
ID : 'a'..'z'+ ;
WS : (' '|'\n') -> skip ;
