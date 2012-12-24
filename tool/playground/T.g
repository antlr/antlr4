grammar T;

s : a | b ;
a : c ID ;
b : c INT ID ;
c : INT
  |
  ;
INT : [0-9]+ ;
ID : [a-z]+ ;
WS : [ \r\t\n]+ -> skip ;
