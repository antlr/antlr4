/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.tool;

import org.antlr.v4.tool.ErrorType;
import org.junit.Before;
import org.junit.Test;

/** Test errors with the set stuff in lexer and parser */
public class TestErrorSets extends BaseJavaToolTest {
	protected boolean debug = false;

	@Before
	@Override
	public void testSetUp() throws Exception {
		super.testSetUp();
	}

	/** Public default constructor used by TestRig */
	public TestErrorSets() {
	}

	@Test public void testNotCharSetWithRuleRef() throws Exception {
		// might be a useful feature to add someday
		String[] pair = new String[] {
			"grammar T;\n" +
			"a : A {System.out.println($A.text);} ;\n" +
			"A : ~('a'|B) ;\n" +
			"B : 'b' ;\n",
			"error(" + ErrorType.UNSUPPORTED_REFERENCE_IN_LEXER_SET.code + "): T.g4:3:10: rule reference B is not currently supported in a set\n"
		};
		super.testErrors(pair, true);
	}

	@Test public void testNotCharSetWithString() throws Exception {
		// might be a useful feature to add someday
		String[] pair = new String[] {
			"grammar T;\n" +
			"a : A {System.out.println($A.text);} ;\n" +
			"A : ~('a'|'aa') ;\n" +
			"B : 'b' ;\n",
			"error(" + ErrorType.INVALID_LITERAL_IN_LEXER_SET.code + "): T.g4:3:10: multi-character literals are not allowed in lexer sets: 'aa'\n"
		};
		super.testErrors(pair, true);
	}


}
