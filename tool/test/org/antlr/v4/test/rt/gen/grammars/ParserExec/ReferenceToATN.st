grammar <grammarName>;
a : (ID|ATN_)* ATN_? {<writeln("$text")>} ;
ID : 'a'..'z'+ ;
ATN_ : '0'..'9'+;
WS : (' '|'\n') -> skip ;