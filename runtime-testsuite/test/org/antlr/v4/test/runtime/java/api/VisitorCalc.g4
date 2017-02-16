grammar VisitorCalc;

s	:	expr EOF
	|	EOF
	;

s2	:	;

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
NL  : [\r\n] -> channel(HIDDEN); // separate these out so TerminalNodeWithHidden can check associating tokens with the current line
WS  : [ \t]+ -> channel(HIDDEN);
LINE_COMMENT : '//' ~'\n'* '\n' -> channel(HIDDEN) ;
COMMENT : '/*' .*? '*/' -> channel(HIDDEN) ;