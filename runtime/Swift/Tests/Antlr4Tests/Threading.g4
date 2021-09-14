grammar Threading;

NUMBER : [0-9] ;
WS : [ \r\n\t] + -> skip ;

operation : l=NUMBER op='+' r=NUMBER ;