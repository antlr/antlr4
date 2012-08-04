grammar A;

s : e ;

e : e '*' e 		# Mult
  | INT        		# primary
  ;

INT : [0-9]+ ;
WS : [ \t\n]+ -> skip ;
