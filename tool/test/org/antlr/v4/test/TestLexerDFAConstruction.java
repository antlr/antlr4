package org.antlr.v4.test;

import org.junit.Test;

/** */
public class TestLexerDFAConstruction extends BaseTest {

	@Test public void unicode() throws Exception {
		String g =
			"lexer grammar L;\n" +
			"A : '\\u0030'..'\\u8000'+ 'a' ;\n" + // TODO: FAILS; \\u not converted
			"B : '\\u0020' ;";
		String expecting =
			"";
		checkLexerDFA(g, expecting);
	}

	@Test public void keywordvsID() throws Exception {
		String g =
			"lexer grammar L2;\n" +
			"IF : 'if' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"INT : DIGIT+ ;\n" +
			"public fragment\n" +
			"DIGIT : '0'..'9' ;";
		String expecting =
			":s1=> INT-{'0'..'9'}->:s1=> INT\n" +
			":s2=> ID-{'a'..'z'}->:s2=> ID\n" +
			":s3=> ID-'f'->:s4=> IF ID\n" +
			":s3=> ID-{'a'..'e', 'g'..'z'}->:s2=> ID\n" +
			":s4=> IF ID-{'a'..'z'}->:s2=> ID\n" +
			"s0-'i'->:s3=> ID\n" +
			"s0-{'0'..'9'}->:s1=> INT\n" +
			"s0-{'a'..'h', 'j'..'z'}->:s2=> ID\n";
		checkLexerDFA(g, expecting);
	}

	@Test public void recursiveMatchingTwoAlts() throws Exception {
		// ambig with ACTION; accept state will try both after matching
		// since one is recursive
		String g =
			"lexer grammar L3;\n" +
			"SPECIAL : '{{}}' ;\n" +
			"ACTION : '{' (FOO | 'x')* '}' ;\n" +
			"fragment\n" +
			"FOO : ACTION ;\n" +
			"LCURLY : '{' ;";
		String expecting =
			":s1=> LCURLY-'x'->s4\n" +
			":s1=> LCURLY-'{'->s3\n" +
			":s1=> LCURLY-'}'->:s2=> ACTION\n" +
			"s0-'{'->:s1=> LCURLY\n" +
			"s3-'x'->s6\n" +
			"s3-'}'->s5\n" +
			"s4-'x'->s4\n" +
			"s4-'{'->s7\n" +
			"s4-'}'->:s2=> ACTION\n" +
			"s5-'x'->s4\n" +
			"s5-'{'->s7\n" +
			"s5-'}'->:s8=> SPECIAL ACTION\n" +  // order meaningful here: SPECIAL ACTION
			"s6-'x'->s6\n" +
			"s6-'}'->s9\n" +
			"s7-'x'->s6\n" +
			"s7-'}'->s9\n" +
			"s9-'x'->s4\n" +
			"s9-'{'->s7\n" +
			"s9-'}'->:s2=> ACTION\n";
		checkLexerDFA(g, expecting);
	}

	@Test public void testAplusNonGreedy() throws Exception {
		String g =
			"lexer grammar t;\n"+
			"A : (options {greedy=false;}:'0'..'9')+ '000' ;\n";
		String expecting =
			"\n";
		checkLexerDFA(g, expecting);
	}

	@Test public void testDotNonGreedy() throws Exception {
		String g =
			"lexer grammar t;\n"+
			"A : (options {greedy=false;}:.)+ '000' ;\n";
		String expecting =
			"\n";
		checkLexerDFA(g, expecting);
	}



	public void _template() throws Exception {
		String g =
			"";
		String expecting =
			"";
		checkLexerDFA(g, expecting);
	}
	
}
