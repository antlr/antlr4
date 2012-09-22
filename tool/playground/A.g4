grammar A;

s : INT ;
catch[T x] {foo}
catch[U y] {bar}
finally { xxxxx }

INT : [0-9]+ ;
WS : [ \t\n]+ -> skip ;
