grammar <grammarName>;
file_
@after {<ToStringTree("$ctx"):writeln()>}
  : para para EOF ;
para: paraContent NL NL ;
paraContent : ('s'|'x'|{<LANotEquals("2","TParser.NL")>}? NL)+ ;
NL : '\n' ;
s : 's' ;
X : 'x' ;
