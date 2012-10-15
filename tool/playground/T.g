grammar T;
s@after {dumpDFA();}
    : ID | ID {;} ;
ID : 'a'..'z'+ ;
WS : (' '|'\t'|'\n')+ {skip();} ;
