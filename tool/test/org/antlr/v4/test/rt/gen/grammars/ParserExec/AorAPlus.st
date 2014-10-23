grammar <grammarName>;
a : (ID|ID)+ {
<writeln("$text")>
};
ID : 'a'..'z'+;
WS : (' '|'\n') -> skip;
