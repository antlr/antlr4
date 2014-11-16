grammar <grammarName>;
s 
@init {<LL_EXACT_AMBIG_DETECTION()>}
@after {<DumpDFA()>}
	: '{' stat* '}' ;
stat: 'if' ID 'then' stat ('else' ID)?
		| 'return'
		;
ID : 'a'..'z'+ ;
WS : (' '|'\t'|'\n')+ -> skip ;
