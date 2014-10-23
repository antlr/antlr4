grammar <grammarName>;
s
@init {
<BuildParseTrees()>
}
@after {
<ToStringTree("$r.ctx"):writeln()>
}
  : r=a ;
a : b 'x'
  ;
b : 'y' 
  ;
