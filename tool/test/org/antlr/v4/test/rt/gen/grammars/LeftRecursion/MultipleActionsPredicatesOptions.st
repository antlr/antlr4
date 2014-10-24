grammar <grammarName>;
s @after {<ToStringTree("$ctx"):writeln()>} : e ;
e : a=e op=('*'|'/') b=e  {}{true}?
  | a=e op=('+'|'-') b=e  {}\<p=3>{true}?\<fail='Message'>
  | INT {}{}
  | '(' x=e ')' {}{}
  ;
INT : '0'..'9'+ ;
WS : (' '|'\n') -> skip ;
