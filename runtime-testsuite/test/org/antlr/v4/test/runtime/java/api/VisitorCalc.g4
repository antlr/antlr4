grammar VisitorCalc;

s	:	expr EOF
	|	EOF
	;

s2	:	;

s3  :   ID '=' expr ';' ;

expr
	:	INT						# number
	|	ID						# variable
	|	expr (MUL | DIV) expr	# multiply
	|	expr (ADD | SUB) expr	# add
	|	expr '[' expr ']'		# array
	;

INT : [0-9]+;
ID  : [a-zA-Z_] [a-zA-Z0-9_]* ;
MUL : '*';
DIV : '/';
ADD : '+';
SUB : '-';
NL  : [\r\n] -> channel(HIDDEN); // separate these out so TerminalNodeWithHidden can check associating tokens with the current line
WS  : [ \t]+ -> channel(HIDDEN);
LINE_COMMENT : '//' ~'\n'* '\n' -> channel(HIDDEN) ;
COMMENT : '/*' .*? '*/' -> channel(HIDDEN) ;