lexer grammar <grammarName>;
ID : [a-z]+  ;
INDENT : [ \t]+ { <TokenStartColumnEquals("0")> }? \n" +
         { <writeln("\"INDENT\"")> }  ;"+
NL : '\n';
WS : [ \t]+ ;
