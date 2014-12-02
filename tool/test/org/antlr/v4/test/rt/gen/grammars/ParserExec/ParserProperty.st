grammar <grammarName>;
<ParserPropertyMember()>
a : {$parser.Property()}? ID {<writeln("\"valid\"")>}
  ;
ID : 'a'..'z'+ ;
WS : (' '|'\n') -> skip ;
