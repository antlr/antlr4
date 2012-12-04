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

/** Test the set stuff in lexer and parser */
public class TestSets extends BaseTest {
	protected boolean debug = false;

	/** Public default constructor used by TestRig */
	public TestSets() {
	}

	@Test public void testSeqDoesNotBecomeSet() throws Exception {
		// this must return A not I to the parser; calling a nonfragment rule
		// from a nonfragment rule does not set the overall token.
		String grammar =
			"grammar P;\n" +
			"a : C {System.out.println(_input.getText());} ;\n" +
			"fragment A : '1' | '2';\n" +
			"fragment B : '3' '4';\n" +
			"C : A | B;\n";
		String found = execParser("P.g4", grammar, "PParser", "PLexer",
								  "a", "34", debug);
		assertEquals("34\n", found);
	}

	@Test public void testParserSet() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : t=('x'|'y') {System.out.println($t.text);} ;\n";
		String found = execParser("T.g4", grammar, "TParser", "TLexer",
								  "a", "x", debug);
		assertEquals("x\n", found);
	}

	@Test public void testParserNotSet() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : t=~('x'|'y') 'z' {System.out.println($t.text);} ;\n";
		String found = execParser("T.g4", grammar, "TParser", "TLexer",
								  "a", "zz", debug);
		assertEquals("z\n", found);
	}

	@Test public void testParserNotToken() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : ~'x' 'z' {System.out.println(_input.getText());} ;\n";
		String found = execParser("T.g4", grammar, "TParser", "TLexer",
								  "a", "zz", debug);
		assertEquals("zz\n", found);
	}

	@Test public void testParserNotTokenWithLabel() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : t=~'x' 'z' {System.out.println($t.text);} ;\n";
		String found = execParser("T.g4", grammar, "TParser", "TLexer",
								  "a", "zz", debug);
		assertEquals("z\n", found);
	}

	@Test public void testRuleAsSet() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a @after {System.out.println(_input.getText());} : 'a' | 'b' |'c' ;\n";
		String found = execParser("T.g4", grammar, "TParser", "TLexer",
								  "a", "b", debug);
		assertEquals("b\n", found);
	}

	@Test public void testNotChar() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : A {System.out.println($A.text);} ;\n" +
			"A : ~'b' ;\n";
		String found = execParser("T.g4", grammar, "TParser", "TLexer",
								  "a", "x", debug);
		assertEquals("x\n", found);
	}

	@Test public void testOptionalSingleElement() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : A? 'c' {System.out.println(_input.getText());} ;\n" +
			"A : 'b' ;\n";
		String found = execParser("T.g4", grammar, "TParser", "TLexer",
								  "a", "bc", debug);
		assertEquals("bc\n", found);
	}

	@Test public void testOptionalLexerSingleElement() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : A {System.out.println(_input.getText());} ;\n" +
			"A : 'b'? 'c' ;\n";
		String found = execParser("T.g4", grammar, "TParser", "TLexer",
								  "a", "bc", debug);
		assertEquals("bc\n", found);
	}

	@Test public void testStarLexerSingleElement() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : A {System.out.println(_input.getText());} ;\n" +
			"A : 'b'* 'c' ;\n";
		String found = execParser("T.g4", grammar, "TParser", "TLexer",
								  "a", "bbbbc", debug);
		assertEquals("bbbbc\n", found);
		found = execParser("T.g4", grammar, "TParser", "TLexer",
								  "a", "c", debug);
		assertEquals("c\n", found);
	}

	@Test public void testPlusLexerSingleElement() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : A {System.out.println(_input.getText());} ;\n" +
			"A : 'b'+ 'c' ;\n";
		String found = execParser("T.g4", grammar, "TParser", "TLexer",
								  "a", "bbbbc", debug);
		assertEquals("bbbbc\n", found);
	}

	@Test public void testOptionalSet() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : ('a'|'b')? 'c' {System.out.println(_input.getText());} ;\n";
		String found = execParser("T.g4", grammar, "TParser", "TLexer",
								  "a", "ac", debug);
		assertEquals("ac\n", found);
	}

	@Test public void testStarSet() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : ('a'|'b')* 'c' {System.out.println(_input.getText());} ;\n";
		String found = execParser("T.g4", grammar, "TParser", "TLexer",
								  "a", "abaac", debug);
		assertEquals("abaac\n", found);
	}

	@Test public void testPlusSet() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : ('a'|'b')+ 'c' {System.out.println(_input.getText());} ;\n";
		String found = execParser("T.g4", grammar, "TParser", "TLexer",
								  "a", "abaac", debug);
		assertEquals("abaac\n", found);
	}

	@Test public void testLexerOptionalSet() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : A {System.out.println(_input.getText());} ;\n" +
			"A : ('a'|'b')? 'c' ;\n";
		String found = execParser("T.g4", grammar, "TParser", "TLexer",
								  "a", "ac", debug);
		assertEquals("ac\n", found);
	}

	@Test public void testLexerStarSet() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : A {System.out.println(_input.getText());} ;\n" +
			"A : ('a'|'b')* 'c' ;\n";
		String found = execParser("T.g4", grammar, "TParser", "TLexer",
								  "a", "abaac", debug);
		assertEquals("abaac\n", found);
	}

	@Test public void testLexerPlusSet() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : A {System.out.println(_input.getText());} ;\n" +
			"A : ('a'|'b')+ 'c' ;\n";
		String found = execParser("T.g4", grammar, "TParser", "TLexer",
								  "a", "abaac", debug);
		assertEquals("abaac\n", found);
	}

	@Test public void testNotCharSet() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : A {System.out.println($A.text);} ;\n" +
			"A : ~('b'|'c') ;\n";
		String found = execParser("T.g4", grammar, "TParser", "TLexer",
								  "a", "x", debug);
		assertEquals("x\n", found);
	}

	@Test public void testNotCharSetWithLabel() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : A {System.out.println($A.text);} ;\n" +
			"A : h=~('b'|'c') ;\n";
		String found = execParser("T.g4", grammar, "TParser", "TLexer",
								  "a", "x", debug);
		assertEquals("x\n", found);
	}

	@Test public void testNotCharSetWithRuleRef() throws Exception {
		// might be a useful feature to add someday
		String[] pair = new String[] {
			"grammar T;\n" +
			"a : A {System.out.println($A.text);} ;\n" +
			"A : ~('a'|B) ;\n" +
			"B : 'b' ;\n",
			"error(134): T.g4:3:10: lexer set element B is invalid (either lexer rule ref or literal with > 1 char)\n"
		};
		super.testErrors(pair, true);
	}

	@Test public void testNotCharSetWithRuleRef3() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : A {System.out.println($A.text);} ;\n" +
			"A : ('a'|B) ;\n" +  // this doesn't collapse to set but works
			"fragment\n" +
			"B : ~('a'|'c') ;\n";
		String found = execParser("T.g4", grammar, "TParser", "TLexer",
								  "a", "x", debug);
		assertEquals("x\n", found);
	}

	@Test public void testCharSetLiteral() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a : (A {System.out.println($A.text);})+ ;\n" +
			"A : [AaBb] ;\n" +
			"WS : (' '|'\\n')+ {skip();} ;\n";
		String found = execParser("T.g4", grammar, "TParser", "TLexer",
								  "a", "A a B b", debug);
		assertEquals("A\n" +
					 "a\n" +
					 "B\n" +
					 "b\n", found);
	}
}
