grammar CSVFlexible;
file: row+ ;
row: value (',' value)* NEWLINE ;
value: QUOTED | TEXT? ;
TEXT: ~[,"\r\n]+ ;
QUOTED: '"' (~["\r\n] | '""')* '"' ;
NEWLINE: '\r'? '\n' ;
WS: [ \t]+ -> skip ;
