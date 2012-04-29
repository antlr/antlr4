grammar T;
s : r=e ;
e : e '(' INT ')'
  | INT 
  ;     
MULT: '*' ;
ADD : '+' ;
INT : [0-9]+ ;
WS : [ \t\n]+ -> skip ;
