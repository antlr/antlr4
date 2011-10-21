grammar T;
s : i=ifstat  {System.out.println(_input.toString(0,_input.index()-1));} ;

ifstat : 'if' '(' expr ')' assign ;
assign : ID '=' expr ';' ;
expr : INT | ID ;

EQ : '=' ;
INT : '0'..'9'+ ;
ID : 'a'..'z'+ ;
WS : (' '|'\n')+ {skip();} ;
