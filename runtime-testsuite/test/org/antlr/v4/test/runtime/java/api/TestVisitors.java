/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.java.api;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
		assertEquals("(s A <EOF>)", context.toStringTree(parser));

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
		assertEquals(expected, result);
	}

	/**
	 * This test verifies the basic behavior of visitors, with an emphasis on
	 * {@link AbstractParseTreeVisitor#visitErrorNode}.
	 */
	@Test
	public void testVisitErrorNode() {
		String input = "";
		VisitorBasicLexer lexer = new VisitorBasicLexer(new ANTLRInputStream(input));
		VisitorBasicParser parser = new VisitorBasicParser(new CommonTokenStream(lexer));

		final List<String> errors = new ArrayList<>();
		parser.removeErrorListeners();
		parser.addErrorListener(new BaseErrorListener() {
			@Override
			public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
				errors.add("line " + line + ":" + charPositionInLine + " " + msg);
			}
		});

		VisitorBasicParser.SContext context = parser.s();
		assertEquals("(s <missing 'A'> <EOF>)", context.toStringTree(parser));
		assertEquals(1, errors.size());
		assertEquals("line 1:0 missing 'A' at '<EOF>'", errors.get(0));

		VisitorBasicVisitor<String> listener = new VisitorBasicBaseVisitor<String>() {
			@Override
			public String visitErrorNode(ErrorNode node) {
				return "Error encountered: " + node.getSymbol();
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
		String expected = "Error encountered: [@-1,-1:-1='<missing 'A'>',<1>,1:0]";
		assertEquals(expected, result);
	}

	/**
	 * This test verifies that {@link AbstractParseTreeVisitor#visitChildren} does not call
	 * {@link org.antlr.v4.runtime.tree.ParseTreeVisitor#visit} after
	 * {@link org.antlr.v4.runtime.tree.AbstractParseTreeVisitor#shouldVisitNextChild} returns
	 * {@code false}.
	 */
	@Test
	public void testShouldNotVisitEOF() {
		String input = "A";
		VisitorBasicLexer lexer = new VisitorBasicLexer(new ANTLRInputStream(input));
		VisitorBasicParser parser = new VisitorBasicParser(new CommonTokenStream(lexer));

		VisitorBasicParser.SContext context = parser.s();
		assertEquals("(s A <EOF>)", context.toStringTree(parser));

		VisitorBasicVisitor<String> listener = new VisitorBasicBaseVisitor<String>() {
			@Override
			public String visitTerminal(TerminalNode node) {
				return node.getSymbol().toString() + "\n";
			}

			@Override
			protected boolean shouldVisitNextChild(RuleNode node, String currentResult) {
				return currentResult == null || currentResult.isEmpty();
			}
		};

		String result = listener.visit(context);
		String expected = "[@0,0:0='A',<1>,1:0]\n";
		assertEquals(expected, result);
	}

	/**
	 * This test verifies that {@link AbstractParseTreeVisitor#shouldVisitNextChild} is called before visiting the first
	 * child. It also verifies that {@link AbstractParseTreeVisitor#defaultResult} provides the default return value for
	 * visiting a tree.
	 */
	@Test
	public void testShouldNotVisitTerminal() {
		String input = "A";
		VisitorBasicLexer lexer = new VisitorBasicLexer(new ANTLRInputStream(input));
		VisitorBasicParser parser = new VisitorBasicParser(new CommonTokenStream(lexer));

		VisitorBasicParser.SContext context = parser.s();
		assertEquals("(s A <EOF>)", context.toStringTree(parser));

		VisitorBasicVisitor<String> listener = new VisitorBasicBaseVisitor<String>() {
			@Override
			public String visitTerminal(TerminalNode node) {
				throw new RuntimeException("Should not be reachable");
			}

			@Override
			protected String defaultResult() {
				return "default result";
			}

			@Override
			protected boolean shouldVisitNextChild(RuleNode node, String currentResult) {
				return false;
			}
		};

		String result = listener.visit(context);
		String expected = "default result";
		assertEquals(expected, result);
	}

	/**
	 * This test verifies that the visitor correctly dispatches calls for labeled outer alternatives.
	 */
	@Test
	public void testCalculatorVisitor() {
		String input = "2 + 8 / 2";
		VisitorCalcLexer lexer = new VisitorCalcLexer(new ANTLRInputStream(input));
		VisitorCalcParser parser = new VisitorCalcParser(new CommonTokenStream(lexer));

		VisitorCalcParser.SContext context = parser.s();
		assertEquals("(s (expr (expr 2) + (expr (expr 8) / (expr 2))) <EOF>)", context.toStringTree(parser));

		VisitorCalcVisitor<Integer> listener = new VisitorCalcBaseVisitor<Integer>() {
			@Override
			public Integer visitS(VisitorCalcParser.SContext ctx) {
				return visit(ctx.expr());
			}

			@Override
			public Integer visitNumber(VisitorCalcParser.NumberContext ctx) {
				return Integer.valueOf(ctx.INT().getText());
			}

			@Override
			public Integer visitMultiply(VisitorCalcParser.MultiplyContext ctx) {
				Integer left = visit(ctx.expr(0));
				Integer right = visit(ctx.expr(1));
				if (ctx.MUL() != null) {
					return left * right;
				}
				else {
					return left / right;
				}
			}

			@Override
			public Integer visitAdd(VisitorCalcParser.AddContext ctx) {
				Integer left = visit(ctx.expr(0));
				Integer right = visit(ctx.expr(1));
				if (ctx.ADD() != null) {
					return left + right;
				}
				else {
					return left - right;
				}
			}

			@Override
			protected Integer defaultResult() {
				throw new RuntimeException("Should not be reachable");
			}

			@Override
			protected Integer aggregateResult(Integer aggregate, Integer nextResult) {
				throw new RuntimeException("Should not be reachable");
			}
		};

		int result = listener.visit(context);
		int expected = 6;
		assertEquals(expected, result);
	}

}
