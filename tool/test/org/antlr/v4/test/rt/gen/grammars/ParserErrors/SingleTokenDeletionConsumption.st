grammar <grammarName>;
myset: ('b'|'c') ;
a: 'a' myset 'd' {<writeln("$myset.stop")>} ;
