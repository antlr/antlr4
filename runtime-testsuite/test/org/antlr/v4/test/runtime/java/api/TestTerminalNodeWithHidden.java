package org.antlr.v4.test.runtime.java.api;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ErrorNodeWithHidden;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeWithHidden;
import org.antlr.v4.runtime.tree.Trees;
import org.antlr.v4.runtime.tree.xpath.XPath;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestTerminalNodeWithHidden {
	public static class MyVisitorCalcParser extends VisitorCalcParser {
		private final CommonTokenStream tokens;

		public MyVisitorCalcParser(CommonTokenStream tokens) {
			super(tokens);
			this.tokens = tokens;
		}

		@Override
		public TerminalNode createTerminalNode(ParserRuleContext parent, Token t) {
			TerminalNodeWithHidden node = new TerminalNodeWithHidden(tokens, -1, t);
			node.parent = parent;
			return node;
		}

		@Override
		public ErrorNode createErrorNode(ParserRuleContext parent, Token t) {
			ErrorNodeWithHidden node = new ErrorNodeWithHidden(tokens, -1, t);
			node.parent = parent;
			return node;
		}
	}

	@Test public void testEmptyInputWithCommentNoEOFRefInGrammar() {
		Pair<Parser, ParseTree> results = parse_calc2("\t\n/* foo */\n");
		VisitorCalcParser parser = (VisitorCalcParser)results.a;
		ParseTree tree = results.b;
		assertEquals("s2", tree.toStringTree(parser));
	}

	@Test public void testEmptyInputWithCommentEOFRefInGrammar() {
		String input = "\t\n/* foo */\n";
		Pair<Parser, ParseTree> results = parse_calc(input);
		VisitorCalcParser parser = (VisitorCalcParser)results.a;
		ParseTree tree = results.b;
		assertEquals("(s <EOF>)", tree.toStringTree(parser));
		ParseTree leaf = tree.getChild(0);
		assertEquals("\t\n/* foo */\n<EOF>", leaf.getText());

		verifyLeavesCoverCompleteInput(tree, input);
	}

	@Test public void testWSBeforeFirstToken() {
		String input = "\t\n 1";
		Pair<Parser, ParseTree> results = parse_calc(input);
		VisitorCalcParser parser = (VisitorCalcParser)results.a;
		ParseTree tree = results.b;
		assertEquals("(s (expr 1) <EOF>)", tree.toStringTree(parser));
		ParseTree leaf = tree.getChild(0).getChild(0);
		assertEquals("\t\n 1", leaf.getText());

		verifyLeavesCoverCompleteInput(tree, input);
	}

	@Test public void testWSAfterLastToken() {
		String input = "1 \t";
		Pair<Parser, ParseTree> results = parse_calc(input);
		VisitorCalcParser parser = (VisitorCalcParser)results.a;
		ParseTree tree = results.b;
		assertEquals("(s (expr 1) <EOF>)", tree.toStringTree(parser));
		ParseTree leaf = tree.getChild(0).getChild(0);
		assertEquals("1 \t", leaf.getText());

		verifyLeavesCoverCompleteInput(tree, input);
	}

	@Test public void testWSBeforeAfterSingleToken() {
		String input = " \t1 \t";
		Pair<Parser, ParseTree> results = parse_calc(input);
		VisitorCalcParser parser = (VisitorCalcParser)results.a;
		ParseTree tree = results.b;
		assertEquals("(s (expr 1) <EOF>)", tree.toStringTree(parser));
		ParseTree leaf = tree.getChild(0).getChild(0);
		assertEquals(" \t1 \t", leaf.getText());

		verifyLeavesCoverCompleteInput(tree, input);
	}

	@Test public void testMultilineWSAfterLastTokenGetsAll() {
		String input = "1 \t\n \n";
		Pair<Parser, ParseTree> results = parse_calc(input);
		VisitorCalcParser parser = (VisitorCalcParser)results.a;
		ParseTree tree = results.b;
		assertEquals("(s (expr 1) <EOF>)", tree.toStringTree(parser));
		ParseTree leaf = tree.getChild(0).getChild(0);
		assertEquals("1 \t\n \n", leaf.getText());

		verifyLeavesCoverCompleteInput(tree, input);
	}

	@Test public void testMultilineWSAfterTokenGetsOnLineOnly() {
		String input = "1 \t\n \n+ 2";
		Pair<Parser, ParseTree> results = parse_calc(input);
		VisitorCalcParser parser = (VisitorCalcParser)results.a;
		ParseTree tree = results.b;
		assertEquals("(s (expr (expr 1) + (expr 2)) <EOF>)", tree.toStringTree(parser));
		Collection<ParseTree> intsColl = XPath.findAll(tree, "//INT", parser);
		TerminalNodeWithHidden[] ints = intsColl.toArray(new TerminalNodeWithHidden[0]);
		assertEquals("1 \t\n", ints[0].getText());
		assertEquals("2", ints[1].getText());
		TerminalNodeWithHidden[] op = XPath.findAll(tree, "//'+'", parser).toArray(new TerminalNodeWithHidden[0]);
		assertEquals(" \n+ ", op[0].getText());

		verifyLeavesCoverCompleteInput(tree, input);
	}

	@Test public void testWSAroundSingleOp() {
		String input = "1 + 2\n";
		Pair<Parser, ParseTree> results = parse_calc(input);
		VisitorCalcParser parser = (VisitorCalcParser)results.a;
		ParseTree tree = results.b;
		assertEquals("(s (expr (expr 1) + (expr 2)) <EOF>)", tree.toStringTree(parser));
		Collection<ParseTree> intsColl = XPath.findAll(tree, "//INT", parser);
		TerminalNodeWithHidden[] ints = intsColl.toArray(new TerminalNodeWithHidden[0]);
		assertEquals("1 ", ints[0].getText());
		assertEquals("2\n", ints[1].getText());

		TerminalNodeWithHidden[] op = XPath.findAll(tree, "//'+'", parser).toArray(new TerminalNodeWithHidden[0]);
		assertEquals("+ ", op[0].getText());

		verifyLeavesCoverCompleteInput(tree, input);
	}

	// ERROR NODE CASES

	@Test
	public void testSingleInvalidToken() {
		String input = "\t\n+\n";
		Pair<Parser, ParseTree> results = parse_calc(input);
		VisitorCalcParser parser = (VisitorCalcParser)results.a;
		ParseTree tree = results.b;
		assertEquals("(s + <EOF>)", tree.toStringTree(parser));
		ParseTree leaf = tree.getChild(0);
		assertTrue(leaf instanceof ErrorNodeWithHidden);
		assertEquals("\t\n+\n", leaf.getText());

		verifyLeavesCoverCompleteInput(tree, input);
	}

	@Test
	public void testExtraToken() {
		String input = "1 + * 2";
		Pair<Parser, ParseTree> results = parse_calc(input);
		VisitorCalcParser parser = (VisitorCalcParser)results.a;
		ParseTree tree = results.b;
		assertEquals("(s (expr (expr 1) + (expr * 2)) <EOF>)", tree.toStringTree(parser));
		TerminalNodeWithHidden[] op = XPath.findAll(tree, "//'*'", parser).toArray(new TerminalNodeWithHidden[0]);
		assertTrue(op[0] instanceof ErrorNodeWithHidden);
		assertEquals("* ", op[0].getText());

		op = XPath.findAll(tree, "//'+'", parser).toArray(new TerminalNodeWithHidden[0]);
		assertEquals("+ ", op[0].getText());

		verifyLeavesCoverCompleteInput(tree, input);
	}

	@Test
	public void testMissingToken() {
		String input = "1 2 ";
		Pair<Parser, ParseTree> results = parse_calc(input);
		VisitorCalcParser parser = (VisitorCalcParser)results.a;
		ParseTree tree = results.b;
		assertEquals("(s (expr 1) 2 <EOF>)", tree.toStringTree(parser));
		TerminalNodeWithHidden[] ints = XPath.findAll(tree, "//INT", parser).toArray(new TerminalNodeWithHidden[0]);
		assertTrue(ints[1] instanceof ErrorNodeWithHidden);
		assertEquals("1 ", ints[0].getText());
		assertEquals("2 ", ints[1].getText());

		verifyLeavesCoverCompleteInput(tree, input);
	}

	@Test
	public void testConjuredUpImaginaryMissingTokenAtEnd() {
		String input = "a [ i //oops\n";
		Pair<Parser, ParseTree> results = parse_calc(input);
		VisitorCalcParser parser = (VisitorCalcParser) results.a;
		ParseTree tree = results.b;
		assertEquals("(s (expr (expr a) [ (expr i) <missing ']'>) <EOF>)", tree.toStringTree(parser));
		TerminalNode[] leaves = Trees.getLeaves(tree).toArray(new TerminalNode[0]);
		assertEquals("a ", leaves[0].getText());
		assertEquals("[ ", leaves[1].getText());
		assertEquals("i //oops\n", leaves[2].getText());
		assertEquals("<missing ']'>", leaves[3].getText());
		assertEquals("<EOF>", leaves[4].getText());

		// can't check leaves text against input since we conjure up a ']'
	}

	@Test
	public void testConjuredUpImaginaryMissingToken() {
		String input = "a 3 ;\n";
		Pair<Parser, ParseTree> results = parse_calc3(input);
		VisitorCalcParser parser = (VisitorCalcParser) results.a;
		ParseTree tree = results.b;
		assertEquals("(s3 a <missing '='> (expr 3) ;)", tree.toStringTree(parser));
		TerminalNode[] leaves = Trees.getLeaves(tree).toArray(new TerminalNode[0]);
		assertEquals("a ", leaves[0].getText());
		assertEquals("<missing '='>", leaves[1].getText());
		assertEquals("3 ", leaves[2].getText());
		assertEquals(";\n", leaves[3].getText());

		// can't check leaves text against input since we conjure up a '='
	}

	// SUPPORT

	protected void verifyLeavesCoverCompleteInput(ParseTree tree, String input) {
		List<TerminalNode> leaves = Trees.getLeaves(tree);
		StringBuilder buf = new StringBuilder();
		for (TerminalNode t : leaves) {
			TerminalNodeWithHidden t2 = (TerminalNodeWithHidden)t;
			if ( t2.getSymbol().getType()==Token.EOF ) {
				buf.append(getText(t2.getLeadingHidden()));
			}
			else {
				buf.append(t.getText());
			}
		}
		assertEquals(input, buf.toString());
	}

	public static String getText(List<TerminalNode> leaves) {
		StringBuilder buf = new StringBuilder();
		for (TerminalNode t : leaves) {
			buf.append(t.getText());
		}
		return buf.toString();
	}

	public static String getText(Token[] tokens) {
		if ( tokens==null || tokens.length==0 ) return "";
		StringBuilder buf = new StringBuilder();
		for (Token t : tokens) {
			buf.append(t.getText());
		}
		return buf.toString();
	}

	public Pair<Parser, ParseTree> parse_calc(String input) {
		final VisitorCalcLexer lexer = new VisitorCalcLexer(new ANTLRInputStream(input));
		final CommonTokenStream tokens = new CommonTokenStream(lexer);
		final VisitorCalcParser parser = new MyVisitorCalcParser(tokens);
		ParseTree tree = parser.s();
		return new Pair<>((Parser)parser, tree);
	}

	public Pair<Parser, ParseTree> parse_calc2(String input) {
		final VisitorCalcLexer lexer = new VisitorCalcLexer(new ANTLRInputStream(input));
		final CommonTokenStream tokens = new CommonTokenStream(lexer);
		final VisitorCalcParser parser = new MyVisitorCalcParser(tokens);
		ParseTree tree = parser.s2();
		return new Pair<>((Parser)parser, tree);
	}

	public Pair<Parser, ParseTree> parse_calc3(String input) {
		final VisitorCalcLexer lexer = new VisitorCalcLexer(new ANTLRInputStream(input));
		final CommonTokenStream tokens = new CommonTokenStream(lexer);
		final VisitorCalcParser parser = new MyVisitorCalcParser(tokens);
		ParseTree tree = parser.s3();
		return new Pair<>((Parser)parser, tree);
	}
}
