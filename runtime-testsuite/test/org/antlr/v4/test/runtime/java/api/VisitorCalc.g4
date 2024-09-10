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
WS : [ \t\r\n]+ -> channel(HIDDEN);
LINE_COMMENT : '//' ~'\n'* '\n' -> channel(HIDDEN) ;
COMMENT : '/*' .*? '*/' -> channel(HIDDEN) ;