lexer grammar <grammarName>;
I : ({<writeln("\"stuff fail: \" + this.text")>} 'a'
| {<writeln("\"stuff0: \" + this.text")>}
		'a' {<writeln("\"stuff1: \" + this.text")>}
		'b' {<writeln("\"stuff2: \" + this.text")>})
		{<writeln("this.text")>} ;
WS : (' '|'\n') -> skip ;
J : .;
