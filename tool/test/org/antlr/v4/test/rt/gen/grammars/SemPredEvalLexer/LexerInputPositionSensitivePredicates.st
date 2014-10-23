lexer grammar <grammarName>;
WORD1 : ID1+ { <Text():writeln()> } ;
WORD2 : ID2+ { <Text():writeln()> } ;
fragment ID1 : { <Column()> \< 2 }? [a-zA-Z];
fragment ID2 : { <Column()> >= 2 }? [a-zA-Z];
WS : (' '|'\n') -> skip;
