grammar UnitExpr;
expr: NUMBER UNIT ;
NUMBER: [0-9]+ ('.' [0-9]+)? ;
UNIT: [a-zA-Z/_]+ ;
WS: [ \t\r\n]+ -> skip ;
