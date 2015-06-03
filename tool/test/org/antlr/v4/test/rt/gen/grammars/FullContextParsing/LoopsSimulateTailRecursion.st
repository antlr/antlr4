grammar <grammarName>;
prog
@init {<LL_EXACT_AMBIG_DETECTION()>}
	: expr_or_assign*;
expr_or_assign
	: expr '++' {<writeln("\"fail.\"")>}
	|  expr {<writeln("\"pass: \"+$expr.text")>}
	;
expr: expr_primary ('\<-' ID)?;
expr_primary
	: '(' ID ')'
	| ID '(' ID ')'
	| ID
	;
ID  : [a-z]+ ;
