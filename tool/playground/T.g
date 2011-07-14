grammar T;
options {output=AST;}

a : ('+' | '-')^  ;
b returns [int i] : ID;
ID : 'a'..'z'+ ;
INT : '0'..'9'+;
PLUS : '+';
WS : (' '|'\n') {$channel=HIDDEN;} ;

/*
c : A B C -> A ( D A B C*)* (B A*)? ;

A : 'a';
B : 'b';
C : 'c';
D : 'd';
SEMI : ';';
WS : ' '|'\t'|'\n' {skip();} ;
*/

/*
r[int a] returns [int b]
scope {int qq;}
	:	x=ID y=r[34] z+=b {$b = 99;}
	;

b	: r[34] {$r::qq = 3;} ;
*/
