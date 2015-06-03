lexer grammar <grammarName>;
START_BLOCK: '-.-.-';
ID : (LETTER SEPARATOR) (LETTER SEPARATOR)+;
fragment LETTER: L_A|L_K;
fragment L_A: '.-';
fragment L_K: '-.-';
SEPARATOR: '!';
