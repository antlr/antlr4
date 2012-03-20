grammar T;
s : a a a;
a : {false}? ID {System.out.println("alt 1");}
  | {true}?  ID {System.out.println("alt 2");}
  | INT         {System.out.println("alt 3");}
  ;
ID : 'a'..'z'+ ;
INT : '0'..'9'+;
WS : (' '|'\n') {skip();} ;
