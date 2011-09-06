grammar T;
s : i=ifstat  {System.out.println(_input.toString(0,_input.index()-1));} ;
ifstat : 'if' '(' INT ')' ID '=' ID ';' ;

r[int x] returns [int y]
locals [int z]
	: name=ID
	;

EQ : '=' ;
INT : '0'..'9'+ ;
ID : 'a'..'z'+ ;
WS : (' '|'\n')+ {skip();} ;
