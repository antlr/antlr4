grammar Hello;

r : 'hello' ID ;
ID : [A-Za-z0-9]+ ;
WS : [ \t\r\n]+ -> skip;
