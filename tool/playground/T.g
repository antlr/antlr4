grammar T;
s : e # foo;
f : ID # A
  | INT # B
  ;
e : e ('+='<assoc=right>|'-='<assoc=right>) e 
  | INT					     
  ;
ID : 'a'..'z'+ ;
INT : '0'..'9'+;
WS : (' '|'\n') {skip();} ;
