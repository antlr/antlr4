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

		testInterp(lg, g, "s", "a", "(s a)");
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
		testInterp(lg, g, "s", "a", "(s a)");
		testInterp(lg, g, "s", "b", "(s b)");
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

		testInterp(lg, g, "s", "ac", "(s (t a) c)");
		testInterp(lg, g, "s", "bc", "(s (t b) c)");
	}

	@Test public void testCall2() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n" +
			"B : 'b' ;\n" +
			"C : 'c' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"s : t C ;\n" +
			"t : u ;\n" +
			"u : A{;} | B ;\n",
			lg);

		testInterp(lg, g, "s", "ac", "(s (t (u a)) c)");
		testInterp(lg, g, "s", "bc", "(s (t (u b)) c)");
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

		testInterp(lg, g, "s", "b", "(s b)");
		testInterp(lg, g, "s", "ab", "(s a b)");
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

		testInterp(lg, g, "s", "c", "(s c)");
		testInterp(lg, g, "s", "ac", "(s a c)");
		testInterp(lg, g, "s", "bc", "(s b c)");
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

		testInterp(lg, g, "s", "b", "(s b)");
		testInterp(lg, g, "s", "ab", "(s a b)");
		testInterp(lg, g, "s", "aaaaaab", "(s a a a a a a b)");
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

		testInterp(lg, g, "s", "c", "(s c)");
		testInterp(lg, g, "s", "ac", "(s a c)");
		testInterp(lg, g, "s", "bc", "(s b c)");
		testInterp(lg, g, "s", "abaaabc", "(s a b a a a b c)");
		testInterp(lg, g, "s", "babac", "(s b a b a c)");
	}

	@Test public void testLeftRecursion() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n" +
			"B : 'b' ;\n" +
			"C : 'c' ;\n" +
			"PLUS : '+' ;\n" +
			"MULT : '*' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n" +
			"s : e ;\n" +
			"e : e MULT e\n" +
			"  | e PLUS e\n" +
			"  | A\n" +
			"  ;\n",
			lg);

//		testInterp(lg, g, "s", "a", "(s (e a))");
		testInterp(lg, g, "s", "a+a", "(s (e a) + (e a))");
//		testInterp(lg, g, "s", "a+a*b", "s");
	}

	void testInterp(LexerGrammar lg, Grammar g,
					String startRule, String input,
					String parseTree)
	{
		LexerInterpreter lexEngine = new LexerInterpreter(lg, input);
		CommonTokenStream tokens = new CommonTokenStream(lexEngine);

		ParserInterpreter parser = new ParserInterpreter(g, tokens);
		ParseTree t = parser.parse(startRule);
		System.out.println("parse tree: "+t.toStringTree(parser));
		assertEquals(parseTree, t.toStringTree(parser));
	}
}
