grammar <grammarName>;
@parser::header {
<importListener(grammarName)>
}

@parser::members {
<BasicListener(grammarName)>
}

s
@after {
<ToStringTree("$ctx.r"):writeln()>
<walkListener("$ctx.r")>
}
  : r=a ;
a : INT INT
  | ID
  ;
MULT: '*' ;
ADD : '+' ;
INT : [0-9]+ ;
ID  : [a-z]+ ;
WS : [ \t\n]+ -> skip ;