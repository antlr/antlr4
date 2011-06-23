parser grammar T;

r[int a] returns [int b]
	:	x=ID y=r[34] {$b = 99;}
	;

b	: r[34] ;
