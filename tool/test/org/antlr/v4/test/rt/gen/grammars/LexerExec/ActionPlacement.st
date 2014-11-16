lexer grammar <grammarName>;
I : ({<PlusText("stuff fail: "):writeln()>} 'a'
| {<PlusText("stuff0: "):writeln()>}
		'a' {<PlusText("stuff1: "):writeln()>}
		'b' {<PlusText("stuff2: "):writeln()>})
		{<Text():writeln()>} ;
WS : (' '|'\n') -> skip ;
J : .;
