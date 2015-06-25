/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.test.tool;

import org.antlr.v4.test.runtime.java.BaseTest;
import org.antlr.v4.tool.ErrorType;
import org.junit.Test;

/** */
public class TestLeftRecursionToolIssues extends BaseTest {
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
