grammar <grammarName>;
start : e[0] EOF;
e[int _p]
    :   ( 'a' | 'b'+ ) ( {3 >= $_p}? '+' e[4] )*
    ;

