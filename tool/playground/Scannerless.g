scannerless grammar T;

method : 'fun' name=ID '(' a+=arg (',' a+=arg)* ')' body ;

body : '{' (body|CMT|.)* '}' ; // nongreedy

stat:	'return' INT
	|	ID '=' expr
	;

expr:	atom ('*' atom)* ;

atom:	INT
	|	sql
	;

sql :	'select' '*' 'from' ID ;

// literals like 'select' become 'select' WS rules? hmm..nope
// might not want WS. or might need &!id-letter or something

always call WS implicitly after any token match; if don't want, then make WS undefined.

RETURN : 'return' {!Character.isJavaId(input.LA(1))}? WS ;

ID : 'a'..'z'+ ;

WS : (' '|'\n')* ;

CMT : '/*' (options {greedy=false;}:.)* '*'/ ;

