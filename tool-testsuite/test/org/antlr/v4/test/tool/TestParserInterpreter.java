/*
 * [The "BSD license"]
 * Copyright (c) 2013 Terence Parr
 * Copyright (c) 2013 Sam Harwell
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.test.tool;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.LexerInterpreter;
import org.antlr.v4.runtime.ParserInterpreter;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.LexerGrammar;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class TestParserInterpreter extends BaseJavaToolTest {
	@Before
	@Override
	public void testSetUp() throws Exception {
		super.testSetUp();
	}

	@Test public void testEmptyStartRule() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n" +
			"s :  ;",
			lg);

		testInterp(lg, g, "s", "", "s");
		testInterp(lg, g, "s", "a", "s");
	}

	@Test public void testA() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n" +
			"s : A ;",
			lg);

		ParseTree t = testInterp(lg, g, "s", "a", "(s a)");
		assertEquals("0..0", t.getSourceInterval().toString());
	}

	@Test public void testEOF() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n" +
			"s : A EOF ;",
			lg);

		ParseTree t = testInterp(lg, g, "s", "a", "(s a <EOF>)");
		assertEquals("0..1", t.getSourceInterval().toString());
	}

	@Test public void testEOFInChild() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n" +
			"s : x ;\n" +
			"x : A EOF ;",
			lg);

		ParseTree t = testInterp(lg, g, "s", "a", "(s (x a <EOF>))");
		assertEquals("0..1", t.getSourceInterval().toString());
		assertEquals("0..1", t.getChild(0).getSourceInterval().toString());
	}

	@Test public void testEmptyRuleAfterEOFInChild() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n" +
			"s : x y;\n" +
			"x : A EOF ;\n" +
			"y : ;",
			lg);

		ParseTree t = testInterp(lg, g, "s", "a", "(s (x a <EOF>) y)");
		assertEquals("0..1", t.getSourceInterval().toString()); // s
		assertEquals("0..1", t.getChild(0).getSourceInterval().toString()); // x
// unspecified		assertEquals("1..0", t.getChild(1).getSourceInterval().toString()); // y
	}

	@Test public void testEmptyRuleAfterJustEOFInChild() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n" +
			"s : x y;\n" +
			"x : EOF ;\n" +
			"y : ;",
			lg);

		ParseTree t = testInterp(lg, g, "s", "", "(s (x <EOF>) y)");
		assertEquals("0..0", t.getSourceInterval().toString()); // s
		assertEquals("0..0", t.getChild(0).getSourceInterval().toString()); // x
		// this next one is a weird special case where somebody tries to match beyond in the file
// unspecified		assertEquals("0..-1", t.getChild(1).getSourceInterval().toString()); // y
	}

	@Test public void testEmptyInput() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n" +
			"s : x EOF ;\n" +
			"x : ;\n",
			lg);

		ParseTree t = testInterp(lg, g, "s", "", "(s x <EOF>)");
		assertEquals("0..0", t.getSourceInterval().toString()); // s
		assertEquals("0..-1", t.getChild(0).getSourceInterval().toString()); // x
	}

	@Test public void testEmptyInputWithCallsAfter() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n" +
			"s : x y ;\n" +
			"x : EOF ;\n" +
			"y : z ;\n" +
			"z : ;",
			lg);

		ParseTree t = testInterp(lg, g, "s", "", "(s (x <EOF>) (y z))");
		assertEquals("0..0", t.getSourceInterval().toString()); // s
		assertEquals("0..0", t.getChild(0).getSourceInterval().toString()); // x
// unspecified		assertEquals("0..-1", t.getChild(1).getSourceInterval().toString()); // x
	}

	@Test public void testEmptyFirstRule() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n" +
			"s : x A ;\n" +
			"x : ;\n",
			lg);

		ParseTree t = testInterp(lg, g, "s", "a", "(s x a)");
		assertEquals("0..0", t.getSourceInterval().toString()); // s
		// This gets an empty interval because the stop token is null for x
		assertEquals("0..-1", t.getChild(0).getSourceInterval().toString()); // x
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

		testInterp(lg, g, "s", "a", 	"(s (e a))");
		testInterp(lg, g, "s", "a+a", 	"(s (e (e a) + (e a)))");
		testInterp(lg, g, "s", "a*a", 	"(s (e (e a) * (e a)))");
		testInterp(lg, g, "s", "a+a+a", "(s (e (e (e a) + (e a)) + (e a)))");
		testInterp(lg, g, "s", "a*a+a", "(s (e (e (e a) * (e a)) + (e a)))");
		testInterp(lg, g, "s", "a+a*a", "(s (e (e a) + (e (e a) * (e a))))");
	}

	/**
	 * This is a regression test for antlr/antlr4#461.
	 * https://github.com/antlr/antlr4/issues/461
	 */
	@Test public void testLeftRecursiveStartRule() throws Exception {
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

		testInterp(lg, g, "e", "a", 	"(e a)");
		testInterp(lg, g, "e", "a+a", 	"(e (e a) + (e a))");
		testInterp(lg, g, "e", "a*a", 	"(e (e a) * (e a))");
		testInterp(lg, g, "e", "a+a+a", "(e (e (e a) + (e a)) + (e a))");
		testInterp(lg, g, "e", "a*a+a", "(e (e (e a) * (e a)) + (e a))");
		testInterp(lg, g, "e", "a+a*a", "(e (e a) + (e (e a) * (e a)))");
	}

	ParseTree testInterp(LexerGrammar lg, Grammar g,
					String startRule, String input,
					String expectedParseTree)
	{
		LexerInterpreter lexEngine = lg.createLexerInterpreter(new ANTLRInputStream(input));
		CommonTokenStream tokens = new CommonTokenStream(lexEngine);
		ParserInterpreter parser = g.createParserInterpreter(tokens);
		ParseTree t = parser.parse(g.rules.get(startRule).index);
		assertEquals(expectedParseTree, t.toStringTree(parser));
		return t;
	}
}
