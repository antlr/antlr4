package org.antlr.v4.test.runtime.java.api;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeWithHidden;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestTerminalNodeWithHidden {
	@Test public void testWSAfterSingleValue() {
//		parse_calc("1 + // comment\n /* cmt */ 3");
		Pair<Parser, ParseTree> results = parse_calc("1 \t");
		VisitorCalcParser parser = (VisitorCalcParser)results.a;
		ParseTree tree = results.b;
		assertEquals("(s (expr 1) <EOF>)", tree.toStringTree(parser));
		ParseTree leaf = tree.getChild(0).getChild(0);
		assertEquals("1 \t", leaf.getText());
	}

	@Test public void testMultilineWSAfterSingleValue() {
		Pair<Parser, ParseTree> results = parse_calc("1 \t\n \n");
		VisitorCalcParser parser = (VisitorCalcParser)results.a;
		ParseTree tree = results.b;
		assertEquals("(s (expr 1) <EOF>)", tree.toStringTree(parser));
		ParseTree leaf = tree.getChild(0).getChild(0);
		assertEquals("1 \t\n \n", leaf.getText());
	}

	// SUPPORT

	public Pair<Parser, ParseTree> parse_calc(String input) {
		final VisitorCalcLexer lexer = new VisitorCalcLexer(new ANTLRInputStream(input));
		final CommonTokenStream tokens = new CommonTokenStream(lexer);
		final VisitorCalcParser parser = new VisitorCalcParser(tokens) {
			@Override
			public TerminalNode createTerminalNode(ParserRuleContext parent, Token t) {
				TerminalNodeWithHidden node = new TerminalNodeWithHidden(tokens, -1, t);
				node.parent = parent;
				return node;
			}
		};
		ParseTree tree = parser.s();
//		System.out.println(tree.toStringTree(parser));
		return new Pair<>((Parser)parser, tree);
	}

}
