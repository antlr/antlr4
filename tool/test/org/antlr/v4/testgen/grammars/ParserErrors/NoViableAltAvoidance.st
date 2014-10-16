grammar <grammarName>;
s : e '!' ;
e : 'a' 'b'
  | 'a'
  ;
DOT : '.' ;
WS : [ \t\r\n]+ -> skip;
