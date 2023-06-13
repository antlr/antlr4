grammar VisitorCalc;

s
	:	expr EOF
	|
	;

expr
	:	INT						# number
	|   ID                      # var
	|   ID '(' ')'              # func
	|	expr (MUL | DIV) expr	# multiply
	|	expr (ADD | SUB) expr	# add
	;

INT : [0-9]+;
ID  : [a-zA-Z_]+ ;
MUL : '*';
DIV : '/';
ADD : '+';
SUB : '-';
WS : [ \t]+ -> channel(HIDDEN);
