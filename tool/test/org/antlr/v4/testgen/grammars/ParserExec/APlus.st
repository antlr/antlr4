grammar <grammarName>;
a : ID+ {
<writeln("$text")>
};
ID : 'a'..'z'+;
WS : (' '|'\n') -> skip;
