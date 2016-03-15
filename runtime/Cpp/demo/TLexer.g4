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

ID: [a-z]+ ;
LessThan: '<' -> pushMode(Mode1);
GreaterThan:  '>' -> popMode;
Foo: {canTestFoo()}? 'foo' {isItFoo()}? { myFooLexerAction(); };
Bar: 'bar' {isItBar()}? { myBarLexerAction(); };
Any: Foo Dot Bar? DotDot Baz;

fragment Baz: 'Baz';

mode Mode1;
Dot: '.';

mode Mode2;
DotDot: '..';
