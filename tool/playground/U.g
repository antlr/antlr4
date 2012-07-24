lexer grammar U;

X : 'a' -> skip ;
Y : 'z' -> skip, more ;
// (RULE C (BLOCK (LEXER_ALT_ACTION (ALT 'x') (LEXER_ACTION_CALL mode ISLAND))))
C:   'x' -> mode(ISLAND) ;
// (RULE A (BLOCK (LEXER_ALT_ACTION (ALT 'a') (LEXER_ACTION_CALL mode ISLAND) skip)))
A:   'b' -> mode(ISLAND), skip ;

mode INSIDE;

B : '<' ;
