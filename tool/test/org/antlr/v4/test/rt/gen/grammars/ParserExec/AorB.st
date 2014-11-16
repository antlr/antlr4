grammar <grammarName>;
a : ID {
<writeln("\"alt 1\"")>
} | INT {
<writeln("\"alt 2\"")>
};
ID : 'a'..'z'+ ;
INT : '0'..'9'+;
WS : (' '|'\\n') -> skip ;
