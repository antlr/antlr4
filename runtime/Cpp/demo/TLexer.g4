lexer grammar TLexer;

@lexer::header {
// Lexer header
}

@lexer::members {
bool canTestFoo() { return true; }
bool isItFoo() { return true; }
bool isItBar() { return true; }

void myFooLexerAction() { /* do something*/ };
void myBarLexerAction() { /* do something*/ };
}

@lexer::context
{
 // Lexer context.
}

@lexer::apifuncs
{
 // Lexer API functions.
}

channels { COMMENTS_CHANNEL, DIRECTIVE }

tokens {
	DUMMY	
}

Return: 'return';
Continue: 'continue';

INT: Digit+;
Digit: [0..9];

ID: LETTER (LETTER | '0'..'9')*;
fragment LETTER : [a-zA-Z\u0080-\uFFFD] ;

LessThan: '<';
GreaterThan:  '>';
Equal: '=';
And: 'and';

Colon: ':';
Semicolon: ';';
Plus: '+';
Minus: '-';
Star: '*';
OpenPar: '(';
ClosePar: ')';
OpenCurly: '{' -> pushMode(Mode1);
CloseCurly: '}' -> popMode;
QuestionMark: '?';
Comma: ',';
Dollar: '$' -> more, mode(Mode1), type(DUMMY);
		   
Foo: {canTestFoo()}? 'foo' {isItFoo()}? { myFooLexerAction(); };
Bar: 'bar' {isItBar()}? { myBarLexerAction(); };
Any: Foo Dot Bar? DotDot Baz;

Comment : '#' ~[\r\n]* '\r'? '\n' -> skip ;
WS: [ \t\r\n]+ -> channel(99);

fragment Baz: 'Baz';

mode Mode1;
Dot: '.';

mode Mode2;
DotDot: '..';
