grammar BaseContext;

mainRule
	:	A* B
	;

mainRuleNoA
options { baseContext = mainRule; }
	:	B
	;

listLabelPrimary
	:	B C?
	;

listLabelAlternative
options { baseContext = listLabelPrimary; }
	:	B+
	;

labeledAlts1
	:	B	# contextName1
	;

labeledAlts2
options { baseContext = labeledAlts1; }
	:	B	# contextName2
	;

expr
	:	B
	|	expr A expr
	;

exprPrimaryOnly
options { baseContext = expr; }
	:	B
	;

indirectReference
	:	a=mainRule b=mainRuleNoA
	;

A : 'A';
B : 'B';
C : 'C';
