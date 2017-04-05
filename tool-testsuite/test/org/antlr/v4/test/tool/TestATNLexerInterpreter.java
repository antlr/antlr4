/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.tool.DOTGenerator;
import org.antlr.v4.tool.LexerGrammar;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Lexer rules are little quirky when it comes to wildcards. Problem
 * stems from the fact that we want the longest match to win among
 * several rules and even within a rule. However, that conflicts
 * with the notion of non-greedy, which by definition tries to match
 * the fewest possible. During ATN construction, non-greedy loops
 * have their entry and exit branches reversed so that the ATN
 * simulator will see the exit branch 1st, giving it a priority. The
 * 1st path to the stop state kills any other paths for that rule
 * that begin with the wildcard. In general, this does everything we
 * want, but occasionally there are some quirks as you'll see from
 * the tests below.
 */
public class TestATNLexerInterpreter extends BaseJavaToolTest {
	@Before
	@Override
	public void testSetUp() throws Exception {
		super.testSetUp();
	}

	@Test public void testLexerTwoRules() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"A : 'a' ;\n" +
			"B : 'b' ;\n");
		String expecting = "A, B, A, B, EOF";
		checkLexerMatches(lg, "abab", expecting);
	}

	@Test public void testShortLongRule() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"A : 'xy'\n" +
			"  | 'xyz'\n" + // this alt is preferred since there are no non-greedy configs
			"  ;\n" +
			"Z : 'z'\n" +
			"  ;\n");
		checkLexerMatches(lg, "xy", "A, EOF");
		checkLexerMatches(lg, "xyz", "A, EOF");
	}

	@Test public void testShortLongRule2() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"A : 'xyz'\n" +  // make sure nongreedy mech cut off doesn't kill this alt
			"  | 'xy'\n" +
			"  ;\n");
		checkLexerMatches(lg, "xy", "A, EOF");
		checkLexerMatches(lg, "xyz", "A, EOF");
	}

	@Test public void testWildOnEndFirstAlt() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"A : 'xy' .\n" + // should pursue '.' since xyz hits stop first, before 2nd alt
			"  | 'xy'\n" +
			"  ;\n" +
			"Z : 'z'\n" +
			"  ;\n");
		checkLexerMatches(lg, "xy", "A, EOF");
		checkLexerMatches(lg, "xyz", "A, EOF");
	}

	@Test public void testWildOnEndLastAlt() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"A : 'xy'\n" +
			"  | 'xy' .\n" +  // this alt is preferred since there are no non-greedy configs
			"  ;\n" +
			"Z : 'z'\n" +
			"  ;\n");
		checkLexerMatches(lg, "xy", "A, EOF");
		checkLexerMatches(lg, "xyz", "A, EOF");
	}

	@Test public void testWildcardNonQuirkWhenSplitBetweenTwoRules() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"A : 'xy' ;\n" +
			"B : 'xy' . 'z' ;\n");
		checkLexerMatches(lg, "xy", "A, EOF");
		checkLexerMatches(lg, "xyqz", "B, EOF");
	}

	@Test public void testLexerLoops() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"INT : '0'..'9'+ ;\n" +
			"ID : 'a'..'z'+ ;\n");
		String expecting = "ID, INT, ID, INT, EOF";
		checkLexerMatches(lg, "a34bde3", expecting);
	}

	@Test public void testLexerNotSet() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"ID : ~('a'|'b')\n ;");
		String expecting = "ID, EOF";
		checkLexerMatches(lg, "c", expecting);
	}

	@Test public void testLexerSetUnicodeBMP() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"ID : ('\u611B'|'\u611C')\n ;");
		String expecting = "ID, EOF";
		checkLexerMatches(lg, "\u611B", expecting);
	}

	@Test public void testLexerNotSetUnicodeBMP() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"ID : ~('\u611B'|'\u611C')\n ;");
		String expecting = "ID, EOF";
		checkLexerMatches(lg, "\u611D", expecting);
	}

		@Test public void testLexerNotSetUnicodeBMPMatchesSMP() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"ID : ~('\u611B'|'\u611C')\n ;");
		String expecting = "ID, EOF";
		checkLexerMatches(lg, new StringBuilder().appendCodePoint(0x1F4A9).toString(), expecting);
	}

	@Test public void testLexerSetUnicodeSMP() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"ID : ('\\u{1F4A9}'|'\\u{1F4AA}')\n ;");
		String expecting = "ID, EOF";
		checkLexerMatches(lg, new StringBuilder().appendCodePoint(0x1F4A9).toString(), expecting);
	}

	@Test public void testLexerNotBMPSetMatchesUnicodeSMP() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"ID : ~('a'|'b')\n ;");
		String expecting = "ID, EOF";
		checkLexerMatches(lg, new StringBuilder().appendCodePoint(0x1F4A9).toString(), expecting);
	}

	@Test public void testLexerNotBMPSetMatchesBMP() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"ID : ~('a'|'b')\n ;");
		String expecting = "ID, EOF";
		checkLexerMatches(lg, "\u611B", expecting);
	}

	@Test public void testLexerNotBMPSetMatchesSMP() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"ID : ~('a'|'b')\n ;");
		String expecting = "ID, EOF";
		checkLexerMatches(lg, new StringBuilder().appendCodePoint(0x1F4A9).toString(), expecting);
	}

	@Test public void testLexerNotSMPSetMatchesBMP() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"ID : ~('\\u{1F4A9}'|'\\u{1F4AA}')\n ;");
		String expecting = "ID, EOF";
		checkLexerMatches(lg, "\u611B", expecting);
	}

	@Test public void testLexerNotSMPSetMatchesSMP() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"ID : ~('\\u{1F4A9}'|'\\u{1F4AA}')\n ;");
		String expecting = "ID, EOF";
		checkLexerMatches(lg, new StringBuilder().appendCodePoint(0x1D7C0).toString(), expecting);
	}

	@Test public void testLexerRangeUnicodeSMP() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"ID : ('\\u{1F4A9}'..'\\u{1F4B0}')\n ;");
		String expecting = "ID, EOF";
		checkLexerMatches(lg, new StringBuilder().appendCodePoint(0x1F4AF).toString(), expecting);
	}

	@Test public void testLexerRangeUnicodeBMPToSMP() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"ID : ('\\u611B'..'\\u{1F4B0}')\n ;");
		String expecting = "ID, EOF";
		checkLexerMatches(lg, new StringBuilder().appendCodePoint(0x12001).toString(), expecting);
	}

	@Test public void testLexerKeywordIDAmbiguity() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"KEND : 'end' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n')+ ;");
		String expecting = "ID, EOF";
		//checkLexerMatches(lg, "e", expecting);
		expecting = "KEND, EOF";
		checkLexerMatches(lg, "end", expecting);
		expecting = "ID, EOF";
		checkLexerMatches(lg, "ending", expecting);
		expecting = "ID, WS, KEND, WS, ID, EOF";
		checkLexerMatches(lg, "a end bcd", expecting);
	}

	@Test public void testLexerRuleRef() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"INT : DIGIT+ ;\n" +
			"fragment DIGIT : '0'..'9' ;\n" +
			"WS : (' '|'\\n')+ ;");
		String expecting = "INT, WS, INT, EOF";
		checkLexerMatches(lg, "32 99", expecting);
	}

	@Test public void testRecursiveLexerRuleRef() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"CMT : '/*' (CMT | ~'*')+ '*/' ;\n" +
			"WS : (' '|'\\n')+ ;");
		String expecting = "CMT, WS, CMT, EOF";
		checkLexerMatches(lg, "/* ick */\n/* /*nested*/ */", expecting);
	}

	@Test public void testRecursiveLexerRuleRefWithWildcard() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"CMT : '/*' (CMT | .)*? '*/' ;\n" +
			"WS : (' '|'\\n')+ ;");

		String expecting = "CMT, WS, CMT, WS, EOF";
		checkLexerMatches(lg,
						  "/* ick */\n" +
						  "/* /* */\n" +
						  "/* /*nested*/ */\n",
						  expecting);
	}

	@Test public void testLexerWildcardGreedyLoopByDefault() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"CMT : '//' .* '\\n' ;\n");
		String expecting = "CMT, EOF";
		checkLexerMatches(lg, "//x\n//y\n", expecting);
	}

	@Test public void testLexerWildcardLoopExplicitNonGreedy() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"CMT : '//' .*? '\\n' ;\n");
		String expecting = "CMT, CMT, EOF";
		checkLexerMatches(lg, "//x\n//y\n", expecting);
	}

	@Test public void testLexerEscapeInString() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"STR : '[' ('~' ']' | .)* ']' ;\n");
		checkLexerMatches(lg, "[a~]b]", "STR, EOF");
		checkLexerMatches(lg, "[a]", "STR, EOF");
	}

	@Test public void testLexerWildcardGreedyPlusLoopByDefault() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"CMT : '//' .+ '\\n' ;\n");
		String expecting = "CMT, EOF";
		checkLexerMatches(lg, "//x\n//y\n", expecting);
	}

	@Test public void testLexerWildcardExplicitNonGreedyPlusLoop() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"CMT : '//' .+? '\\n' ;\n");
		String expecting = "CMT, CMT, EOF";
		checkLexerMatches(lg, "//x\n//y\n", expecting);
	}

	// does not fail since ('*/')? can't match and have rule succeed
	@Test public void testLexerGreedyOptionalShouldWorkAsWeExpect() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"CMT : '/*' ('*/')? '*/' ;\n");
		String expecting = "CMT, EOF";
		checkLexerMatches(lg, "/**/", expecting);
	}

	@Test public void testGreedyBetweenRules() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"A : '<a>' ;\n" +
			"B : '<' .+ '>' ;\n");
		String expecting = "B, EOF";
		checkLexerMatches(lg, "<a><x>", expecting);
	}

	@Test public void testNonGreedyBetweenRules() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"A : '<a>' ;\n" +
			"B : '<' .+? '>' ;\n");
		String expecting = "A, B, EOF";
		checkLexerMatches(lg, "<a><x>", expecting);
	}

	@Test public void testEOFAtEndOfLineComment() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"CMT : '//' ~('\\n')* ;\n");
		String expecting = "CMT, EOF";
		checkLexerMatches(lg, "//x", expecting);
	}

	@Test public void testEOFAtEndOfLineComment2() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"CMT : '//' ~('\\n'|'\\r')* ;\n");
		String expecting = "CMT, EOF";
		checkLexerMatches(lg, "//x", expecting);
	}

	/** only positive sets like (EOF|'\n') can match EOF and not in wildcard or ~foo sets
	 *  EOF matches but does not advance cursor.
	 */
	@Test public void testEOFInSetAtEndOfLineComment() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"CMT : '//' .* (EOF|'\\n') ;\n");
		String expecting = "CMT, EOF";
		checkLexerMatches(lg, "//", expecting);
	}

	@Test public void testEOFSuffixInSecondRule() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"A : 'a' ;\n"+ // shorter than 'a' EOF, despite EOF being 0 width
			"B : 'a' EOF ;\n");
		String expecting = "B, EOF";
		checkLexerMatches(lg, "a", expecting);
	}

	@Test public void testEOFSuffixInFirstRule() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"A : 'a' EOF ;\n"+
			"B : 'a';\n");
		String expecting = "A, EOF";
		checkLexerMatches(lg, "a", expecting);
	}

	@Test public void testEOFByItself() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"DONE : EOF ;\n"+
			"A : 'a';\n");
		String expecting = "A, DONE, EOF";
		checkLexerMatches(lg, "a", expecting);
	}

	protected void checkLexerMatches(LexerGrammar lg, String inputString, String expecting) {
		ATN atn = createATN(lg, true);
		CharStream input = CharStreams.fromString(inputString);
		ATNState startState = atn.modeNameToStartState.get("DEFAULT_MODE");
		DOTGenerator dot = new DOTGenerator(lg);
//		System.out.println(dot.getDOT(startState, true));

		List<String> tokenTypes = getTokenTypes(lg, atn, input);

		String result = Utils.join(tokenTypes.iterator(), ", ");
//		System.out.println(tokenTypes);
		assertEquals(expecting, result);
	}

}
