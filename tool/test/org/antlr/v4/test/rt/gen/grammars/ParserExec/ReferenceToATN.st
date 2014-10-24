grammar <grammarName>;
a : (ID|ATN)* ATN? {<writeln("$text")>} ;
ID : 'a'..'z'+ ;
ATN : '0'..'9'+;
WS : (' '|'\n') -> skip ;