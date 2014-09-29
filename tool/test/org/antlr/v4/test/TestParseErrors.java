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

import org.antlr.v4.runtime.atn.ATNSerializer;
import org.junit.Test;

import static org.junit.Assert.*;

/** test runtime parse errors */
public class TestParseErrors extends BaseTest {
	@Test public void testTokenMismatch() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : 'a' 'b' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "aa", false);
		String expecting = "line 1:1 mismatched input 'a' expecting 'b'\n";
		String result = stderrDuringParse;
		assertEquals(expecting, result);
	}

	@Test public void testSingleTokenDeletion() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : 'a' 'b' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "aab", false);
		String expecting = "line 1:1 extraneous input 'a' expecting 'b'\n";
		String result = stderrDuringParse;
		assertEquals(expecting, result);
	}

	@Test public void testSingleTokenDeletionExpectingSet() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : 'a' ('b'|'c') ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "aab", false);
		String expecting = "line 1:1 extraneous input 'a' expecting {'b', 'c'}\n";
		String result = stderrDuringParse;
		assertEquals(expecting, result);
	}

	@Test public void testSingleTokenInsertion() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : 'a' 'b' 'c' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "ac", false);
		String expecting = "line 1:1 missing 'b' at 'c'\n";
		String result = stderrDuringParse;
		assertEquals(expecting, result);
	}

	@Test public void testConjuringUpToken() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : 'a' x='b' {System.out.println(\"conjured=\"+$x);} 'c' ;";
		String result = execParser("T.g4", grammar, "TParser", "TLexer", "a", "ac", false);
		String expecting = "conjured=[@-1,-1:-1='<missing 'b'>',<2>,1:1]\n";
		assertEquals(expecting, result);
	}

	@Test public void testSingleSetInsertion() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : 'a' ('b'|'c') 'd' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "ad", false);
		String expecting = "line 1:1 missing {'b', 'c'} at 'd'\n";
		String result = stderrDuringParse;
		assertEquals(expecting, result);
	}

	@Test public void testConjuringUpTokenFromSet() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : 'a' x=('b'|'c') {System.out.println(\"conjured=\"+$x);} 'd' ;";
		String result = execParser("T.g4", grammar, "TParser", "TLexer", "a", "ad", false);
		String expecting = "conjured=[@-1,-1:-1='<missing 'b'>',<2>,1:1]\n";
		assertEquals(expecting, result);
	}

	@Test public void testLL2() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : 'a' 'b'" +
			"  | 'a' 'c'" +
			";\n" +
			"q : 'e' ;\n";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "ae", false);
		String expecting = "line 1:1 no viable alternative at input 'ae'\n";
		String result = stderrDuringParse;
		assertEquals(expecting, result);
	}

	@Test public void testLL3() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : 'a' 'b'* 'c'" +
			"  | 'a' 'b' 'd'" +
			"  ;\n" +
			"q : 'e' ;\n";
		System.out.println(grammar);
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "abe", false);
		String expecting = "line 1:2 no viable alternative at input 'abe'\n";
		String result = stderrDuringParse;
		assertEquals(expecting, result);
	}

	@Test public void testLLStar() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : 'a'+ 'b'" +
			"  | 'a'+ 'c'" +
			";\n" +
			"q : 'e' ;\n";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "aaae", false);
		String expecting = "line 1:3 no viable alternative at input 'aaae'\n";
		String result = stderrDuringParse;
		assertEquals(expecting, result);
	}

	@Test public void testSingleTokenDeletionBeforeLoop() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : 'a' 'b'*;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "aabc", false);
		String expecting = "line 1:1 extraneous input 'a' expecting {<EOF>, 'b'}\n" +
			"line 1:3 token recognition error at: 'c'\n";
		String result = stderrDuringParse;
		assertEquals(expecting, result);
	}

	@Test public void testMultiTokenDeletionBeforeLoop() throws Exception {
		// can only delete 1 before loop
		String grammar =
			"grammar T;\n" +
			"a : 'a' 'b'* 'c';";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "aacabc", false);
		String expecting =
			"line 1:1 extraneous input 'a' expecting {'b', 'c'}\n";
		String result = stderrDuringParse;
		assertEquals(expecting, result);
	}

	@Test public void testSingleTokenDeletionDuringLoop() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : 'a' 'b'* 'c' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "ababbc", false);
		String expecting = "line 1:2 extraneous input 'a' expecting {'b', 'c'}\n";
		String result = stderrDuringParse;
		assertEquals(expecting, result);
	}

	@Test public void testMultiTokenDeletionDuringLoop() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : 'a' 'b'* 'c' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "abaaababc", false);
		String expecting =
				"line 1:2 extraneous input 'a' expecting {'b', 'c'}\n" +
				"line 1:6 extraneous input 'a' expecting {'b', 'c'}\n";
		String result = stderrDuringParse;
		assertEquals(expecting, result);
	}

	// ------

	@Test public void testSingleTokenDeletionBeforeLoop2() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : 'a' ('b'|'z'{;})*;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "aabc", false);
		String expecting = "line 1:1 extraneous input 'a' expecting {<EOF>, 'b', 'z'}\n" +
			"line 1:3 token recognition error at: 'c'\n";
		String result = stderrDuringParse;
		assertEquals(expecting, result);
	}

	@Test public void testMultiTokenDeletionBeforeLoop2() throws Exception {
		// can only delete 1 before loop
		String grammar =
			"grammar T;\n" +
			"a : 'a' ('b'|'z'{;})* 'c';";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "aacabc", false);
		String expecting =
			"line 1:1 extraneous input 'a' expecting {'b', 'z', 'c'}\n";
		String result = stderrDuringParse;
		assertEquals(expecting, result);
	}

	@Test public void testSingleTokenDeletionDuringLoop2() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : 'a' ('b'|'z'{;})* 'c' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "ababbc", false);
		String expecting = "line 1:2 extraneous input 'a' expecting {'b', 'z', 'c'}\n";
		String result = stderrDuringParse;
		assertEquals(expecting, result);
	}

	@Test public void testMultiTokenDeletionDuringLoop2() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : 'a' ('b'|'z'{;})* 'c' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "abaaababc", false);
		String expecting =
				"line 1:2 extraneous input 'a' expecting {'b', 'z', 'c'}\n" +
				"line 1:6 extraneous input 'a' expecting {'b', 'z', 'c'}\n";
		String result = stderrDuringParse;
		assertEquals(expecting, result);
	}

	@Test public void testLL1ErrorInfo() throws Exception {
		String grammar =
			"grammar T;\n" +
			"start : animal (AND acClass)? service EOF;\n" +
			"animal : (DOG | CAT );\n" +
			"service : (HARDWARE | SOFTWARE) ;\n" +
			"AND : 'and';\n" +
			"DOG : 'dog';\n" +
			"CAT : 'cat';\n" +
			"HARDWARE: 'hardware';\n" +
			"SOFTWARE: 'software';\n" +
			"WS : ' ' -> skip ;" +
			"acClass\n" +
			"@init\n" +
			"{ System.out.println(getExpectedTokens().toString(tokenNames)); }\n" +
			"  : ;\n";
		String result = execParser("T.g4", grammar, "TParser", "TLexer", "start", "dog and software", false);
		String expecting = "{'hardware', 'software'}\n";
		assertEquals(expecting, result);
	}

	/**
	 * This is a regression test for #6 "NullPointerException in getMissingSymbol".
	 * https://github.com/antlr/antlr4/issues/6
	 */
	@Test
	public void testInvalidEmptyInput() throws Exception {
		String grammar =
			"grammar T;\n" +
			"start : ID+;\n" +
			"ID : [a-z]+;\n" +
			"\n";
		String result = execParser("T.g4", grammar, "TParser", "TLexer", "start", "", true);
		String expecting = "";
		assertEquals(expecting, result);
		assertEquals("line 1:0 missing ID at '<EOF>'\n", this.stderrDuringParse);
	}

	/**
	 * Regression test for "Getter for context is not a list when it should be".
	 * https://github.com/antlr/antlr4/issues/19
	 */
	@Test
	public void testContextListGetters() throws Exception {
		String grammar =
			"grammar T;\n" +
			"@parser::members{\n" +
			"  void foo() {\n" +
			"    SContext s = null;\n" +
			"    List<? extends AContext> a = s.a();\n" +
			"    List<? extends BContext> b = s.b();\n" +
			"  }\n" +
			"}\n" +
			"s : (a | b)+;\n" +
			"a : 'a' {System.out.print('a');};\n" +
			"b : 'b' {System.out.print('b');};\n" +
			"";
		String result = execParser("T.g", grammar, "TParser", "TLexer", "s", "abab", true);
		String expecting = "abab\n";
		assertEquals(expecting, result);
		assertNull(this.stderrDuringParse);
	}

	/**
	 * This is a regression test for #26 "an exception upon simple rule with double recursion in an alternative".
	 * https://github.com/antlr/antlr4/issues/26
	 */
	@Test
	public void testDuplicatedLeftRecursiveCall() throws Exception {
		String grammar =
			"grammar T;\n" +
			"start : expr EOF;\n" +
			"expr : 'x'\n" +
			"     | expr expr\n" +
			"     ;\n" +
			"\n";

		String result = execParser("T.g4", grammar, "TParser", "TLexer", "start", "x", true);
		assertEquals("", result);
		assertNull(this.stderrDuringParse);

		result = execParser("T.g4", grammar, "TParser", "TLexer", "start", "xx", true);
		assertEquals("", result);
		assertNull(this.stderrDuringParse);

		result = execParser("T.g4", grammar, "TParser", "TLexer", "start", "xxx", true);
		assertEquals("", result);
		assertNull(this.stderrDuringParse);

		result = execParser("T.g4", grammar, "TParser", "TLexer", "start", "xxxx", true);
		assertEquals("", result);
		assertNull(this.stderrDuringParse);
	}

	/**
	 * This is a regression test for #45 "NullPointerException in ATNConfig.hashCode".
	 * https://github.com/antlr/antlr4/issues/45
	 * <p/>
	 * The original cause of this issue was an error in the tool's ATN state optimization,
	 * which is now detected early in {@link ATNSerializer} by ensuring that all
	 * serialized transitions point to states which were not removed.
	 */
	@Test
	public void testInvalidATNStateRemoval() throws Exception {
		String grammar =
			"grammar T;\n" +
			"start : ID ':' expr;\n" +
			"expr : primary expr? {} | expr '->' ID;\n" +
			"primary : ID;\n" +
			"ID : [a-z]+;\n" +
			"\n";
		String result = execParser("T.g4", grammar, "TParser", "TLexer", "start", "x:x", true);
		String expecting = "";
		assertEquals(expecting, result);
		assertNull(this.stderrDuringParse);
	}

	@Test public void testNoViableAltAvoidance() throws Exception {
		// "a." matches 'a' to rule e but then realizes '.' won't match.
		// previously would cause noviablealt. now prediction pretends to
		// have "a' predict 2nd alt of e. Will get syntax error later so
		// let it get farther.
		String grammar =
			"grammar T;\n" +
			"s : e '!' ;\n" +
			"e : 'a' 'b'\n" +
			"  | 'a'\n" +
			"  ;\n" +
			"DOT : '.' ;\n" +
			"WS : [ \\t\\r\\n]+ -> skip;\n";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", "a.", false);
		String expecting =
				"line 1:1 mismatched input '.' expecting '!'\n";
		String result = stderrDuringParse;
		assertEquals(expecting, result);
	}
}
