package org.antlr.v4.test;

import org.junit.Test;

public class TestLexerDFAConstruction extends BaseTest {

	@Test public void unicode() throws Exception {
		String g =
			"lexer grammar L;\n" +
			"A : '\\u0030'..'\\u8000'+ 'a' ;\n" +
			"B : '\\u0020' ;";
		String expecting =
			"s0-{'0'..'\\u8000'}->s1\n" +
			"s0-' '->:s2=>B\n" +
			"s1-'a'->:s3=>A\n" +
			"s1-{'0'..'`', 'b'..'\\u8000'}->s1\n" +
			":s3=>A-'a'->:s3=>A\n" +
			":s3=>A-{'0'..'`', 'b'..'\\u8000'}->s1\n";
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
			"s0-'i'->:s1=>ID\n" +
			"s0-{'a'..'h', 'j'..'z'}->:s2=>ID\n" +
			"s0-{'0'..'9'}->:s3=>INT\n" +
			":s1=>ID-'f'->:s4=>IF\n" +
			":s1=>ID-{'a'..'e', 'g'..'z'}->:s2=>ID\n" +
			":s2=>ID-{'a'..'z'}->:s2=>ID\n" +
			":s3=>INT-{'0'..'9'}->:s3=>INT\n" +
			":s4=>IF-{'a'..'z'}->:s2=>ID\n";
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
			"s0-'b'->:s1=>B\n" +
			"s0-'c'->:s2=>C\n";
		checkLexerDFA(g, "FOO", expecting);
	}

	@Test public void pred() throws Exception {
		String g =
			"lexer grammar L;\n" +
			"A : {p1}? 'a' 'b' ;\n" +
			"B :  'a' 'b'  ;";
		String expecting =
			"s0-'a'->s1\n" +
			"s1-'b'->s2\n" +
			"s2-{p1}?->:s3=>A\n" +
			"s2-true->:s4=>B\n";
		checkLexerDFA(g, expecting);
	}

	@Test public void gated_pred() throws Exception {
		String g =
			"lexer grammar pred;\n" +
			"A : {p1}?=> 'a' 'b'\n" +
			"  | 'a' 'c' \n" +
			"  | 'b'\n" +
			"  ;";
		String expecting =
			"s0-'a'->s1\n" +
			"s0-'b'->:s2=>A\n" +
			"s1-'b'&&{p1}?->:s3=>A\n" +
			"s1-'c'->:s4=>A\n";
		checkLexerDFA(g, expecting);
	}

	@Test public void gated_pred2() throws Exception {
		String g =
			"lexer grammar T;\n" +
			"A : {p1}?=> 'a' 'b'\n" +
			"  | 'b'\n" +
			"  ;\n" +
			"B : 'a' 'c' ;";
		String expecting =
			"s0-'a'->s1\n" +
			"s0-'b'->:s2=>A\n" +
			"s1-'b'&&{p1}?->:s3=>A\n" +
			"s1-'c'->:s4=>B\n";
		checkLexerDFA(g, expecting);
	}

	@Test public void ambigButPredicatedTokens() throws Exception {
		String g =
			"lexer grammar L4;\n" +
			"A : {p1}? 'a' ; \n" +
			"B : {p2}? 'a' ;";
		String expecting =
			"s0-'a'->s1\n" +
			"s1-{p1}?->:s2=>A\n" +
			"s1-{p2}?->:s3=>B\n";
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
