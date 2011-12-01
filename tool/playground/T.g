grammar T;
s : a a;
a :          ID {System.out.println("alt 1");}
  | {true}?  ID {System.out.println("alt 2");}
  ;
ID : 'a'..'z'+ ;
INT : '0'..'9'+;
WS : (' '|'\n') {skip();} ;
