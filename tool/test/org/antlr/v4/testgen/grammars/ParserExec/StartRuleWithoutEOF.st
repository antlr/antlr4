grammar <grammarName>;
s @after { <dumpDFA()> }
  : ID | ID INT ID ;
ID : 'a'..'z'+ ;
INT : '0'..'9'+;
WS : (' '|'\t'|'\n') -> skip;
