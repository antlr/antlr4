/*
 * Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.java.api;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.junit.Assert;
import org.junit.Test;

public class TestVisitors {

	/**
	 * This test verifies the basic behavior of visitors, with an emphasis on
	 * {@link AbstractParseTreeVisitor#visitTerminal}.
	 */
	@Test
	public void testVisitTerminalNode() {
		String input = "A";
		VisitorBasicLexer lexer = new VisitorBasicLexer(new ANTLRInputStream(input));
		VisitorBasicParser parser = new VisitorBasicParser(new CommonTokenStream(lexer));

		VisitorBasicParser.SContext context = parser.s();
		Assert.assertEquals("(s A <EOF>)", context.toStringTree(parser));

		VisitorBasicVisitor<String> listener = new VisitorBasicBaseVisitor<String>() {
			@Override
			public String visitTerminal(TerminalNode node) {
				return node.getSymbol().toString() + "\n";
			}

			@Override
			protected String defaultResult() {
				return "";
			}

			@Override
			protected String aggregateResult(String aggregate, String nextResult) {
				return aggregate + nextResult;
			}
		};

		String result = listener.visit(context);
		String expected =
			"[@0,0:0='A',<1>,1:0]\n" +
			"[@1,1:0='<EOF>',<-1>,1:1]\n";
		Assert.assertEquals(expected, result);
	}

}
