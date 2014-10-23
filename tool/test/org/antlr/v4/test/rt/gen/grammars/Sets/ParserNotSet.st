grammar <grammarName>;
a : t=~('x'|'y') 'z' {<writeln("$t.text")>} ;
