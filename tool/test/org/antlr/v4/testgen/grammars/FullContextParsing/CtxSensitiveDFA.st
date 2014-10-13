grammar <grammarName>;
s @after {<dumpDFA()>}
  : '$' a | '@' b ;
a : e ID ;
b : e INT ID ;
e : INT | ;
ID : 'a'..'z'+ ;
INT : '0'..'9'+ ;
WS : (' '|'\t'|'\n')+ -> skip ;
