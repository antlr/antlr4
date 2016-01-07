grammar T;
prog
@init {_interp.SetPredictionMode(PredictionModeLL_EXACT_AMBIG_DETECTION);}
	: expr_or_assign*;
expr_or_assign
	: expr '++' {fmt.Println("fail.")}
	|  expr {fmt.Println("pass: "+$expr.text)}
	;
expr: expr_primary ('<-' ID)?;
expr_primary
	: '(' ID ')'
	| ID '(' ID ')'
	| ID
	;
ID  : [a-z]+ ;