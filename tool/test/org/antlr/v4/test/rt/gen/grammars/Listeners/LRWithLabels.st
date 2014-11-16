grammar <grammarName>;
@parser::header {
<ImportListener(grammarName)>
}

@parser::members {
<LRWithLabelsListener(grammarName)>
}

s
@after {
<ToStringTree("$ctx.r"):writeln()>
<WalkListener("$ctx.r")>
}
  : r=e ;
e : e '(' eList ')' # Call
  | INT             # Int
  ;
eList : e (',' e)* ;
MULT: '*' ;
ADD : '+' ;
INT : [0-9]+ ;
ID  : [a-z]+ ;
WS : [ \t\n]+ -> skip ;
