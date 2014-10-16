grammar <grammarName>;
start : ID ':' expr;
expr : primary expr? {<pass()>} | expr '->' ID;
primary : ID;
ID : [a-z]+;
;
