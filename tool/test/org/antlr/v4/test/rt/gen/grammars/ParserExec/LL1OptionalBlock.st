grammar <grammarName>;
a : (ID|{}INT)? {
<writeln("$text")>
};
ID : 'a'..'z'+;
INT : '0'..'9'+ ;
WS : (' '|'\n') -> skip;
