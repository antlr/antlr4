lexer grammar E;
DUH : 'eee' {int y=1;}
    | 'fff' {int z=3;}
    ;
I : '0'..'9'+ {System.out.println("I");}
  | 'z'       {int x = 2;}
  ;
WS : (' '|'\n') -> type(WS) ;
