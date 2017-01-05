lexer grammar TestBaseLexer;

tokens  { Name }

// Default "mode": Everything OUTSIDE of a tag
Comment     :  '<!--' .*? '-->' ;
CDSect      :  '<![CDATA[' .*? ']]>' ;

fragment
Whitespace  :  ' ' | '\n' | '\t' | '\r' ;

fragment
Hexdigit    :  [a-fA-F0-9] ;

fragment
Digit       :  [0-9] ;
