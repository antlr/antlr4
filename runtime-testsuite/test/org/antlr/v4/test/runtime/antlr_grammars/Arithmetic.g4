grammar Arithmetic;
expr: expr op=('*'|'/') expr   # MulDiv
    | expr op=('+'|'-') expr   # AddSub
    | INT                      # Int
    | '(' expr ')'             # Parens ;
INT: [0-9]+;
WS: [ \t\r\n]+ -> skip;
