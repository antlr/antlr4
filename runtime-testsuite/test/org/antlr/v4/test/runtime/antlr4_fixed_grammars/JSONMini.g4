grammar JSONMini;
json : value EOF ;
value
    : STRING
    | NUMBER
    | object
    | array
    | 'true'
    | 'false'
    | 'null'
    ;
object : '{' pair (',' pair)* '}' | '{' '}' ;
pair : STRING ':' value ;
array : '[' value (',' value)* ']' | '[' ']' ;
STRING : '"' (~['"'\\] | '\\' .)* '"' ;
NUMBER : '-'? INT ('.' [0-9]+)? EXP? ;
fragment INT : '0' | [1-9] [0-9]* ;
fragment EXP : [Ee] [+-]? [0-9]+ ;
WS : [ \t\r\n]+ -> skip ;