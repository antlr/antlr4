lexer grammar TestLexer;

tokens { ID, DELIM, COMMENT, OUT_PARAMSTART, OUT_PARAMEND, LINE_TERMINATE }

/*
 * Lexer Rules
 */

fragment ID_TOKEN: IDENTIFIER;

fragment IDENTIFIER: IDENTIFIER_START IDENTIFIER_PART*;

fragment IDENTIFIER_START: 
    [a-zA-Zа-яА-Я_]
    | UNICODE_ESCAPE
    ;

fragment IDENTIFIER_PART:
    IDENTIFIER_START
    | UNICODE_ESCAPE
    | [\.\+0-9]
    ;

fragment UNICODE_ESCAPE:
    '\\u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    | '\\U' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    ;

fragment HEX_DIGIT:
    '0'..'9'
    | 'a'..'f'
    | 'A'..'F'
    ;

fragment WS: WHITESPACE+;

fragment NEW_LINE:
    [\r\n] | '\u0085' | '\u2028' | '\u2029';

fragment WHITESPACE: NEW_LINE
    | [ \t\f] 
    | '\u000B'
    | '\u00A0' 
    | '\u1680' 
    | '\u2000'..'\u200A' 
    | '\u202F' 
    | '\u205F' 
    | '\u3000'
    ;

fragment PARA_ST: '(';
fragment PARA_CL: ')';
fragment EXT_DELIM: ':';
fragment LINE_TERM: ';';
fragment COMMENT_BLOCK: '@*' .*? '*@';

CALL_COMMENT:
    COMMENT_BLOCK -> skip;

CALL_OUT_PARAMEND: 
    PARA_CL -> type(OUT_PARAMEND), popMode;

CALL_OUT_PARAMSTART: 
    PARA_ST -> type(OUT_PARAMSTART), pushMode(DEFAULT_MODE);

CALL_OUT_DELIM: 
    EXT_DELIM -> type(DELIM);

CALL_OUT_ID: ID_TOKEN -> type(ID);

CALL_LINE_TERMINATE: 
    LINE_TERM WS* -> type(LINE_TERMINATE), popMode, popMode;

CALL_OUT_WS: WS+ -> skip;