grammar T;

s : INT { System.out.println($start.getText());} ;

INT : [0-9]+ {$x.type = 3; String x = $text; $channel, $mode} ;
WS : [ \t\n]+ -> skip ;
