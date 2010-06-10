package org.antlr.v4.test;

import org.junit.Test;

public class TestLexerDFAConstruction extends BaseTest {

	@Test public void unicode() throws Exception {
		String g =
			"lexer grammar L;\n" +
			"A : '\\u0030'..'\\u8000'+ 'a' ;\n" + // TODO: FAILS; \\u not converted
			"B : '\\u0020' ;";
		String expecting =
			"s0-{'0'..'\\u8000'}->s1\n" +
			"s0-' '->:s2=> B\n" +
			"s1-'a'->:s3=> A\n" +
			"s1-{'0'..'`', 'b'..'\\u8000'}->s1\n" +
			":s3=> A-'a'->:s3=> A\n" +
			":s3=> A-{'0'..'`', 'b'..'\\u8000'}->s1\n";
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
			"s0-'i'->:s1=> ID\n" +
			"s0-{'a'..'h', 'j'..'z'}->:s2=> ID\n" +
			"s0-{'0'..'9'}->:s3=> INT\n" +
			":s1=> ID-'f'->:s4=> IF ID\n" +
			":s1=> ID-{'a'..'e', 'g'..'z'}->:s2=> ID\n" +
			":s2=> ID-{'a'..'z'}->:s2=> ID\n" +
			":s3=> INT-{'0'..'9'}->:s3=> INT\n" +
			":s4=> IF ID-{'a'..'z'}->:s2=> ID\n";
		checkLexerDFA(g, expecting);
	}

	@Test public void recursiveMatchingTwoAlts() throws Exception {
		// TODO: recursion requires NFA
		String g =
			"lexer grammar L3;\n" +
			"SPECIAL : '{{}}' ;\n" +
			"ACTION : '{' (FOO | 'x')* '}' ;\n" +
			"fragment\n" +
			"FOO : ACTION ;\n" +
			"LCURLY : '{' ;";
		String expecting =
			"";
		checkLexerDFA(g, expecting);
	}

	@Test public void testMode() throws Exception {
		String g =
			"lexer grammar L;\n"+
			"A : 'a' ;\n" +
			"X : 'x' ;\n" +
			"mode FOO;\n" +
			"B : 'b' ;\n" +
			"C : 'c' ;\n";
		String expecting =
			"s0-'b'->:s1=> B\n" +
			"s0-'c'->:s2=> C\n";
		checkLexerDFA(g, "FOO", expecting);
	}


	public void _template() throws Exception {
		String g =
			"";
		String expecting =
			"";
		checkLexerDFA(g, expecting);
	}
	
}
