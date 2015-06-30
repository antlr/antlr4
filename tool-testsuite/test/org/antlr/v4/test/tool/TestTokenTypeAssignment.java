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

import org.antlr.v4.runtime.Token;
import org.antlr.v4.test.runtime.java.BaseTest;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.LexerGrammar;
import org.junit.Test;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.StringTokenizer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
				"  C,\n" +
				"  D" +
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
				"  C,\n" +
				"  D" +
				"}\n"+
				"A : 'a';\n" +
				"C : 'c' ;");
		String rules = "A, C";
		String tokenNames = "A, C, D";
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

		String literals = "['x']";
		String foundLiterals = g.stringLiteralToTypeMap.keySet().toString();
		assertEquals(literals, foundLiterals);

		foundLiterals = g.implicitLexer.stringLiteralToTypeMap.keySet().toString();
		assertEquals("['x']", foundLiterals); // pushed in lexer from parser

		String[] typeToTokenName = g.getTokenDisplayNames();
		Set<String> tokens = new LinkedHashSet<String>();
		for (String t : typeToTokenName) if ( t!=null ) tokens.add(t);
		assertEquals("[<INVALID>, 'x', E]", tokens.toString());
	}

	@Test public void testPredDoesNotHideNameToLiteralMapInLexer() throws Exception {
		// 'x' is token and char in lexer rule
		Grammar g = new Grammar(
				"grammar t;\n" +
				"a : 'x' X ; \n" +
				"X: 'x' {true}?;\n"); // must match as alias even with pred

		assertEquals("{'x'=1}", g.stringLiteralToTypeMap.toString());
		assertEquals("{EOF=-1, X=1}", g.tokenNameToTypeMap.toString());

		// pushed in lexer from parser
		assertEquals("{'x'=1}", g.implicitLexer.stringLiteralToTypeMap.toString());
		assertEquals("{EOF=-1, X=1}", g.implicitLexer.tokenNameToTypeMap.toString());
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
		Set<?> literals = g.stringLiteralToTypeMap.keySet();
		// must store literals how they appear in the antlr grammar
		assertEquals("'\\n'", literals.toArray()[0]);
	}

	protected void checkSymbols(Grammar g,
								String rulesStr,
								String allValidTokensStr)
		throws Exception
	{
		String[] typeToTokenName = g.getTokenNames();
		Set<String> tokens = new HashSet<String>();
		for (int i = 0; i < typeToTokenName.length; i++) {
			String t = typeToTokenName[i];
			if ( t!=null ) {
				if (t.startsWith(Grammar.AUTO_GENERATED_TOKEN_NAME_PREFIX)) {
					tokens.add(g.getTokenDisplayName(i));
				}
				else {
					tokens.add(t);
				}
			}
		}

		// make sure expected tokens are there
		StringTokenizer st = new StringTokenizer(allValidTokensStr, ", ");
		while ( st.hasMoreTokens() ) {
			String tokenName = st.nextToken();
			assertTrue("token "+tokenName+" expected, but was undefined",
					   g.getTokenType(tokenName) != Token.INVALID_TYPE);
			tokens.remove(tokenName);
		}
		// make sure there are not any others (other than <EOF> etc...)
		for (String tokenName : tokens) {
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
