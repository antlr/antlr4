tree grammar ASTVerifier;

options {
	ASTLabelType=CommonTree;
	tokenVocab = gUnit;
}

@header {
package org.antlr.v4.gunit;
}

gUnitDef
	:	^('gunit' ID DOC_COMMENT? (optionsSpec|header)* testsuite+)
	;

optionsSpec
	:	^(OPTIONS option+)
	;

option
    :   ^('=' ID ID)
    |   ^('=' ID STRING)
    ;
 	
header : ^('@header' ACTION);

testsuite
	:	^(SUITE ID ID DOC_COMMENT? testcase+)
	|	^(SUITE ID DOC_COMMENT? testcase+)
	;

testcase
	:	^(TEST_OK DOC_COMMENT? input)
	|	^(TEST_FAIL DOC_COMMENT? input)
	|	^(TEST_RETVAL DOC_COMMENT? input RETVAL)
	|	^(TEST_STDOUT DOC_COMMENT? input STRING)
	|	^(TEST_STDOUT DOC_COMMENT? input ML_STRING)
	|	^(TEST_TREE DOC_COMMENT? input TREE)
	|	^(TEST_ACTION DOC_COMMENT? input ACTION)
	;

input
	:	STRING
	|	ML_STRING
	|	FILENAME
	;