grammar <grammarName>;
a : 'a' x='b' {<Append("\"conjured=\"","$x"):writeln()>} 'c' ;