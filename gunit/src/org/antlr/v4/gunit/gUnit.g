grammar gUnit;
options {
	output=AST;
	ASTLabelType=CommonTree;
}

tokens { SUITE; TEST_OK; TEST_FAIL; TEST_RETVAL; TEST_STDOUT; TEST_TREE; TEST_ACTION; }

@header {
package org.antlr.v4.gunit;
}
@lexer::header {
package org.antlr.v4.gunit;
}

gUnitDef
	:	DOC_COMMENT? 'gunit' ID ';' (optionsSpec|header)* testsuite+
	    -> ^('gunit' ID DOC_COMMENT? optionsSpec? header? testsuite+)
	;

optionsSpec
	:	OPTIONS (option ';')+ '}' -> ^(OPTIONS option+)
	;

option
    :   ID '=' optionValue -> ^('=' ID optionValue)
 	;
 	
optionValue
    :   ID 
    |   STRING
    ;
    
header : '@header' ACTION -> ^('@header' ACTION);

testsuite
	:	DOC_COMMENT? treeRule=ID 'walks' parserRule=ID ':' testcase+
		-> ^(SUITE $treeRule $parserRule DOC_COMMENT? testcase+)
	|	DOC_COMMENT? ID ':' testcase+ -> ^(SUITE ID DOC_COMMENT? testcase+)
	;

testcase
	:	DOC_COMMENT? input 'OK'				-> ^(TEST_OK DOC_COMMENT? input)
	|	DOC_COMMENT? input 'FAIL' 			-> ^(TEST_FAIL DOC_COMMENT? input)
	|	DOC_COMMENT? input 'returns' RETVAL -> ^(TEST_RETVAL DOC_COMMENT? input RETVAL)
	|	DOC_COMMENT? input '->' STRING		-> ^(TEST_STDOUT DOC_COMMENT? input STRING)
	|	DOC_COMMENT? input '->' ML_STRING	-> ^(TEST_STDOUT DOC_COMMENT? input ML_STRING)
	|	DOC_COMMENT? input '->' TREE		-> ^(TEST_TREE DOC_COMMENT? input TREE)
	|	DOC_COMMENT? input '->' ACTION		-> ^(TEST_ACTION DOC_COMMENT? input ACTION)
	;

input
	:	STRING
	|	ML_STRING
	|	FILENAME
	;

ACTION
	:	'{' ('\\}'|'\\' ~'}'|~('\\'|'}'))* '}' {setText(getText().substring(1, getText().length()-1));}
    ;

RETVAL
	:	NESTED_RETVAL {setText(getText().substring(1, getText().length()-1));}
	;

fragment
NESTED_RETVAL :
	'['
	(	options {greedy=false;}
	:	NESTED_RETVAL
	|	.
	)*
	']'
	;

TREE : NESTED_AST (' '? NESTED_AST)*;

fragment
NESTED_AST
	:	'('
		(	NESTED_AST
		|   STRING_
		|	~('('|')'|'"')
		)*
		')'
	;

OPTIONS	: 'options' WS* '{' ;

ID : ID_ ('.' ID_)* ;

fragment
ID_ :	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
    ;

WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {$channel=HIDDEN;}
    ;

SL_COMMENT
 	:	'//' ~('\r'|'\n')* '\r'? '\n' {$channel=HIDDEN;}
	;

DOC_COMMENT
	:	'/**' (options {greedy=false;}:.)* '*/'
	;

ML_COMMENT
	:	'/*' ~'*' (options {greedy=false;}:.)* '*/' {$channel=HIDDEN;}
	;

STRING : STRING_ {setText(getText().substring(1, getText().length()-1));} ;

fragment
STRING_
	:	'"' ('\\"'|'\\' ~'"'|~('\\'|'"'))+ '"'		
	;

ML_STRING
	:	'<<' .* '>>' {setText(getText().substring(2, getText().length()-2));}
	;

FILENAME
	:	'/' ID ('/' ID)*
	|	ID ('/' ID)+
	;
	
/*
fragment
ESC	:	'\\'
		(	'n'
		|	'r'
		|	't'
		|	'b'
		|	'f'
		|	'"'
		|	'\''
		|	'\\'
		|	'>'
		|	'u' XDIGIT XDIGIT XDIGIT XDIGIT
		|	. // unknown, leave as it is
		)
	;
*/

fragment
XDIGIT :
		'0' .. '9'
	|	'a' .. 'f'
	|	'A' .. 'F'
	;

