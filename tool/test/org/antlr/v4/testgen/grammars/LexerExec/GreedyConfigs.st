lexer grammar <grammarName>;
I : ('a' | 'ab') {<writeln("this.text")>} ;
WS : (' '|'\n') -> skip ;
J : .;
