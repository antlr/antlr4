/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import org.antlr.v4.test.runtime.states.ExecutedState;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestDollarParser extends BaseJavaToolTest {
	@Before
	@Override
	public void testSetUp() throws Exception {
		super.testSetUp();
	}

	@Test
	public void testSimpleCall() {
		String grammar = "grammar T;\n" +
	                  "a : ID  { System.out.println( new java.io.File($parser.getSourceName()).getAbsolutePath() ); }\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n";
		ExecutedState executedState = execParser("T.g4", grammar, "TParser", "TLexer", "a", "x", true);
		assertTrue(executedState.output.contains(this.getClass().getSimpleName()));
		assertEquals("", executedState.errors);
	}
}
