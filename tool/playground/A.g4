grammar A;

s : e ;

e : e '*' e 		-> Mult
  | e '+' e 		-> Add
  | INT        		-> Int
  | '(' e ')'		-> Parens
  ;

INT : [0-9]+ ;
WS : [ \t\n]+ -> skip ;
