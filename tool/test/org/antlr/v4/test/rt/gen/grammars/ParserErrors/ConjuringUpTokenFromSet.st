grammar <grammarName>;
a : 'a' x=('b'|'c') {<writeln("\"conjured=\"+$x")>} 'd' ;
