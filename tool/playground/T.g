grammar T;

s : a+ ';' ;

a
@after {
	System.out.println($start);
}
 : ID|INT ;

ID : 'a'..'z'+;
INT : '0'..'9'+;
SEMI : ';';
ASSIGN : '=';
PLUS : '+';
MULT : '*';
WS : [ \n\t]+ -> skip;
