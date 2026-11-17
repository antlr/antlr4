grammar CSVFlexible;
file : row+ EOF ;
row : field (',' field)* NEWLINE ;
field : TEXT | STRING ;
TEXT : ~[\r\n,"]+ ;
STRING : '"' ('""'|~'"')* '"' ;
NEWLINE : ('\r'? '\n')+ ;
WS : [ \t]+ -> skip ;