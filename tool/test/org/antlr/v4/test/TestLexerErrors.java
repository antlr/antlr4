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

package org.antlr.v4.test;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestLexerErrors extends BaseTest {
	// TEST DETECTION
	@Test public void testInvalidCharAtStart() throws Exception {
		String grammar =
			"lexer grammar L;\n" +
			"A : 'a' 'b' ;\n";
		String tokens = execLexer("L.g4", grammar, "L", "x");
		String expectingTokens =
			"[@0,1:0='<EOF>',<-1>,1:1]\n";
		assertEquals(expectingTokens, tokens);
		String expectingError = "line 1:0 token recognition error at: 'x'\n";
		String error = stderrDuringParse;
		assertEquals(expectingError, error);
	}

	@Test
	public void testStringsEmbeddedInActions() {
		String grammar =
			"lexer grammar Actions;\n"
			+ "ACTION2 : '[' (STRING | ~'\"')*? ']';\n"
			+ "STRING : '\"' ('\\\"' | .)*? '\"';\n"
			+ "WS : [ \\t\\r\\n]+ -> skip;\n";
		String tokens = execLexer("Actions.g4", grammar, "Actions", "[\"foo\"]");
		String expectingTokens =
			"[@0,0:6='[\"foo\"]',<1>,1:0]\n" +
			"[@1,7:6='<EOF>',<-1>,1:7]\n";
		assertEquals(expectingTokens, tokens);
		assertNull(stderrDuringParse);

		tokens = execLexer("Actions.g4", grammar, "Actions", "[\"foo]");
		expectingTokens =
			"[@0,6:5='<EOF>',<-1>,1:6]\n";
		assertEquals(expectingTokens, tokens);
		assertEquals("line 1:0 token recognition error at: '[\"foo]'\n", stderrDuringParse);
	}

	@Test public void testEnforcedGreedyNestedBrances() {
		String grammar =
			"lexer grammar R;\n"
			+ "ACTION : '{' (ACTION | ~[{}])* '}';\n"
			+ "WS : [ \\r\\n\\t]+ -> skip;\n";
		String tokens = execLexer("R.g4", grammar, "R", "{ { } }");
		String expectingTokens =
			"[@0,0:6='{ { } }',<1>,1:0]\n" +
			"[@1,7:6='<EOF>',<-1>,1:7]\n";
		assertEquals(expectingTokens, tokens);
		assertEquals(null, stderrDuringParse);

		tokens = execLexer("R.g4", grammar, "R", "{ { }");
		expectingTokens =
			"[@0,5:4='<EOF>',<-1>,1:5]\n";
		assertEquals(expectingTokens, tokens);
		assertEquals("line 1:0 token recognition error at: '{ { }'\n", stderrDuringParse);
	}

	@Test public void testInvalidCharAtStartAfterDFACache() throws Exception {
		String grammar =
			"lexer grammar L;\n" +
			"A : 'a' 'b' ;\n";
		String tokens = execLexer("L.g4", grammar, "L", "abx");
		String expectingTokens =
			"[@0,0:1='ab',<1>,1:0]\n" +
			"[@1,3:2='<EOF>',<-1>,1:3]\n";
		assertEquals(expectingTokens, tokens);
		String expectingError = "line 1:2 token recognition error at: 'x'\n";
		String error = stderrDuringParse;
		assertEquals(expectingError, error);
	}

	@Test public void testInvalidCharInToken() throws Exception {
		String grammar =
			"lexer grammar L;\n" +
			"A : 'a' 'b' ;\n";
		String tokens = execLexer("L.g4", grammar, "L", "ax");
		String expectingTokens =
			"[@0,2:1='<EOF>',<-1>,1:2]\n";
		assertEquals(expectingTokens, tokens);
		String expectingError = "line 1:0 token recognition error at: 'ax'\n";
		String error = stderrDuringParse;
		assertEquals(expectingError, error);
	}

	@Test public void testInvalidCharInTokenAfterDFACache() throws Exception {
		String grammar =
			"lexer grammar L;\n" +
			"A : 'a' 'b' ;\n";
		String tokens = execLexer("L.g4", grammar, "L", "abax");
		String expectingTokens =
			"[@0,0:1='ab',<1>,1:0]\n" +
			"[@1,4:3='<EOF>',<-1>,1:4]\n";
		assertEquals(expectingTokens, tokens);
		String expectingError = "line 1:2 token recognition error at: 'ax'\n";
		String error = stderrDuringParse;
		assertEquals(expectingError, error);
	}

	@Test public void testDFAToATNThatFailsBackToDFA() throws Exception {
		String grammar =
			"lexer grammar L;\n" +
			"A : 'ab' ;\n"+
			"B : 'abc' ;\n";
		// The first ab caches the DFA then abx goes through the DFA but
		// into the ATN for the x, which fails. Must go back into DFA
		// and return to previous dfa accept state
		String tokens = execLexer("L.g4", grammar, "L", "ababx");
		String expectingTokens =
			"[@0,0:1='ab',<1>,1:0]\n" +
			"[@1,2:3='ab',<1>,1:2]\n" +
			"[@2,5:4='<EOF>',<-1>,1:5]\n";
		assertEquals(expectingTokens, tokens);
		String expectingError = "line 1:4 token recognition error at: 'x'\n";
		String error = stderrDuringParse;
		assertEquals(expectingError, error);
	}

	@Test public void testDFAToATNThatMatchesThenFailsInATN() throws Exception {
		String grammar =
			"lexer grammar L;\n" +
			"A : 'ab' ;\n"+
			"B : 'abc' ;\n"+
			"C : 'abcd' ;\n";
		// The first ab caches the DFA then abx goes through the DFA but
		// into the ATN for the c.  It marks that hasn't except state
		// and then keeps going in the ATN. It fails on the x, but
		// uses the previous accepted in the ATN not DFA
		String tokens = execLexer("L.g4", grammar, "L", "ababcx");
		String expectingTokens =
			"[@0,0:1='ab',<1>,1:0]\n" +
			"[@1,2:4='abc',<2>,1:2]\n" +
			"[@2,6:5='<EOF>',<-1>,1:6]\n";
		assertEquals(expectingTokens, tokens);
		String expectingError = "line 1:5 token recognition error at: 'x'\n";
		String error = stderrDuringParse;
		assertEquals(expectingError, error);
	}

	@Test public void testErrorInMiddle() throws Exception {
		String grammar =
			"lexer grammar L;\n" +
			"A : 'abc' ;\n";
		String tokens = execLexer("L.g4", grammar, "L", "abx");
		String expectingTokens =
			"[@0,3:2='<EOF>',<-1>,1:3]\n";
		assertEquals(expectingTokens, tokens);
		String expectingError = "line 1:0 token recognition error at: 'abx'\n";
		String error = stderrDuringParse;
		assertEquals(expectingError, error);
	}

	// TEST RECOVERY

	/**
	 * This is a regression test for #45 "NullPointerException in LexerATNSimulator.execDFA".
	 * https://github.com/antlr/antlr4/issues/46
	 */
	@Test
	public void testLexerExecDFA() throws Exception {
		String grammar =
			"grammar T;\n" +
			"start : ID ':' expr;\n" +
			"expr : primary expr? {} | expr '->' ID;\n" +
			"primary : ID;\n" +
			"ID : [a-z]+;\n" +
			"\n";
		String result = execLexer("T.g4", grammar, "TLexer", "x : x", false);
		String expecting =
			"[@0,0:0='x',<3>,1:0]\n" +
			"[@1,2:2=':',<1>,1:2]\n" +
			"[@2,4:4='x',<3>,1:4]\n" +
			"[@3,5:4='<EOF>',<-1>,1:5]\n";
		assertEquals(expecting, result);
		assertEquals("line 1:1 token recognition error at: ' '\n" +
					 "line 1:3 token recognition error at: ' '\n",
					 this.stderrDuringParse);
	}

}
