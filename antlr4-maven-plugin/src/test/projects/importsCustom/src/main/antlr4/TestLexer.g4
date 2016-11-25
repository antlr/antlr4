lexer grammar TestLexer;

import TestBaseLexer;

WS    :  Whitespace+              -> skip;
TEXT  :  ~[<&]+ ; // match any 16 bit char other than < and &