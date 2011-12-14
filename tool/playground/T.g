grammar T;
@header {import java.util.*;}
s : a ';' a;
a :          ID {System.out.println("alt 1");}
  |          ID {System.out.println("alt 2");}
  | {false}? ID {System.out.println("alt 3");}
  ;
ID : 'a'..'z'+ ;
INT : '0'..'9'+;
WS : (' '|'\n') {skip();} ;
