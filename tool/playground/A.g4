grammar A;

s : e ;

e : e '*' e 		-> Mult
  | e '+' e 		-> Add
  | INT        		-> primary
  | ID        		-> primary
  | '(' e ')'		-> Parens
  ;

/*
primary : EEE;

add : A ;
*/

ID : [a-z]+ ;
INT : [0-9]+ ;
WS : [ \t\n]+ -> skip ;
