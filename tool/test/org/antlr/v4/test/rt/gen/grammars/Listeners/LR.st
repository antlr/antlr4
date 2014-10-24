grammar <grammarName>;
@parser::header {
<ImportListener(grammarName)>
}

@parser::members {
<LRListener(grammarName)>
}

s
@after {
<ToStringTree("$ctx.r"):writeln()>
<WalkListener("$ctx.r")>
}
	: r=e ;
e : e op='*' e
	| e op='+' e
	| INT
	;
MULT: '*' ;
ADD : '+' ;
INT : [0-9]+ ;
ID  : [a-z]+ ;
WS : [ \t\n]+ -> skip ;
