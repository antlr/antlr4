grammar T;
options {output=AST;}
tokens {I;}
a : A b ';' -> A b ';' ;

b : B ;

A : 'a';
B : 'b';
C : 'c';
D : 'd';
WS : ' '|'\t'|'\n' {skip();} ;

/*
r[int a] returns [int b]
scope {int qq;}
	:	x=ID y=r[34] z+=b {$b = 99;}
	;

b	: r[34] {$r::qq = 3;} ;
*/
