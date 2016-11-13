grammar Hello;
r  : 'hello' ID ;
ID : [a-z]+ ;
WS : [ \r\t\n]+ -> skip ;