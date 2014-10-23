grammar <grammarName>;
stat : ifstat | 'x';
ifstat : 'if' stat ('else' stat)?;
WS : [ \n\t]+ -> skip ;
