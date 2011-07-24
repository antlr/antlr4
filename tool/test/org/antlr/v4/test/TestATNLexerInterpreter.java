package org.antlr.v4.test;

import org.antlr.v4.misc.Utils;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.tool.*;
import org.junit.*;

import java.util.List;

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
			"  | 'xyz'\n" +
			"  ;\n");
		checkLexerMatches(lg, "xy", "A, EOF");
		checkLexerMatches(lg, "xyz", "A, EOF");
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
			"WS : (' '|'\n')+ ;");
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
			"WS : (' '|'\n')+ ;");
		String expecting = "INT, WS, INT, EOF";
		checkLexerMatches(lg, "32 99", expecting);
	}

	@Test public void testRecursiveLexerRuleRef() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"CMT : '/*' (CMT | ~'*')+ '*/' ;\n" +
			"WS : (' '|'\n')+ ;");
		String expecting = "CMT, WS, CMT, EOF";
		checkLexerMatches(lg, "/* ick */\n/* /*nested*/ */", expecting);
	}

	@Ignore public void testLexerWildcardNonGreedyLoopByDefault() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"CMT : '//' .* '\\n' ;\n");
		String expecting = "CMT, CMT, EOF";
		checkLexerMatches(lg, "//x\n//y\n", expecting);
	}

	// should not work. no priority within a single rule. the subrule won't work. need modes
	@Ignore
	public void testLexerEscapeInString() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"STR : '\"' ('\\\\' '\"' | .)* '\"' ;\n"); // STR : '"' ('\\' '"' | .)* '"'
		checkLexerMatches(lg, "\"a\\\"b\"", "STR, EOF");
		checkLexerMatches(lg, "\"a\"", "STR, EOF");
	}

	@Ignore public void testLexerWildcardNonGreedyPlusLoopByDefault() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"CMT : '//' .+ '\\n' ;\n");
		String expecting = "CMT, CMT, EOF";
		checkLexerMatches(lg, "//x\n//y\n", expecting);
	}

	@Ignore public void testLexerGreedyOptionalShouldWorkAsWeExpect() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"CMT : '/*' ('*/')? '*/' ;\n");
		String expecting = "CMT, EOF";
		checkLexerMatches(lg, "/**/", expecting);
	}

	@Ignore public void testNonGreedyBetweenRules() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"A : '<a>' ;\n" +
			"B : '<' .+ '>' ;\n");
		String expecting = "A, B, EOF";
		checkLexerMatches(lg, "<a><x>", expecting);
	}

	protected void checkLexerMatches(LexerGrammar lg, String inputString, String expecting) {
		ATN atn = createATN(lg);
		CharStream input = new ANTLRStringStream(inputString);
		ATNState startState = atn.modeNameToStartState.get("DEFAULT_MODE");
		DOTGenerator dot = new DOTGenerator(lg);
		System.out.println(dot.getDOT(startState, true));

		List<String> tokenTypes = getTokenTypes(lg, atn, input, false);
		String result = Utils.join(tokenTypes.iterator(), ", ");
		System.out.println(tokenTypes);
		assertEquals(expecting, result);

		// try now adaptive DFA
		input.seek(0);
		List<String> tokenTypes2 = getTokenTypes(lg, atn, input, true);
		assertEquals("interp vs adaptive types differ", tokenTypes, tokenTypes2);
	}

}
