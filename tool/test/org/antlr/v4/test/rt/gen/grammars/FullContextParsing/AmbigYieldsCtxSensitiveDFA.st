grammar <grammarName>;
s @after {<DumpDFA()>}
	: ID | ID {} ;
ID : 'a'..'z'+;
WS : (' '|'\t'|'\n')+ -> skip ;
