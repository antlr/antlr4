grammar T;
s : e ;
e : a=e op=('*'|'/') b=e  {}
  | INT {}
  | '(' x=e ')' {}
  ;
INT : '0'..'9'+ ;
WS : (' '|'\n') {skip();} ;
