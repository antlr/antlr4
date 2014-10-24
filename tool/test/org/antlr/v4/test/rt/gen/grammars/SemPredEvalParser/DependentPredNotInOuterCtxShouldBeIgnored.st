grammar <grammarName>;
s : b[2] ';' |  b[2] '.' ; // decision in s drills down to ctx-dependent pred in a;
b[int i] : a[i] ;
a[int i]
  : {<ValEquals("$i","1")>}? ID {<writeln("\"alt 1\"")>}
    | {<ValEquals("$i","2")>}? ID {<writeln("\"alt 2\"")>}
    ;
ID : 'a'..'z'+ ;
INT : '0'..'9'+;
WS : (' '|'\n') -> skip ;

