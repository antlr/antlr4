grammar <grammarName>;
s @after {<ToStringTree("$ctx"):writeln()>} : a ;
a : a ID {<False()>}?\<fail='custom message'>
  | ID
  ;
ID : 'a'..'z'+ ;
WS : (' '|'\n') -> skip ;
