grammar <grammarName>;
myset: ('b'|'c') ;
a: 'a' myset 'd' {<writeln(Append("\"\"","$myset.stop"))>} ; <! bit complicated because of the JavaScript target !>
