grammar <grammarName>;
prog
@init {<LL_EXACT_AMBIG_DETECTION()>}
	: expr expr {<writeln("\"alt 1\"")>}
	| expr
	;
expr: '@'
	| ID '@'
	| ID
	;
ID  : [a-z]+ ;
WS  : [ \r\n\t]+ -> skip ;
