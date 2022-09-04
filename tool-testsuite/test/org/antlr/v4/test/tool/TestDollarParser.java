/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import org.antlr.v4.test.runtime.states.ExecutedState;
import org.junit.jupiter.api.Test;

import static org.antlr.v4.test.tool.ToolTestUtils.execParser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestDollarParser {
	@Test
	public void testSimpleCall() {
		String grammar = "grammar T;\n" +
                      "a : ID  { outStream.println(new java.io.File($parser.getSourceName()).getAbsolutePath()); }\n" +
                      "  ;\n" +
                      "ID : 'a'..'z'+ ;\n";
		ExecutedState executedState = execParser("T.g4", grammar, "TParser", "TLexer", "a", "x", true);
		assertTrue(executedState.output.contains("input"));
		assertEquals("", executedState.errors);
	}
}
