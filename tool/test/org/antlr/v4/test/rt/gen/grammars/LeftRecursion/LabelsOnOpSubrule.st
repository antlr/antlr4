grammar <grammarName>;
s @after {<ToStringTree("$ctx"):writeln()>} : e;
e : a=e op=('*'|'/') b=e  {}
  | INT {}
  | '(' x=e ')' {}
  ;
INT : '0'..'9'+ ;
WS : (' '|'\n') -> skip ;
