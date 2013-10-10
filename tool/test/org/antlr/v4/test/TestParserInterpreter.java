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

		String input = "a";
		LexerInterpreter lexEngine = new LexerInterpreter(lg, input);
		CommonTokenStream tokens = new CommonTokenStream(lexEngine);

		ParserInterpreter parser = new ParserInterpreter(g, tokens);
		ParseTree t = parser.parse("s");
		System.out.println(t.toStringTree(parser));
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
		String input = "a";
		LexerInterpreter lexEngine = new LexerInterpreter(lg, input);
		CommonTokenStream tokens = new CommonTokenStream(lexEngine);

		ParserInterpreter parser = new ParserInterpreter(g, tokens);
		ParseTree t = parser.parse("s");
		assertEquals("s", t.toStringTree(parser));

		input = "b";
		lexEngine = new LexerInterpreter(lg, input);
		tokens = new CommonTokenStream(lexEngine);

		parser = new ParserInterpreter(g, tokens);
		t = parser.parse("s");
		assertEquals("s", t.toStringTree(parser));
	}
}
