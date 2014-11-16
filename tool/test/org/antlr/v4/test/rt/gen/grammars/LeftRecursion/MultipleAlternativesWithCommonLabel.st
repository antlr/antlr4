grammar <grammarName>;
s : e {<writeln("$e.v")>}; 
e returns [int v]
  : e '*' e     {$v = <Cast("BinaryContext","$ctx")>.e(0).v * <Cast("BinaryContext","$ctx")>.e(1).v;}  # binary
  | e '+' e     {$v = <Cast("BinaryContext","$ctx")>.e(0).v + <Cast("BinaryContext","$ctx")>.e(1).v;}  # binary
  | INT         {$v = $INT.int;}                   # anInt
  | '(' e ')'   {$v = $e.v;}                       # parens
  | left=e INC  {<Cast("UnaryContext","$ctx"):Concat(".INC() != null"):Assert()>$v = $left.v + 1;}      # unary
  | left=e DEC  {<Cast("UnaryContext","$ctx"):Concat(".DEC() != null"):Assert()>$v = $left.v - 1;}      # unary
  | ID          {<AssignLocal("$v","3")>}                                                     # anID
  ; 
ID : 'a'..'z'+ ;
INT : '0'..'9'+ ;
INC : '++' ;
DEC : '--' ;
WS : (' '|'\n') -> skip ;
