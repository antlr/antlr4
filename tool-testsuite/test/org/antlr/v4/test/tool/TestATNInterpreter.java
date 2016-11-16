/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.test.tool;

import org.antlr.v4.automata.ParserATNFactory;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.atn.BlockStartState;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.IntegerList;
import org.antlr.v4.test.runtime.java.BaseJavaTest;
import org.antlr.v4.tool.DOTGenerator;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.LexerGrammar;
import org.antlr.v4.tool.Rule;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

	// NOTICE: TOKENS IN LEXER, PARSER MUST BE SAME OR TOKEN TYPE MISMATCH
	// NOTICE: TOKENS IN LEXER, PARSER MUST BE SAME OR TOKEN TYPE MISMATCH
	// NOTICE: TOKENS IN LEXER, PARSER MUST BE SAME OR TOKEN TYPE MISMATCH

public class TestATNInterpreter extends BaseJavaTest {
	@Before
	@Override
	public void testSetUp() throws Exception {
		super.testSetUp();
	}

	@Test public void testSimpleNoBlock() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n" +
			"B : 'b' ;\n" +
			"C : 'c' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : A B ;");
		checkMatchedAlt(lg, g, "ab", 1);
	}

	@Test public void testSet() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n" +
			"B : 'b' ;\n" +
			"C : 'c' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"tokens {A,B,C}\n" +
			"a : ~A ;");
		checkMatchedAlt(lg, g, "b", 1);
	}

	@Test public void testPEGAchillesHeel() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n" +
			"B : 'b' ;\n" +
			"C : 'c' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : A | A B ;");
		checkMatchedAlt(lg, g, "a", 1);
		checkMatchedAlt(lg, g, "ab", 2);
		checkMatchedAlt(lg, g, "abc", 2);
	}

	@Test public void testMustTrackPreviousGoodAlt() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n" +
			"B : 'b' ;\n" +
			"C : 'c' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : A | A B ;");

		checkMatchedAlt(lg, g, "a", 1);
		checkMatchedAlt(lg, g, "ab", 2);

		checkMatchedAlt(lg, g, "ac", 1);
		checkMatchedAlt(lg, g, "abc", 2);
	}

	@Test(expected = NoViableAltException.class)
	public void testMustTrackPreviousGoodAltWithEOF() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n" +
			"B : 'b' ;\n" +
			"C : 'c' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : (A | A B) EOF;");

		checkMatchedAlt(lg, g, "a", 1);
		checkMatchedAlt(lg, g, "ab", 2);

		try {
			checkMatchedAlt(lg, g, "ac", 1);
		}
		catch (NoViableAltException re) {
			assertEquals(1, re.getOffendingToken().getTokenIndex());
			assertEquals(3, re.getOffendingToken().getType());
			throw re;
		}
	}

	@Test public void testMustTrackPreviousGoodAlt2() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n" +
			"B : 'b' ;\n" +
			"C : 'c' ;\n" +
			"D : 'd' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : A | A B | A B C ;");

		checkMatchedAlt(lg, g, "a", 1);
		checkMatchedAlt(lg, g, "ab", 2);
		checkMatchedAlt(lg, g, "abc", 3);

		checkMatchedAlt(lg, g, "ad", 1);
		checkMatchedAlt(lg, g, "abd", 2);
		checkMatchedAlt(lg, g, "abcd", 3);
	}

	@Test(expected = NoViableAltException.class)
	public void testMustTrackPreviousGoodAlt2WithEOF() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n" +
			"B : 'b' ;\n" +
			"C : 'c' ;\n" +
			"D : 'd' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : (A | A B | A B C) EOF;");

		checkMatchedAlt(lg, g, "a", 1);
		checkMatchedAlt(lg, g, "ab", 2);
		checkMatchedAlt(lg, g, "abc", 3);

		try {
			checkMatchedAlt(lg, g, "abd", 1);
		}
		catch (NoViableAltException re) {
			assertEquals(2, re.getOffendingToken().getTokenIndex());
			assertEquals(4, re.getOffendingToken().getType());
			throw re;
		}
	}

	@Test public void testMustTrackPreviousGoodAlt3() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n" +
			"B : 'b' ;\n" +
			"C : 'c' ;\n" +
			"D : 'd' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : A B | A | A B C ;");

		checkMatchedAlt(lg, g, "a", 2);
		checkMatchedAlt(lg, g, "ab", 1);
		checkMatchedAlt(lg, g, "abc", 3);

		checkMatchedAlt(lg, g, "ad", 2);
		checkMatchedAlt(lg, g, "abd", 1);
		checkMatchedAlt(lg, g, "abcd", 3);
	}

	@Test(expected = NoViableAltException.class)
	public void testMustTrackPreviousGoodAlt3WithEOF() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n" +
			"B : 'b' ;\n" +
			"C : 'c' ;\n" +
			"D : 'd' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : (A B | A | A B C) EOF;");

		checkMatchedAlt(lg, g, "a", 2);
		checkMatchedAlt(lg, g, "ab", 1);
		checkMatchedAlt(lg, g, "abc", 3);

		try {
			checkMatchedAlt(lg, g, "abd", 1);
		}
		catch (NoViableAltException re) {
			assertEquals(2, re.getOffendingToken().getTokenIndex());
			assertEquals(4, re.getOffendingToken().getType());
			throw re;
		}
	}

	@Test public void testAmbigAltChooseFirst() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n" +
			"B : 'b' ;\n" +
			"C : 'c' ;\n" +
			"D : 'd' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : A B | A B ;"); // first alt
		checkMatchedAlt(lg, g, "ab", 1);
		checkMatchedAlt(lg, g, "abc", 1);
	}

	@Test public void testAmbigAltChooseFirstWithFollowingToken() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n" +
			"B : 'b' ;\n" +
			"C : 'c' ;\n" +
			"D : 'd' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : (A B | A B) C ;"); // first alt
		checkMatchedAlt(lg, g, "abc", 1);
		checkMatchedAlt(lg, g, "abcd", 1);
	}

	@Test public void testAmbigAltChooseFirstWithFollowingToken2() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n" +
			"B : 'b' ;\n" +
			"C : 'c' ;\n" +
			"D : 'd' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : (A B | A B | C) D ;");
		checkMatchedAlt(lg, g, "abd", 1);
		checkMatchedAlt(lg, g, "abdc", 1);
		checkMatchedAlt(lg, g, "cd", 3);
	}

	@Test public void testAmbigAltChooseFirst2() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n" +
			"B : 'b' ;\n" +
			"C : 'c' ;\n" +
			"D : 'd' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : A B | A B | A B C ;");

		checkMatchedAlt(lg, g, "ab", 1);
		checkMatchedAlt(lg, g, "abc", 3);

		checkMatchedAlt(lg, g, "abd", 1);
		checkMatchedAlt(lg, g, "abcd", 3);
	}

	@Test(expected = NoViableAltException.class)
	public void testAmbigAltChooseFirst2WithEOF() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n" +
			"B : 'b' ;\n" +
			"C : 'c' ;\n" +
			"D : 'd' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : (A B | A B | A B C) EOF;");

		checkMatchedAlt(lg, g, "ab", 1);
		checkMatchedAlt(lg, g, "abc", 3);

		try {
			checkMatchedAlt(lg, g, "abd", 1);
		}
		catch (NoViableAltException re) {
			assertEquals(2, re.getOffendingToken().getTokenIndex());
			assertEquals(4, re.getOffendingToken().getType());
			throw re;
		}
	}

	@Test public void testSimpleLoop() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n" +
			"B : 'b' ;\n" +
			"C : 'c' ;\n" +
			"D : 'd' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : A+ B ;");
		checkMatchedAlt(lg, g, "ab", 1);
		checkMatchedAlt(lg, g, "aab", 1);
		checkMatchedAlt(lg, g, "aaaaaab", 1);
		checkMatchedAlt(lg, g, "aabd", 1);
	}

	@Test public void testCommonLeftPrefix() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n" +
			"B : 'b' ;\n" +
			"C : 'c' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : A B | A C ;");
		checkMatchedAlt(lg, g, "ab", 1);
		checkMatchedAlt(lg, g, "ac", 2);
	}

	@Test public void testArbitraryLeftPrefix() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n" +
			"B : 'b' ;\n" +
			"C : 'c' ;\n");
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : A+ B | A+ C ;");
		checkMatchedAlt(lg, g, "aac", 2);
	}

	@Test public void testRecursiveLeftPrefix() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n" +
			"B : 'b' ;\n" +
			"C : 'c' ;\n" +
			"LP : '(' ;\n" +
			"RP : ')' ;\n" +
			"INT : '0'..'9'+ ;\n"
		);
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"tokens {A,B,C,LP,RP,INT}\n" +
			"a : e B | e C ;\n" +
			"e : LP e RP\n" +
			"  | INT\n" +
			"  ;");
		checkMatchedAlt(lg, g, "34b", 1);
		checkMatchedAlt(lg, g, "34c", 2);
		checkMatchedAlt(lg, g, "(34)b", 1);
		checkMatchedAlt(lg, g, "(34)c", 2);
		checkMatchedAlt(lg, g, "((34))b", 1);
		checkMatchedAlt(lg, g, "((34))c", 2);
	}

	public void checkMatchedAlt(LexerGrammar lg, final Grammar g,
								String inputString,
								int expected)
	{
		ATN lexatn = createATN(lg, true);
		LexerATNSimulator lexInterp = new LexerATNSimulator(lexatn,new DFA[] { new DFA(lexatn.modeToStartState.get(Lexer.DEFAULT_MODE)) },null);
		IntegerList types = getTokenTypesViaATN(inputString, lexInterp);
//		System.out.println(types);

		g.importVocab(lg);

		ParserATNFactory f = new ParserATNFactory(g);
		ATN atn = f.createATN();

		IntTokenStream input = new IntTokenStream(types);
//		System.out.println("input="+input.types);
		ParserInterpreterForTesting interp = new ParserInterpreterForTesting(g, input);
		ATNState startState = atn.ruleToStartState[g.getRule("a").index];
		if ( startState.transition(0).target instanceof BlockStartState ) {
			startState = startState.transition(0).target;
		}

		DOTGenerator dot = new DOTGenerator(g);
//		System.out.println(dot.getDOT(atn.ruleToStartState[g.getRule("a").index]));
		Rule r = g.getRule("e");
//		if ( r!=null ) System.out.println(dot.getDOT(atn.ruleToStartState[r.index]));

		int result = interp.matchATN(input, startState);
		assertEquals(expected, result);
	}
}
