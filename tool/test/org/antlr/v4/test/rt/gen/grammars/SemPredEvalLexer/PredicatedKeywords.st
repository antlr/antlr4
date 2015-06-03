lexer grammar <grammarName>;
ENUM : [a-z]+ { <TextEquals("enum")> }? { <writeln("\"enum!\"")> } ;
ID   : [a-z]+ { <PlusText("ID "):writeln()> } ;
WS   : [ \n] -> skip ;
