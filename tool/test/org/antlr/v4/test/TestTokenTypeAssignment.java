package org.antlr.v4.test;

import org.antlr.v4.Tool;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.tool.*;
import org.junit.Test;

import java.util.*;

public class TestTokenTypeAssignment extends BaseTest {

	@Test
		public void testParserSimpleTokens() throws Exception {
		Grammar g = new Grammar(
				"parser grammar t;\n"+
				"a : A | B;\n" +
				"b : C ;");
		String rules = "a, b";
		String tokenNames = "A, B, C";
		checkSymbols(g, rules, tokenNames);
	}

	@Test public void testParserTokensSection() throws Exception {
		Grammar g = new Grammar(
				"parser grammar t;\n" +
				"tokens {\n" +
				"  C;\n" +
				"  D;" +
				"}\n"+
				"a : A | B;\n" +
				"b : C ;");
		String rules = "a, b";
		String tokenNames = "A, B, C, D";
		checkSymbols(g, rules, tokenNames);
	}

	@Test public void testLexerTokensSection() throws Exception {
		LexerGrammar g = new LexerGrammar(
				"lexer grammar t;\n" +
				"tokens {\n" +
				"  C;\n" +
				"  D;" +
				"}\n"+
				"A : 'a';\n" +
				"C : 'c' ;");
		String rules = "A, C";
		String tokenNames = "A, C, D";
		checkSymbols(g, rules, tokenNames);
	}

	@Test public void testTokensSectionWithAssignmentSection() throws Exception {
		Grammar g = new Grammar(
				"grammar t;\n" +
				"tokens {\n" +
				"  C='c';\n" +
				"  D;" +
				"}\n"+
				"a : A | B;\n" +
				"b : C ;");
		String rules = "a, b";
		String tokenNames = "A, B, C, D, 'c'";
		checkSymbols(g, rules, tokenNames);
	}

	@Test public void testCombinedGrammarLiterals() throws Exception {
		Grammar g = new Grammar(
				"grammar t;\n"+
				"a : 'begin' b 'end';\n" +
				"b : C ';' ;\n" +
				"ID : 'a' ;\n" +
				"FOO : 'foo' ;\n" +  // "foo" is not a token name
				"C : 'c' ;\n");        // nor is 'c'
		String rules = "a, b";
		String tokenNames = "C, FOO, ID, 'begin', 'end', ';'";
		checkSymbols(g, rules, tokenNames);
	}

	@Test public void testLiteralInParserAndLexer() throws Exception {
		// 'x' is token and char in lexer rule
		Grammar g = new Grammar(
				"grammar t;\n" +
				"a : 'x' E ; \n" +
				"E: 'x' '0' ;\n");

		Tool antlr = new Tool();
		antlr.process(g);

		String literals = "['x']";
		String foundLiterals = g.stringLiteralToTypeMap.keySet().toString();
		assertEquals(literals, foundLiterals);

		foundLiterals = g.implicitLexer.stringLiteralToTypeMap.keySet().toString();
		assertEquals("['x']", foundLiterals); // pushed in lexer from parser

		String[] typeToTokenName = g.getTokenNames();
		Set<String> tokens = new HashSet<String>();
		for (String t : typeToTokenName) if ( t!=null ) tokens.add(t);
		assertEquals("[E]", tokens.toString());
	}

	@Test public void testCombinedGrammarWithRefToLiteralButNoTokenIDRef() throws Exception {
		Grammar g = new Grammar(
				"grammar t;\n"+
				"a : 'a' ;\n" +
				"A : 'a' ;\n");
		String rules = "a";
		String tokenNames = "A, 'a'";
		checkSymbols(g, rules, tokenNames);
	}

	@Test public void testSetDoesNotMissTokenAliases() throws Exception {
		Grammar g = new Grammar(
				"grammar t;\n"+
				"a : 'a'|'b' ;\n" +
				"A : 'a' ;\n" +
				"B : 'b' ;\n");
		String rules = "a";
		String tokenNames = "A, 'a', B, 'b'";
		checkSymbols(g, rules, tokenNames);
	}

	// T E S T  L I T E R A L  E S C A P E S

	@Test public void testParserCharLiteralWithEscape() throws Exception {
		Grammar g = new Grammar(
				"grammar t;\n"+
				"a : '\\n';\n");
		Tool antlr = new Tool();
		antlr.process(g);
		Set literals = g.stringLiteralToTypeMap.keySet();
		// must store literals how they appear in the antlr grammar
		assertEquals("'\\n'", literals.toArray()[0]);
	}

	@Test public void testTokenInTokensSectionAndTokenRuleDef() throws Exception {
		// this must return A not I to the parser; calling a nonfragment rule
		// from a nonfragment rule does not set the overall token.
		String grammar =
			"grammar P;\n" +
			"tokens { B='}'; }\n"+
			"a : A B {System.out.println(state.input);} ;\n"+
			"A : 'a' ;\n" +
			"B : '}' ;\n"+
			"WS : (' '|'\\n') {skip();} ;";
		String found = execParser("P.g", grammar, "PParser", "PLexer",
								  "a", "a}", false);
		assertEquals("a}\n", found);
	}

	@Test public void testTokenInTokensSectionAndTokenRuleDef2() throws Exception {
		// this must return A not I to the parser; calling a nonfragment rule
		// from a nonfragment rule does not set the overall token.
		String grammar =
			"grammar P;\n" +
			"tokens { B='}'; }\n"+
			"a : A '}' {System.out.println(state.input);} ;\n"+
			"A : 'a' ;\n" +
			"B : '}' {/* */} ;\n"+
			"WS : (' '|'\\n') {skip();} ;";
		String found = execParser("P.g", grammar, "PParser", "PLexer",
								  "a", "a}", false);
		assertEquals("a}\n", found);
	}

	protected void checkSymbols(Grammar g,
								String rulesStr,
								String tokensStr)
		throws Exception
	{
		Tool antlr = new Tool();
		antlr.process(g);

		String[] typeToTokenName = g.getTokenNames();
		Set<String> tokens = new HashSet<String>();
		for (String t : typeToTokenName) if ( t!=null ) tokens.add(t);

		// make sure expected tokens are there
		StringTokenizer st = new StringTokenizer(tokensStr, ", ");
		while ( st.hasMoreTokens() ) {
			String tokenName = st.nextToken();
			assertTrue("token "+tokenName+" expected, but was undefined",
					   g.getTokenType(tokenName) != Token.INVALID_TYPE);
			tokens.remove(tokenName);
		}
		// make sure there are not any others (other than <EOF> etc...)
		for (Iterator iter = tokens.iterator(); iter.hasNext();) {
			String tokenName = (String) iter.next();
			assertTrue("unexpected token name "+tokenName,
					   g.getTokenType(tokenName) < Token.MIN_USER_TOKEN_TYPE);
		}

		// make sure all expected rules are there
		st = new StringTokenizer(rulesStr, ", ");
		int n = 0;
		while ( st.hasMoreTokens() ) {
			String ruleName = st.nextToken();
			assertNotNull("rule "+ruleName+" expected", g.getRule(ruleName));
			n++;
		}
		//System.out.println("rules="+rules);
		// make sure there are no extra rules
		assertEquals("number of rules mismatch; expecting "+n+"; found "+g.rules.size(),
					 n, g.rules.size());

	}

}
