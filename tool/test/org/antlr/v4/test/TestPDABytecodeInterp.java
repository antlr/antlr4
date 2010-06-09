package org.antlr.v4.test;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.v4.runtime.pda.PDA;
import org.antlr.v4.tool.LexerGrammar;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** */
public class TestPDABytecodeInterp extends BaseTest {
	@Test public void testString() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n"+
			"A : 'ab' ;");
		String expecting = "A, A, EOF";
		checkMatches(g, "abab", expecting);
	}

	@Test public void testUnicode() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n"+
			"A : '\\u0020' ;"); // space
		String expecting = "A, A, EOF";
		checkMatches(g, "  ", expecting);
	}

	@Test public void testEscapes() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n"+
			"WS : '\t'|'\n'|' ' ;");
		String expecting = "WS, WS, WS, WS, WS, EOF";
		checkMatches(g, " \t\n\n ", expecting);
	}

	@Test public void testNotChar() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n"+
			"A : ~'a' ;");
		String expecting = "A, EOF";
		checkMatches(g, "b", expecting);
	}

	@Test public void testIDandIntandKeyword() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'ab';\n" +
			"B : 'a'..'z'+ ;\n" +
			"I : '0'..'9'+ ;\n");
		String expecting = "A, I, B, EOF";
		checkMatches(g, "ab32abc", expecting);
	}

	@Test public void testSLComment() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n" +
			"\n" +
			"CMT : '//' ~('\r'|'\n')* '\r'? '\n' ;\n" +
			"ID  : 'ab' ;\n");
		String expecting = "ID, CMT, ID, EOF";
		checkMatches(g, "ab// foo\nab", expecting);
	}

	@Test public void testNonGreedy() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n" +
			"\n" +
			"CMT : '/*' (options {greedy=false;}:.)* '*/' ;\n" +
			"ID  : 'ab' ;\n");
		String expecting = "ID, CMT, EOF";
		checkMatches(g, "ab/* x */", expecting);
	}

	@Test public void testNonGreedyAndCommonLeftPrefix() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n" +
			"\n" +
			"CMT : '/*' (options {greedy=false;}:.)* '*/' ;\n" +
			"CMT2: '/*' (options {greedy=false;}:.)* '*/' '!' ;\n" +
			"ID  : 'ab' ;\n");
		String expecting = "ID, CMT2, CMT, EOF";
		checkMatches(g, "ab/* x */!/* foo */", expecting);
	}

	@Test public void testCallFragment() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n" +
			"I : D+ ;\n" +
			"fragment D : '0'..'9'+ ;\n");
		String expecting = "I, EOF";
		checkMatches(g, "32", expecting);
	}

	@Test public void testCallNonFragment() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n" +
			"QID : ID ('.' ID)+ ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"WS : ' ' ;\n");
		String expecting = "ID, EOF";
		checkMatches(g, "z", expecting);
		expecting = "ID, WS, QID, WS, ID, WS, QID, WS, ID, EOF";
		checkMatches(g, "z a.b x c.d.e y", expecting);
	}

	@Test public void testRecursiveCall() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n" +
			"ACTION : '{' (ACTION|.)* '}' ;\n");
		String expecting = "ACTION, EOF";
		checkMatches(g, "{hi}", expecting);
		checkMatches(g, "{{hi}}", expecting);
		checkMatches(g, "{{x}{y}}", expecting);
		checkMatches(g, "{{{{{{x}}}}}}", expecting);
	}

	@Test public void testAltOrWildcard() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' ;\n" +
			"ELSE : . ;\n");
		String expecting = "A, A, ELSE, A, EOF";
		checkMatches(g, "aaxa", expecting);
	}

	@Test public void testRewindBackToLastGoodMatch() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : 'a' 'b'? ;\n"+
			"B : 'b' ;\n"+
			"WS : ' ' ;\n");
		String expecting = "A, WS, A, WS, B, EOF";
		checkMatches(g, "a ab b", expecting);
	}

	// fixes http://www.antlr.org/jira/browse/ANTLR-189 from v3
	@Test public void testRewindBackToLastGoodMatch_DOT_vs_NUM() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n" +
			"NUM: '0'..'9'+ ('.' '0'..'9'+)? ;\n"+
			"DOT : '.' ;\n"+
			"WS : ' ' ;\n");
		checkMatches(g, "3.14 .", "NUM, WS, DOT, EOF");
		checkMatches(g, "9", "NUM, EOF");
		checkMatches(g, ".1", "DOT, NUM, EOF");
		checkMatches(g, "1.", "NUM, DOT, EOF");
	}

	@Test public void testLabeledChar() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : a='a' ;\n");
		checkLabels(g, "a", "A", "[[@-1,0:0='a',<0>,1:0]]");
	}

	@Test public void testLabeledString() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : a='abc' ;\n");
		checkLabels(g, "abc", "A", "[[@-1,0:2='abc',<0>,1:0]]");
	}

	@Test public void testLabeledToken() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n" +
			"I : d=D ;\n" +
			"fragment D : '0'..'9'+ ;\n");
		checkLabels(g, "901", "I", "[[@-1,0:2='901',<0>,1:0]]");
	}

	@Test public void testLabelInLoopIsLastElement() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n" +
			"I : d=D+ ;\n" +
			"fragment D : '0'..'9' ;\n");
		checkLabels(g, "901", "I", "[[@-1,2:2='1',<0>,1:2]]");
	}

	@Test public void testLabelIndexes() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : a='a' ;\n" +
			"B : a='b' b='c' ;\n");
		checkLabels(g, "bc", "B", "[[@-1,0:-1='',<0>,1:0], [@-1,0:0='b',<0>,1:0], [@-1,1:1='c',<0>,1:1]]");
	}

	@Test public void testAction() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n" +
			"I : {a1} d=D {a2} ;\n" +
			"fragment D : ('0'..'9' {a3})+ ;\n");
		checkLabels(g, "901", "I", "[[@-1,0:2='901',<0>,1:0]]");
	}

	@Test public void testSempred() throws Exception {
		// not actually evaluating preds since we're interpreting; assumes true.
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n" +
			"A : {true}? 'a' | 'b' {true}? ;\n");
		checkMatches(g, "ab", "A, A, EOF");
	}


	public void _template() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"\n");
		String expecting = "";
		checkMatches(g, "input", expecting);
	}

	void checkMatches(LexerGrammar g, String input, String expecting) {
		PDA pda = getLexerPDA(g);

		List<Integer> expectingTokenTypes = getTypesFromString(g, expecting);

		List<Integer> tokenTypes = getTokenTypes(input, pda);
		assertEquals(expectingTokenTypes, tokenTypes);
	}

	void checkLabels(LexerGrammar g, String input, String expecting,
					  String expectingTokens)
	{
		PDA pda = getLexerPDA(g);
		List<Integer> expectingTokenTypes = getTypesFromString(g, expecting);
		ANTLRStringStream in = new ANTLRStringStream(input);
		List<Integer> tokenTypes = new ArrayList<Integer>();
		int ttype = pda.execThompson(in);
		tokenTypes.add(ttype);
		assertEquals(expectingTokenTypes, tokenTypes);

		if ( expectingTokens!=null ) {
			assertEquals(expectingTokens, Arrays.toString(pda.labelValues));
		}
	}

	

//	List<Token> getTokens(String input, PDA lexerPDA) {
//		ANTLRStringStream in = new ANTLRStringStream(input);
//		List<Token> tokens = new ArrayList<Token>();
//		int ttype = 0;
//		do {
//			ttype = lexerPDA.execThompson(in);
//			tokens.add(new CommonToken(ttype,""));
//		} while ( ttype!= Token.EOF );
//		return tokens;
//	}

}
