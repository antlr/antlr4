lexer grammar S;
fragment
UNICODE_CLASS_Zs    : '\u0020' | '\u00A0' | '\u1680' | '\u180E'
                    | '\u2000'..'\u200A'
                    | '\u202F' | '\u205F' | '\u3000'
                    ;
