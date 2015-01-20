grammar <grammarName>;
set: ('b'|'c') ;
a: 'a' set 'd' {System.out.println($set.stop);} ;