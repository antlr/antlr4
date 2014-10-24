grammar <grammarName>;
start : expr EOF;
expr : 'x'
     | expr expr
     ;
