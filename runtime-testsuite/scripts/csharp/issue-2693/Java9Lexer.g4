lexer grammar Java9Lexer;

options {
    superClass=LexerBase;
}

ABSTRACT : 'abstract' ;
ASSERT : 'assert' ;
BOOLEAN : 'boolean' ;
BREAK : 'break' ;
BYTE : 'byte' ;
CASE : 'case' ;
CATCH : 'catch' ;
CHAR : 'char' ;
CLASS : 'class' ;
CONST : 'const' ;
CONTINUE : 'continue' ;
DEFAULT : 'default' ;
DO : 'do' ;
DOUBLE : 'double' ;
ELSE : 'else' ;
ENUM : 'enum' ;
EXPORTS : 'exports' ;
EXTENDS : 'extends' ;
FINAL : 'final' ;
FINALLY : 'finally' ;
FLOAT : 'float' ;
FOR : 'for' ;
IF : 'if' ;
GOTO : 'goto' ;
IMPLEMENTS : 'implements' ;
IMPORT : 'import' ;
INSTANCEOF : 'instanceof' ;
INT : 'int' ;
INTERFACE : 'interface' ;
LONG : 'long' ;
MODULE : 'module' ;
NATIVE : 'native' ;
NEW : 'new' ;
OPEN : 'open' ;
OPENS : 'opens' ;
PACKAGE : 'package' ;
PRIVATE : 'private' ;
PROTECTED : 'protected' ;
PROVIDES : 'provides' ;
PUBLIC : 'public' ;
REQUIRES : 'requires' ;
RETURN : 'return' ;
SHORT : 'short' ;
STATIC : 'static' ;
STRICTFP : 'strictfp' ;
SUPER : 'super' ;
SWITCH : 'switch' ;
SYNCHRONIZED : 'synchronized' ;
THIS : 'this' ;
THROW : 'throw' ;
THROWS : 'throws' ;
TO : 'to' ;
TRANSIENT : 'transient' ;
TRANSITIVE : 'transitive' ;
TRY : 'try' ;
USES : 'uses' ;
VOID : 'void' ;
VOLATILE : 'volatile' ;
WHILE : 'while' ;
WITH : 'with' ;
UNDER_SCORE : '_' ;//Introduced in Java 9
IntegerLiteral : DecimalIntegerLiteral | HexIntegerLiteral | OctalIntegerLiteral | BinaryIntegerLiteral ;
fragment DecimalIntegerLiteral : DecimalNumeral IntegerTypeSuffix? ;
fragment HexIntegerLiteral : HexNumeral IntegerTypeSuffix? ;
fragment OctalIntegerLiteral : OctalNumeral IntegerTypeSuffix? ;
fragment BinaryIntegerLiteral : BinaryNumeral IntegerTypeSuffix? ;
fragment IntegerTypeSuffix : [lL] ;
fragment DecimalNumeral : '0' | NonZeroDigit (Digits?|Underscores Digits) ;
fragment Digits : Digit (DigitsAndUnderscores? Digit)? ;
fragment Digit : '0'|NonZeroDigit ;
fragment NonZeroDigit : [1-9] ;
fragment DigitsAndUnderscores : DigitOrUnderscore+ ;
fragment DigitOrUnderscore : Digit|'_' ;
fragment Underscores : '_'+ ;
fragment HexNumeral : '0' [xX] HexDigits ;
fragment HexDigits : HexDigit (HexDigitsAndUnderscores? HexDigit)? ;
fragment HexDigit : [0-9a-fA-F] ;
fragment HexDigitsAndUnderscores : HexDigitOrUnderscore+ ;
fragment HexDigitOrUnderscore : HexDigit|'_' ;
fragment OctalNumeral : '0' Underscores? OctalDigits ;
fragment OctalDigits : OctalDigit (OctalDigitsAndUnderscores? OctalDigit)? ;
fragment OctalDigit : [0-7] ;
fragment OctalDigitsAndUnderscores : OctalDigitOrUnderscore+ ;
fragment OctalDigitOrUnderscore : OctalDigit|'_' ;
fragment BinaryNumeral : '0' [bB] BinaryDigits ;
fragment BinaryDigits : BinaryDigit (BinaryDigitsAndUnderscores? BinaryDigit)? ;
fragment BinaryDigit : [01] ;
fragment BinaryDigitsAndUnderscores : BinaryDigitOrUnderscore+ ;
fragment BinaryDigitOrUnderscore : BinaryDigit|'_' ;
FloatingPointLiteral : DecimalFloatingPointLiteral | HexadecimalFloatingPointLiteral ;
fragment DecimalFloatingPointLiteral : Digits '.' Digits? ExponentPart? FloatTypeSuffix? | '.' Digits ExponentPart? FloatTypeSuffix? | Digits ExponentPart FloatTypeSuffix? | Digits FloatTypeSuffix ;
fragment ExponentPart : ExponentIndicator SignedInteger ;
fragment ExponentIndicator : [eE] ;
fragment SignedInteger : Sign? Digits ;
fragment Sign : [+-] ;
fragment FloatTypeSuffix : [fFdD] ;
fragment HexadecimalFloatingPointLiteral : HexSignificand BinaryExponent FloatTypeSuffix? ;
fragment HexSignificand : HexNumeral '.'? | '0' [xX] HexDigits? '.' HexDigits ;
fragment BinaryExponent : BinaryExponentIndicator SignedInteger ;
fragment BinaryExponentIndicator : [pP] ;
BooleanLiteral : 'true' | 'false' ;
CharacterLiteral : '\'' SingleCharacter '\'' | '\'' EscapeSequence '\'' ;
fragment SingleCharacter : ~['\\\r\n] ;
StringLiteral : '"' StringCharacters? '"' ;
fragment StringCharacters : StringCharacter+ ;
fragment StringCharacter : ~["\\\r\n]|EscapeSequence ;
fragment EscapeSequence : '\\' [btnfr"'\\]|OctalEscape|UnicodeEscape ;
fragment OctalEscape : '\\' OctalDigit | '\\' OctalDigit OctalDigit | '\\' ZeroToThree OctalDigit OctalDigit ;
fragment ZeroToThree : [0-3] ;
fragment UnicodeEscape : '\\' 'u'+ HexDigit HexDigit HexDigit HexDigit ;
NullLiteral : 'null' ;
LPAREN : '(' ;
RPAREN : ')' ;
LBRACE : '{' ;
RBRACE : '}' ;
LBRACK : '[' ;
RBRACK : ']' ;
SEMI : ';' ;
COMMA : ',' ;
DOT : '.' ;
ELLIPSIS : '...' ;
AT : '@' ;
COLONCOLON : '::' ;
ASSIGN : '=' ;
GT : '>' ;
LT : '<' ;
BANG : '!' ;
TILDE : '~' ;
QUESTION : '?' ;
COLON : ':' ;
ARROW : '->' ;
EQUAL : '==' ;
LE : '<=' ;
GE : '>=' ;
NOTEQUAL : '!=' ;
AND : '&&' ;
OR : '||' ;
INC : '++' ;
DEC : '--' ;
ADD : '+' ;
SUB : '-' ;
MUL : '*' ;
DIV : '/' ;
BITAND : '&' ;
BITOR : '|' ;
CARET : '^' ;
MOD : '%' ;
ADD_ASSIGN : '+=' ;
SUB_ASSIGN : '-=' ;
MUL_ASSIGN : '*=' ;
DIV_ASSIGN : '/=' ;
AND_ASSIGN : '&=' ;
OR_ASSIGN : '|=' ;
XOR_ASSIGN : '^=' ;
MOD_ASSIGN : '%=' ;
LSHIFT_ASSIGN : '<<=' ;
RSHIFT_ASSIGN : '>>=' ;
URSHIFT_ASSIGN : '>>>=' ;
Identifier : JavaLetter JavaLetterOrDigit* ;

fragment
JavaLetter
    : [a-zA-Z$_] // these are the "java letters" below 0x7F
    |   // covers all characters above 0x7F which are not a surrogate
    ~[\u0000-\u007F\uD800-\uDBFF] { testIsJavaIdentifierStart1() }?
    |   // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
    [\uD800-\uDBFF] [\uDC00-\uDFFF] { testIsJavaIdentifierStart2() }?
    ;
fragment
JavaLetterOrDigit
    : [a-zA-Z0-9$_] // these are the "java letters or digits" below 0x7F
    |   // covers all characters above 0x7F which are not a surrogate
    ~[\u0000-\u007F\uD800-\uDBFF] { testIsJavaIdentifierStart3() }?
    |   // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
    [\uD800-\uDBFF] [\uDC00-\uDFFF] { testIsJavaIdentifierStart4() }?
    ;
WS : [ \t\r\n\u000C]+ -> channel(HIDDEN) ;
COMMENT : '/*' .*? '*/' -> channel(HIDDEN) ;
LINE_COMMENT : '//' ~[\r\n]* -> channel(HIDDEN) ;