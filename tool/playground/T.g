grammar T;
s : e ';' ;
e : e '*' e
  | ID
  | INT
  ;
INT : '0'..'9'+;
WS : (' '|'\n') {skip();} ;
