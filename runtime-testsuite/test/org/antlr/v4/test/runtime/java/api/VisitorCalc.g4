grammar VisitorCalc;

s
	:	expr EOF
	;

expr
	:	INT						# number
	|	expr (MUL | DIV) expr	# multiply
	|	expr (ADD | SUB) expr	# add
	;

INT : [0-9]+;
MUL : '*';
DIV : '/';
ADD : '+';
SUB : '-';
WS : [ \t]+ -> channel(HIDDEN);
