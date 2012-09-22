grammar A;

s : INT { System.out.println($ctx.getStart());} ;

INT : [0-9]+ ;
WS : [ \t\n]+ -> skip ;
