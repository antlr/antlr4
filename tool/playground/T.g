grammar T;
options {output=AST;}
a : x+=A^ y+=b B! B b!;

b : B ;

A : 'a';
B : 'b';
WS : ' '|'\t'|'\n' {skip();} ;

/*
r[int a] returns [int b]
scope {int qq;}
	:	x=ID y=r[34] z+=b {$b = 99;}
	;

b	: r[34] {$r::qq = 3;} ;
*/
