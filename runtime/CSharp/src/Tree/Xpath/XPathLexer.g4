lexer grammar XPathLexer;

tokens { TokenRef, RuleRef }

Anywhere : '//' ;
Root     : '/' ;
Wildcard : '*' ;
Bang     : '!' ;

ID          :   NameStartChar NameChar* { Type = Char.IsUpper(Text[0]) ? TokenRef : RuleRef; }
            ;

fragment
NameChar    :   NameStartChar
            |   '0'..'9'
            |   '_'
            |   '\u00B7'
            |   '\u0300'..'\u036F'
            |   '\u203F'..'\u2040'
            ;

fragment
NameStartChar
            :   'A'..'Z'
            |   'a'..'z'
            |   '\u00C0'..'\u00D6'
            |   '\u00D8'..'\u00F6'
            |   '\u00F8'..'\u02FF'
            |   '\u0370'..'\u037D'
            |   '\u037F'..'\u1FFF'
            |   '\u200C'..'\u200D'
            |   '\u2070'..'\u218F'
            |   '\u2C00'..'\u2FEF'
            |   '\u3001'..'\uD7FF'
            |   '\uF900'..'\uFDCF'
            |   '\uFDF0'..'\uFFFD'
            ;

String : '\'' .*? '\'' ;
