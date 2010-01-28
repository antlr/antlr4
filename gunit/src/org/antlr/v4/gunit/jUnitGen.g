tree grammar jUnitGen;

options {
	output=template;
	ASTLabelType=CommonTree;
	tokenVocab = gUnit;
}

@header {
package org.antlr.v4.gunit;
}

gUnitDef
	:	^('gunit' ID DOC_COMMENT? (optionsSpec|header)* suites+=testsuite+)
		-> jUnitClass(className={$ID.text}, header={$header.st}, suites={$suites})
	;

optionsSpec
	:	^(OPTIONS option+)
	;

option
    :   ^('=' ID ID)
    |   ^('=' ID STRING)
    ;
 	
header : ^('@header' ACTION) -> header(action={$ACTION.text});

testsuite
	:	^(SUITE rule=ID ID DOC_COMMENT? cases+=testcase[$rule.text]+)
	|	^(SUITE rule=ID    DOC_COMMENT? cases+=testcase[$rule.text]+)
		-> testSuite(name={$rule.text}, cases={$cases})
	;

testcase[String ruleName]
	:	^(TEST_OK DOC_COMMENT? input)
	|	^(TEST_FAIL DOC_COMMENT? input)
	|	^(TEST_RETVAL DOC_COMMENT? input RETVAL)
	|	^(TEST_STDOUT DOC_COMMENT? input STRING)
	|	^(TEST_STDOUT DOC_COMMENT? input ML_STRING)
	|	^(TEST_TREE DOC_COMMENT? input TREE)
			-> parserRuleTestAST(ruleName={$ruleName},
							     input={$input.st},
							     expecting={Gen.normalizeTreeSpec($TREE.text)},
							     scriptLine={$input.start.getLine()})
	|	^(TEST_ACTION DOC_COMMENT? input ACTION)
	;

input
	:	STRING		-> string(s={Gen.escapeForJava($STRING.text)})
	|	ML_STRING	-> string(s={Gen.escapeForJava($ML_STRING.text)})
	|	FILENAME
	;