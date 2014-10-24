grammar <grammarName>;
start : ID ':' expr;
expr : primary expr? {} | expr '->' ID;
primary : ID;
ID : [a-z]+;
