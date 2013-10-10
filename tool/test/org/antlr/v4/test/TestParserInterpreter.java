package org.antlr.v4.test;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.LexerGrammar;
import org.antlr.v4.tool.interp.LexerInterpreter;
import org.antlr.v4.tool.interp.ParserInterpreter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class TestParserInterpreter extends BaseTest {
	@Test public void testA() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n" +
			"s : A ;",
			lg);

		testInterp(lg, g, "s", "a", "s");
	}

	@Test public void testAorB() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n" +
			"B : 'b' ;\n" +
			"C : 'c' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"s : A{;} | B ;",
			lg);
		testInterp(lg, g, "s", "a", "s");
		testInterp(lg, g, "s", "b", "s");
	}

	@Test public void testCall() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n" +
			"B : 'b' ;\n" +
			"C : 'c' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"s : t C ;\n" +
			"t : A{;} | B ;\n",
			lg);

		testInterp(lg, g, "s", "ac", "s");
		testInterp(lg, g, "s", "bc", "s");
	}

	@Test public void testOptionalA() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n" +
			"B : 'b' ;\n" +
			"C : 'c' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n" +
			"s : A? B ;\n",
			lg);

		testInterp(lg, g, "s", "b", "s");
		testInterp(lg, g, "s", "ab", "s");
	}

	@Test public void testOptionalAorB() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n" +
			"B : 'b' ;\n" +
			"C : 'c' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n" +
			"s : (A{;}|B)? C ;\n",
			lg);

		testInterp(lg, g, "s", "c", "s");
		testInterp(lg, g, "s", "ac", "s");
		testInterp(lg, g, "s", "bc", "s");
	}

	@Test public void testStarA() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n" +
			"B : 'b' ;\n" +
			"C : 'c' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n" +
			"s : A* B ;\n",
			lg);

		testInterp(lg, g, "s", "b", "s");
		testInterp(lg, g, "s", "ab", "s");
		testInterp(lg, g, "s", "aaaaaab", "s");
	}

	@Test public void testStarAorB() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n" +
			"B : 'b' ;\n" +
			"C : 'c' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n" +
			"s : (A{;}|B)* C ;\n",
			lg);

		testInterp(lg, g, "s", "c", "s");
		testInterp(lg, g, "s", "ac", "s");
		testInterp(lg, g, "s", "bc", "s");
		testInterp(lg, g, "s", "abaaabc", "s");
		testInterp(lg, g, "s", "babac", "s");
	}

	void testInterp(LexerGrammar lg, Grammar g,
					String startRule, String input,
					String parseTree)
	{
		LexerInterpreter lexEngine = new LexerInterpreter(lg, input);
		CommonTokenStream tokens = new CommonTokenStream(lexEngine);

		ParserInterpreter parser = new ParserInterpreter(g, tokens);
		ParseTree t = parser.parse(startRule);
		assertEquals(parseTree, t.toStringTree(parser));
	}
}
