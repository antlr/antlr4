grammar T;
options {tokenVocab=A;}
s : ID ;
ID : 'a'..'z'+ ;
WS : (' '|'\n') {skip();} ;
