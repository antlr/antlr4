grammar BooleanExpr;
expr: expr AND expr
    | expr OR expr
    | NOT expr
    | '(' expr ')'
    | BOOL ;
AND: 'AND';
OR: 'OR';
NOT: 'NOT';
BOOL: 'TRUE' | 'FALSE';
WS: [ \t\r\n]+ -> skip;
