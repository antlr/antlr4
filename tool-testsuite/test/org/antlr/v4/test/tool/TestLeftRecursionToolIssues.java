/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import org.antlr.v4.tool.ErrorType;
import org.junit.jupiter.api.Test;

import static org.antlr.v4.test.tool.ToolTestUtils.testErrors;

/** */
public class TestLeftRecursionToolIssues {
	protected boolean debug = false;

	@Test public void testCheckForNonLeftRecursiveRule() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s @after {System.out.println($ctx.toStringTree(this));} : a ;\n" +
			"a : a ID\n" +
			"  ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') -> skip ;\n";
		String expected =
			"error(" + ErrorType.NO_NON_LR_ALTS.code + "): T.g4:3:0: left recursive rule a must contain an alternative which is not left recursive\n";
		testErrors(new String[] { grammar, expected }, false);
	}

	@Test public void testCheckForLeftRecursiveEmptyFollow() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s @after {System.out.println($ctx.toStringTree(this));} : a ;\n" +
			"a : a ID?\n" +
			"  | ID\n" +
			"  ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n') -> skip ;\n";
		String expected =
			"error(" + ErrorType.EPSILON_LR_FOLLOW.code + "): T.g4:3:0: left recursive rule a contains a left recursive alternative which can be followed by the empty string\n";
		testErrors(new String[] { grammar, expected }, false);
	}

	/** Reproduces https://github.com/antlr/antlr4/issues/855 */
	@Test public void testLeftRecursiveRuleRefWithArg() throws Exception {
		String grammar =
			"grammar T;\n" +
			"statement\n" +
			"locals[Scope scope]\n" +
			"    : expressionA[$scope] ';'\n" +
			"    ;\n" +
			"expressionA[Scope scope]\n" +
			"    : atom[$scope]\n" +
			"    | expressionA[$scope] '[' expressionA[$scope] ']'\n" +
			"    ;\n" +
			"atom[Scope scope]\n" +
			"    : 'dummy'\n" +
			"    ;\n";
		String expected =
			"error(" + ErrorType.NONCONFORMING_LR_RULE.code + "): T.g4:6:0: rule expressionA is left recursive but doesn't conform to a pattern ANTLR can handle\n";
		testErrors(new String[]{grammar, expected}, false);
	}

	/** Reproduces https://github.com/antlr/antlr4/issues/855 */
	@Test public void testLeftRecursiveRuleRefWithArg2() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a[int i] : 'x'\n" +
			"  | a[3] 'y'\n" +
			"  ;";
		String expected =
			"error(" + ErrorType.NONCONFORMING_LR_RULE.code + "): T.g4:2:0: rule a is left recursive but doesn't conform to a pattern ANTLR can handle\n";
		testErrors(new String[]{grammar, expected}, false);
	}

	/** Reproduces https://github.com/antlr/antlr4/issues/855 */
	@Test public void testLeftRecursiveRuleRefWithArg3() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : 'x'\n" +
			"  | a[3] 'y'\n" +
			"  ;";
		String expected =
			"error(" + ErrorType.NONCONFORMING_LR_RULE.code + "): T.g4:2:0: rule a is left recursive but doesn't conform to a pattern ANTLR can handle\n";
		testErrors(new String[]{grammar, expected}, false);
	}

	/** Reproduces https://github.com/antlr/antlr4/issues/822 */
	@Test public void testIsolatedLeftRecursiveRuleRef() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : a | b ;\n" +
			"b : 'B' ;\n";
		String expected =
			"error(" + ErrorType.NONCONFORMING_LR_RULE.code + "): T.g4:2:0: rule a is left recursive but doesn't conform to a pattern ANTLR can handle\n";
		testErrors(new String[]{grammar, expected}, false);
	}

	/** Reproduces https://github.com/antlr/antlr4/issues/773 */
	@Test public void testArgOnPrimaryRuleInLeftRecursiveRule() throws Exception {
		String grammar =
			"grammar T;\n" +
			"val: dval[1]\n" +
			"   | val '*' val\n" +
			"   ;\n" +
			"dval[int  x]: '.';\n";
		String expected = ""; // dval[1] should not be error
		testErrors(new String[]{grammar, expected}, false);
	}
}
