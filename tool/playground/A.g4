grammar A;

s : INT { System.out.println($start.getText());} ;

INT : [0-9]+ ;
WS : [ \t\n]+ -> skip ;
