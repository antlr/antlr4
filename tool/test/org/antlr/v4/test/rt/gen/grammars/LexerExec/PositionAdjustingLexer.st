lexer grammar PositionAdjustingLexer;

@members {
<PositionAdjustingLexer()>
}

ASSIGN : '=' ;
PLUS_ASSIGN : '+=' ;
LCURLY:	'{';

// 'tokens' followed by '{'
TOKENS : 'tokens' IGNORED '{';

// IDENTIFIER followed by '+=' or '='
LABEL
	:	IDENTIFIER IGNORED '+'? '='
	;

IDENTIFIER
	:	[a-zA-Z_] [a-zA-Z0-9_]*
	;

fragment
IGNORED
	:	[ \t\r\n]*
	;

NEWLINE
	:	[\r\n]+ -> skip
	;

WS
	:	[ \t]+ -> skip
	;
