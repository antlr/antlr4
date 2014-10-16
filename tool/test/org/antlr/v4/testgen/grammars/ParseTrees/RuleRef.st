grammar <grammarName>;
s
@init {
<buildParseTrees()>
}
@after {
<ToStringTree("$r.ctx"):writeln()>
}
  : r=a ;
a : b 'x'
  ;
b : 'y' 
  ;
