grammar T;
s : e ;
e : e ('+='<assoc=right>|'-='<assoc=right>) e
  | INT
  ;
ID : 'a'..'z'+ ;
INT : '0'..'9'+;
WS : (' '|'\n') {skip();} ;
