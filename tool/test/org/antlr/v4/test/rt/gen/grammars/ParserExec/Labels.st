grammar <grammarName>;
a : b1=b b2+=b* b3+=';' ;
b : id_=ID val+=INT*;
ID : 'a'..'z'+ ;
INT : '0'..'9'+;
WS : (' '|'\n') -> skip ;
