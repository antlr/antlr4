grammar T;

s : e ';' ;

e : e '+' e
  | INT
  ;

INT : [0-9]+ ;
WS : [ \r\n\t]+ -> skip ;
