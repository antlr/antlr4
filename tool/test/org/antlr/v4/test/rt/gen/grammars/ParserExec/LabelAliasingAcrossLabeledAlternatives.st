grammar <grammarName>;
start : a* EOF;
a
  : label=subrule {<writeln("$label.text")>} #One
  | label='y' {<writeln("$label.text")>} #Two
  ;
subrule : 'x';
WS : (' '|'\n') -> skip ;
