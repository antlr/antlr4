lexer grammar JavaLexer;

@members {
  protected boolean enumIsKeyword = true;
  protected boolean assertIsKeyword = true;
}

T__25 : 'package' ;
T__26 : ';' ;
T__27 : 'import' ;
T__28 : 'static' ;
T__29 : '.' ;
T__30 : '*' ;
T__31 : 'public' ;
T__32 : 'protected' ;
T__33 : 'private' ;
T__34 : 'abstract' ;
T__35 : 'final' ;
T__36 : 'strictfp' ;
T__37 : 'class' ;
T__38 : 'extends' ;
T__39 : 'implements' ;
T__40 : '<' ;
T__41 : ',' ;
T__42 : '>' ;
T__43 : '&' ;
T__44 : '{' ;
T__45 : '}' ;
T__46 : 'interface' ;
T__47 : 'void' ;
T__48 : '[' ;
T__49 : ']' ;
T__50 : 'throws' ;
T__51 : '=' ;
T__52 : 'native' ;
T__53 : 'synchronized' ;
T__54 : 'transient' ;
T__55 : 'volatile' ;
T__56 : 'boolean' ;
T__57 : 'char' ;
T__58 : 'byte' ;
T__59 : 'short' ;
T__60 : 'int' ;
T__61 : 'long' ;
T__62 : 'float' ;
T__63 : 'double' ;
T__64 : '?' ;
T__65 : 'super' ;
T__66 : '(' ;
T__67 : ')' ;
T__68 : '...' ;
T__69 : 'this' ;
T__70 : 'null' ;
T__71 : 'true' ;
T__72 : 'false' ;
T__73 : '@' ;
T__74 : 'default' ;
T__75 : ':' ;
T__76 : 'if' ;
T__77 : 'else' ;
T__78 : 'for' ;
T__79 : 'while' ;
T__80 : 'do' ;
T__81 : 'try' ;
T__82 : 'finally' ;
T__83 : 'switch' ;
T__84 : 'return' ;
T__85 : 'throw' ;
T__86 : 'break' ;
T__87 : 'continue' ;
T__88 : 'catch' ;
T__89 : 'case' ;
T__90 : '+=' ;
T__91 : '-=' ;
T__92 : '*=' ;
T__93 : '/=' ;
T__94 : '&=' ;
T__95 : '|=' ;
T__96 : '^=' ;
T__97 : '%=' ;
T__98 : '||' ;
T__99 : '&&' ;
T__100 : '|' ;
T__101 : '^' ;
T__102 : '==' ;
T__103 : '!=' ;
T__104 : 'instanceof' ;
T__105 : '+' ;
T__106 : '-' ;
T__107 : '/' ;
T__108 : '%' ;
T__109 : '++' ;
T__110 : '--' ;
T__111 : '~' ;
T__112 : '!' ;
T__113 : 'new' ;

// $ANTLR src "JavaCombined.g" 911
HexLiteral : '0' ('x'|'X') HexDigit+ IntegerTypeSuffix? ;// $ANTLR src "JavaCombined.g" 913
DecimalLiteral : ('0' | '1'..'9' '0'..'9'*) IntegerTypeSuffix? ;// $ANTLR src "JavaCombined.g" 915
OctalLiteral : '0' ('0'..'7')+ IntegerTypeSuffix? ;// $ANTLR src "JavaCombined.g" 917
fragment
HexDigit : ('0'..'9'|'a'..'f'|'A'..'F') ;// $ANTLR src "JavaCombined.g" 920
fragment
IntegerTypeSuffix : ('l'|'L') ;// $ANTLR src "JavaCombined.g" 923
FloatingPointLiteral
    :   ('0'..'9')+ '.' ('0'..'9')* Exponent? FloatTypeSuffix?
    |   '.' ('0'..'9')+ Exponent? FloatTypeSuffix?
    |   ('0'..'9')+ Exponent FloatTypeSuffix?
    |   ('0'..'9')+ FloatTypeSuffix
    |   ('0x' | '0X') (HexDigit )*
        ('.' (HexDigit)*)?
        ( 'p' | 'P' )
        ( '+' | '-' )?
        ( '0' .. '9' )+
        FloatTypeSuffix?
    ;// $ANTLR src "JavaCombined.g" 930
fragment
Exponent : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;// $ANTLR src "JavaCombined.g" 933
fragment
FloatTypeSuffix : ('f'|'F'|'d'|'D') ;// $ANTLR src "JavaCombined.g" 936
CharacterLiteral
    :   '\'' ( EscapeSequence | ~('\''|'\\') ) '\''
    ;// $ANTLR src "JavaCombined.g" 940
StringLiteral
    :  '"' ( EscapeSequence | ~('\\'|'"') )* '"'
    ;// $ANTLR src "JavaCombined.g" 944
fragment
EscapeSequence
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UnicodeEscape
    |   OctalEscape
    ;// $ANTLR src "JavaCombined.g" 951
fragment
OctalEscape
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;// $ANTLR src "JavaCombined.g" 958
fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;// $ANTLR src "JavaCombined.g" 963
ENUM:   'enum' {if (!enumIsKeyword) setType(Identifier);}
    ;// $ANTLR src "JavaCombined.g" 966
ASSERT
    :   'assert' {if (!assertIsKeyword) setType(Identifier);}
    ;// $ANTLR src "JavaCombined.g" 970
Identifier
    :   Letter (Letter|JavaIDDigit)*
    ;// $ANTLR src "JavaCombined.g" 974
/**I found this char range in JavaCC's grammar, but Letter and Digit overlap.
   Still works, but...
 */
fragment
Letter
    :  '\u0024' |
       '\u0041'..'\u005a' |
       '\u005f' |
       '\u0061'..'\u007a' |
       '\u00c0'..'\u00d6' |
       '\u00d8'..'\u00f6' |
       '\u00f8'..'\u00ff' |
       '\u0100'..'\u1fff' |
       '\u3040'..'\u318f' |
       '\u3300'..'\u337f' |
       '\u3400'..'\u3d2d' |
       '\u4e00'..'\u9fff' |
       '\uf900'..'\ufaff'
    ;// $ANTLR src "JavaCombined.g" 994
fragment
JavaIDDigit
    :  '\u0030'..'\u0039' |
       '\u0660'..'\u0669' |
       '\u06f0'..'\u06f9' |
       '\u0966'..'\u096f' |
       '\u09e6'..'\u09ef' |
       '\u0a66'..'\u0a6f' |
       '\u0ae6'..'\u0aef' |
       '\u0b66'..'\u0b6f' |
       '\u0be7'..'\u0bef' |
       '\u0c66'..'\u0c6f' |
       '\u0ce6'..'\u0cef' |
       '\u0d66'..'\u0d6f' |
       '\u0e50'..'\u0e59' |
       '\u0ed0'..'\u0ed9' |
       '\u1040'..'\u1049'
   ;// $ANTLR src "JavaCombined.g" 1013
WS  :  (' '|'\r'|'\t'|'\u000C'|'\n')+ {skip();}
    ;

LINE_COMMENT
    : '//' ~('\n'|'\r')* '\r'? '\n' {skip();}
    ;

COMMENT_START
    :   '/*' {pushMode(COMMENT_MODE); more();}
    ;

mode COMMENT_MODE;

COMMENT : '*/' {skip(); popMode();} ;

COMMENT_INSIDE : . {more();} ;
