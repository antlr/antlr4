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
import org.antlr.v4.runtime.tree.xpath.XPath;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class TestTerminalNodeWithHidden {
	@Test public void testEmptyInputWithCommentNoEOFRefInGrammar() {
		Pair<Parser, ParseTree> results = parse_calc2("\t\n/* foo */\n");
		VisitorCalcParser parser = (VisitorCalcParser)results.a;
		ParseTree tree = results.b;
		assertEquals("s2", tree.toStringTree(parser));
	}

	@Test public void testEmptyInputWithCommentEOFRefInGrammar() {
		Pair<Parser, ParseTree> results = parse_calc("\t\n/* foo */\n");
		VisitorCalcParser parser = (VisitorCalcParser)results.a;
		ParseTree tree = results.b;
		assertEquals("(s <EOF>)", tree.toStringTree(parser));
		ParseTree leaf = tree.getChild(0);
		assertEquals("\t\n/* foo */\n<EOF>", leaf.getText());
	}

	@Test public void testWSBeforeFirstToken() {
		Pair<Parser, ParseTree> results = parse_calc("\t\n 1");
		VisitorCalcParser parser = (VisitorCalcParser)results.a;
		ParseTree tree = results.b;
		assertEquals("(s (expr 1) <EOF>)", tree.toStringTree(parser));
		ParseTree leaf = tree.getChild(0).getChild(0);
		assertEquals("\t\n 1", leaf.getText());
	}

	@Test public void testWSAfterLastToken() {
		Pair<Parser, ParseTree> results = parse_calc("1 \t");
		VisitorCalcParser parser = (VisitorCalcParser)results.a;
		ParseTree tree = results.b;
		assertEquals("(s (expr 1) <EOF>)", tree.toStringTree(parser));
		ParseTree leaf = tree.getChild(0).getChild(0);
		assertEquals("1 \t", leaf.getText());
	}

	@Test public void testWSBeforeAfterSingleToken() {
		Pair<Parser, ParseTree> results = parse_calc(" \t1 \t");
		VisitorCalcParser parser = (VisitorCalcParser)results.a;
		ParseTree tree = results.b;
		assertEquals("(s (expr 1) <EOF>)", tree.toStringTree(parser));
		ParseTree leaf = tree.getChild(0).getChild(0);
		assertEquals(" \t1 \t", leaf.getText());
	}

	@Test public void testMultilineWSAfterLastTokenGetsAll() {
		Pair<Parser, ParseTree> results = parse_calc("1 \t\n \n");
		VisitorCalcParser parser = (VisitorCalcParser)results.a;
		ParseTree tree = results.b;
		assertEquals("(s (expr 1) <EOF>)", tree.toStringTree(parser));
		ParseTree leaf = tree.getChild(0).getChild(0);
		assertEquals("1 \t\n \n", leaf.getText());
	}

	@Test public void testMultilineWSAfterTokenGetsOnLineOnly() {
		Pair<Parser, ParseTree> results = parse_calc("1 \t\n \n+ 2");
		VisitorCalcParser parser = (VisitorCalcParser)results.a;
		ParseTree tree = results.b;
		assertEquals("(s (expr (expr 1) + (expr 2)) <EOF>)", tree.toStringTree(parser));
		Collection<ParseTree> intsColl = XPath.findAll(tree, "//INT", parser);
		TerminalNodeWithHidden[] ints = intsColl.toArray(new TerminalNodeWithHidden[0]);
		assertEquals("1 \t\n", ints[0].getText());
		assertEquals("2", ints[1].getText());
		TerminalNodeWithHidden[] op = XPath.findAll(tree, "//'+'", parser).toArray(new TerminalNodeWithHidden[0]);
		assertEquals(" \n+ ", op[0].getText());
	}

	@Test public void testWSAroundSingleOp() {
		Pair<Parser, ParseTree> results = parse_calc("1 + 2\n");
		VisitorCalcParser parser = (VisitorCalcParser)results.a;
		ParseTree tree = results.b;
		assertEquals("(s (expr (expr 1) + (expr 2)) <EOF>)", tree.toStringTree(parser));
		Collection<ParseTree> intsColl = XPath.findAll(tree, "//INT", parser);
		TerminalNodeWithHidden[] ints = intsColl.toArray(new TerminalNodeWithHidden[0]);
		assertEquals("1 ", ints[0].getText());
		assertEquals("2\n", ints[1].getText());

		TerminalNodeWithHidden[] op = XPath.findAll(tree, "//'+'", parser).toArray(new TerminalNodeWithHidden[0]);
		assertEquals("+ ", op[0].getText());
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

	public Pair<Parser, ParseTree> parse_calc2(String input) {
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
		ParseTree tree = parser.s2();
//		System.out.println(tree.toStringTree(parser));
		return new Pair<>((Parser)parser, tree);
	}
}
