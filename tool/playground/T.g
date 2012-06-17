grammar T;

s : ( INT {System.out.println("-> "+$INT.text);})+ ;

ID : 'a'..'z'+ ;
INT : '0'..'9'+ ;
WS : [ \n]+ -> skip ;
