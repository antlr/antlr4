grammar <grammarName>;
file_ @init{
<BailErrorStrategy()>
} 
@after {
<ToStringTree("$ctx"):writeln()>
}
  :   item (SEMICOLON item)* SEMICOLON? EOF ;
item : A B?;
SEMICOLON: ';';
A : 'a'|'A';
B : 'b'|'B';
WS      : [ \r\t\n]+ -> skip;

