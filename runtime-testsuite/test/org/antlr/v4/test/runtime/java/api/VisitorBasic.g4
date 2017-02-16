grammar VisitorBasic;

s	:	'A' EOF
	;

b : c ;

c : B c B
  | A
  ;

A : 'A';
B : 'B';
