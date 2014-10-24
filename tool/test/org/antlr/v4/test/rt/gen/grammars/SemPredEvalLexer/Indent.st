lexer grammar <grammarName>;
ID : [a-z]+  ;
INDENT : [ \t]+ { <TokenStartColumnEquals("0")> }?
         { <writeln("\"INDENT\"")> }  ;
NL : '\n';
WS : [ \t]+ ;
