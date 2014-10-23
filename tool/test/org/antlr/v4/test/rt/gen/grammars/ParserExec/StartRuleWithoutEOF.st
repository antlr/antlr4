grammar <grammarName>;
s @after { <DumpDFA()> }
  : ID | ID INT ID ;
ID : 'a'..'z'+ ;
INT : '0'..'9'+;
WS : (' '|'\t'|'\n') -> skip;
