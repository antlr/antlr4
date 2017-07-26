lexer grammar TestLexer;

import TestBaseLexer, TestBaseLexer2;

WS    :  Whitespace+              -> skip;
TEXT  :  ~[<&]+ ; // match any 16 bit char other than < and &
