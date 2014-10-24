grammar <grammarName>;
start : ID ':' expr;
expr : primary expr? {<Pass()>} | expr '->' ID;
primary : ID;
ID : [a-z]+;
