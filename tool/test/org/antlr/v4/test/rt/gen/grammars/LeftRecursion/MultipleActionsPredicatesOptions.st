grammar <grammarName>;
s @after {<ToStringTree("$ctx"):writeln()>} : e ;
e : a=e op=('*'|'/') b=e  {}{<True()>}?
  | a=e op=('+'|'-') b=e  {}\<p=3>{<True()>}?\<fail='Message'>
  | INT {}{}
  | '(' x=e ')' {}{}
  ;
INT : '0'..'9'+ ;
WS : (' '|'\n') -> skip ;
