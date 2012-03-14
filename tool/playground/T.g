grammar T;
s : a a a;
a : {false}? ID 
  | {true}?  ID
  | INT       
  ;
ID : 'a'..'z'+ ;
INT : '0'..'9'+;
WS : (' '|'\n') {skip();} ;
