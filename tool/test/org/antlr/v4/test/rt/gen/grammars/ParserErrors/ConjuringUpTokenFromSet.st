grammar <grammarName>;
a : 'a' x=('b'|'c') {<Append("\"conjured=\"","$x"):writeln()>} 'd' ;
