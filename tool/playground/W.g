grammar W;

s
@init {setBuildParseTree(true);}
@after {System.out.println(_localctx.toStringTree(this));}
  : a
  ;
 
a : 'x' | 'y'
  ;
Z : 'z'; 

EQ : '=' ;
INT : '0'..'9'+ ;
ID : 'a'..'z'+ ;
WS : (' '|'\n')+ {skip();} ;
