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

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.test.runtime.java.BaseTest;
import org.antlr.v4.tool.DOTGenerator;
import org.antlr.v4.tool.LexerGrammar;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
public class TestATNLexerInterpreter extends BaseTest {
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

	@Test public void testLexerCaseInsensitive() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n" +
			"\n" +
			"options { caseInsensitive = true; }" +
			"\n" +
			"WS:             [ \\t\\r\\n] -> skip;\n" +
			"\n" +
			"SIMPLE_TOKEN:           'and';\n" +
			"TOKEN_WITH_SPACES:      'as' 'd' 'f';\n" +
			"TOKEN_WITH_DIGITS:      'INT64';\n" +
			"TOKEN_WITH_UNDERSCORE:  'TOKEN_WITH_UNDERSCORE';\n" +
			"BOOL:                   'true' | 'FALSE';\n" +
			"SPECIAL:                '==';\n" +
			"RANGE:                  [a-z0-9]+;\n"    // [a-zA-Z0-9]
			);

		String inputString =
			"and AND aND\n" +
			"asdf ASDF\n" +
			"int64\n" +
			"token_WITH_underscore\n" +
			"TRUE FALSE\n" +
			"==\n" +
			"A0bcDE93\n";

		String expecting = Utils.join(new String[] {
			"SIMPLE_TOKEN", "SIMPLE_TOKEN", "SIMPLE_TOKEN",
			"TOKEN_WITH_SPACES", "TOKEN_WITH_SPACES",
			"TOKEN_WITH_DIGITS",
			"TOKEN_WITH_UNDERSCORE",
			"BOOL", "BOOL",
			"SPECIAL",
			"RANGE", "EOF" },
			", WS, ");

		checkLexerMatches(lg, inputString, expecting);
	}

	@Test public void testLexerCaseInsensitiveWithNot() throws  Exception {
		String grammar =
				"lexer grammar L;\n" +
				"options { caseInsensitive = true; }" +
				"TOKEN_WITH_NOT:   ~'f';\n";     // ~('f' | 'F)
		execLexer("L.g4", grammar, "L", "F");

		assertEquals("line 1:0 token recognition error at: 'F'\n", stderrDuringParse);
	}

	@Test public void testLexerCaseInsensitiveFragments() throws Exception {
		LexerGrammar lg = new LexerGrammar(
				"lexer grammar L;\n" +
				"options { caseInsensitive = true; }" +
				"TOKEN_0:         FRAGMENT 'd'+;\n" +
				"TOKEN_1:         FRAGMENT 'e'+;\n" +
				"FRAGMENT:        'abc';\n");

		String inputString =
				"ABCDDD";

		String expecting = "TOKEN_0, EOF";

		checkLexerMatches(lg, inputString, expecting);
	}

	@Test public void testLexerCaseInsensitiveInModes() throws Exception {
		String lg =
				"lexer grammar L;\n" +

				"options { caseInsensitive = true; }      \n" +   // caseInsensitive
				"Token_0: 'abc0_' -> mode(CASE_SENSITIVE);\n" +

				"mode CASE_SENSITIVE, caseSensitive;      \n" +   // caseSensitive
				"Token_1: 'DEF1_' -> mode(CASE_INSENSITIVE);\n" +

				"mode CASE_INSENSITIVE;                   \n" +   // default, i.e. caseInsensitive
				"Token_2: 'gHi2_' -> mode(CASE_INSENSITIVE_EXPLICIT);\n" +

				"mode CASE_INSENSITIVE_EXPLICIT, caseInsensitive;\n" +  // caseInsensitive
				"Token_3: 'JkL3';";

		String inputString = "ABC0_DEF1_GHI2_JKL3";

		String result = execLexer("L.g4", lg, "L", "ABC0_DEF1_GHI2_JKL3", false);

		assertEquals("[@0,0:4='ABC0_',<1>,1:0]\n" +
					 "[@1,5:9='DEF1_',<2>,1:5]\n" +
					 "[@2,10:14='GHI2_',<3>,1:10]\n" +
					 "[@3,15:18='JKL3',<4>,1:15]\n" +
					 "[@4,19:18='<EOF>',<-1>,1:19]\n", result);
	}

	@Test public void testLexerCaseInsensitiveInOneMode() throws Exception {
		String lg =
				"lexer grammar L;\n" +

				"options { caseInsensitive = true; }      \n" +   // caseInsensitive
				"Token_1: 'a_';\n" +

				"mode DEFAULT_MODE, caseInsensitive;      \n" +   // caseInsensitive
				"Token_2: 'b_';\n" +

				"mode DEFAULT_MODE, caseSensitive;      \n" +     // caseSensitive
				"Token_3: 'd';\n";

		String result = execLexer("L.g4", lg, "L", "A_B_D", false);
		assertEquals("line 1:4 token recognition error at: 'D'\n", stderrDuringParse);
		assertEquals("[@0,0:1='A_',<1>,1:0]\n" +
				"[@1,2:3='B_',<2>,1:2]\n" +
				"[@2,5:4='<EOF>',<-1>,1:5]\n", result);
	}

	@Test public void testLexerCaseInsensitiveWithDifferentCultures() throws Exception {
		// From here: http://www.periodni.com/unicode_utf-8_encoding.html
		// TODO: Add tokens on Arabic, Japan, Chinese and other languages.
		LexerGrammar lg = new LexerGrammar(
				"lexer grammar L;\n" +
				"options { caseInsensitive = true; }" +
				"ENGLISH_TOKEN:   [a-z_]+;\n" +
				"GERMAN_TOKEN:    [äéöüß_]+;\n" +
				"FRENCH_TOKEN:    [àâæ-ëîïôœùûüÿ_]+;\n" +
				"CROATIAN_TOKEN:  [ćčđšž_]+;\n" +
				"ITALIAN_TOKEN:   [àèéìòù_]+;\n" +
				"SPANISH_TOKEN:   [áéíñóúü¡¿_]+;\n" +
				"GREEK_TOKEN:     [α-ω_]+;\n" +
				"RUSSIAN_TOKEN:   [а-я_]+;\n"
				);

		String inputString = "abcXYZ_äéöüßÄÉÖÜ_àâæçÙÛÜŸ_ćčđĐŠŽ_àèéÌÒÙ_áéÚÜ¡¿_αβγΧΨΩ_абвЭЮЯ_";

		String expecting = "ENGLISH_TOKEN, GERMAN_TOKEN, FRENCH_TOKEN, CROATIAN_TOKEN, ITALIAN_TOKEN, SPANISH_TOKEN, " +
				"GREEK_TOKEN, RUSSIAN_TOKEN, EOF";

		checkLexerMatches(lg, inputString, expecting);
	}

	protected void checkLexerMatches(LexerGrammar lg, String inputString, String expecting) {
		ATN atn = createATN(lg, true);
		CharStream input = new ANTLRInputStream(inputString);
		ATNState startState = atn.modeNameToStartState.get("DEFAULT_MODE");
		DOTGenerator dot = new DOTGenerator(lg);
		System.out.println(dot.getDOT(startState, true));

		List<String> tokenTypes = getTokenTypes(lg, atn, input);

		String result = Utils.join(tokenTypes.iterator(), ", ");
		System.out.println(tokenTypes);
		assertEquals(expecting, result);
	}

}
