grammar T;
s : a a;
a : {_input.LT(1).equals("x")}? ID INT {System.out.println("alt 1");}
  | {_input.LT(1).equals("y")}? ID INT {System.out.println("alt 2");}
  ;
ID : 'a'..'z'+ ;
INT : '0'..'9'+;
WS : (' '|'\n') {skip();} ;
