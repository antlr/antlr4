grammar <grammarName>;
s : e {<writeln("$e.v")>}; 
e returns [int v]
  : e '*' e     {$v = $ctx.e(0).v * $ctx.e(1).v;}  # binary
  | e '+' e     {$v = $ctx.e(0).v + $ctx.e(1).v;}  # binary
  | INT         {$v = $INT.int;}                   # anInt
  | '(' e ')'   {$v = $e.v;}                       # parens
  | left=e INC  {<assert("$ctx.INC() !== null")>;$v = $left.v + 1;}      # unary
  | left=e DEC  {<assert("$ctx.DEC() !== null")>;$v = $left.v - 1;}      # unary
  | ID          {$v = 3}                                                     # anID
  ; 
ID : 'a'..'z'+ ;
INT : '0'..'9'+ ;
INC : '++' ;
DEC : '--' ;
WS : (' '|'\n') -> skip ;
