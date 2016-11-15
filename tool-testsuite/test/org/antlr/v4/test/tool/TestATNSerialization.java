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

import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNSerializer;
import org.antlr.v4.test.runtime.java.BaseJavaTest;
import org.antlr.v4.tool.DOTGenerator;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.LexerGrammar;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class TestATNSerialization extends BaseJavaTest {
	@Before
	@Override
	public void testSetUp() throws Exception {
		super.testSetUp();
	}

	@Test public void testSimpleNoBlock() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : A B ;");
		String expecting =
			"max type 2\n" +
				"0:RULE_START 0\n" +
				"1:RULE_STOP 0\n" +
				"2:BASIC 0\n" +
				"3:BASIC 0\n" +
				"4:BASIC 0\n" +
				"5:BASIC 0\n" +
				"rule 0:0\n" +
				"0->2 EPSILON 0,0,0\n" +
				"2->3 ATOM 1,0,0\n" +
				"3->4 ATOM 2,0,0\n" +
				"4->1 EPSILON 0,0,0\n";
		ATN atn = createATN(g, true);
		String result = ATNSerializer.getDecoded(atn, Arrays.asList(g.getTokenNames()));
		assertEquals(expecting, result);
	}

	@Test public void testEOF() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : A EOF ;");
		String expecting =
			"max type 1\n" +
				"0:RULE_START 0\n" +
				"1:RULE_STOP 0\n" +
				"2:BASIC 0\n" +
				"3:BASIC 0\n" +
				"4:BASIC 0\n" +
				"5:BASIC 0\n" +
				"rule 0:0\n" +
				"0->2 EPSILON 0,0,0\n" +
				"2->3 ATOM 1,0,0\n" +
				"3->4 ATOM 0,0,1\n" +
				"4->1 EPSILON 0,0,0\n";
		ATN atn = createATN(g, true);
		String result = ATNSerializer.getDecoded(atn, Arrays.asList(g.getTokenNames()));
		assertEquals(expecting, result);
	}

	@Test public void testEOFInSet() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : (A|EOF) ;");
		String expecting =
			"max type 1\n" +
				"0:RULE_START 0\n" +
				"1:RULE_STOP 0\n" +
				"2:BASIC 0\n" +
				"3:BASIC 0\n" +
				"4:BASIC 0\n" +
				"rule 0:0\n" +
				"0:EOF, A..A\n" +
				"0->2 EPSILON 0,0,0\n" +
				"2->3 SET 0,0,0\n" +
				"3->1 EPSILON 0,0,0\n";
		ATN atn = createATN(g, true);
		String result = ATNSerializer.getDecoded(atn, Arrays.asList(g.getTokenNames()));
		assertEquals(expecting, result);
	}

	@Test public void testNot() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"tokens {A, B, C}\n" +
			"a : ~A ;");
		String expecting =
			"max type 3\n" +
			"0:RULE_START 0\n" +
			"1:RULE_STOP 0\n" +
			"2:BASIC 0\n" +
			"3:BASIC 0\n" +
			"4:BASIC 0\n" +
			"rule 0:0\n" +
			"0:A..A\n" +
			"0->2 EPSILON 0,0,0\n" +
			"2->3 NOT_SET 0,0,0\n" +
			"3->1 EPSILON 0,0,0\n";
		ATN atn = createATN(g, true);
		DOTGenerator gen = new DOTGenerator(g);
		String result = ATNSerializer.getDecoded(atn, Arrays.asList(g.getTokenNames()));
		assertEquals(expecting, result);
	}

	@Test public void testWildcard() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"tokens {A, B, C}\n" +
			"a : . ;");
		String expecting =
			"max type 3\n" +
			"0:RULE_START 0\n" +
			"1:RULE_STOP 0\n" +
			"2:BASIC 0\n" +
			"3:BASIC 0\n" +
			"4:BASIC 0\n" +
			"rule 0:0\n" +
			"0->2 EPSILON 0,0,0\n" +
			"2->3 WILDCARD 0,0,0\n" +
			"3->1 EPSILON 0,0,0\n";
		ATN atn = createATN(g, true);
		String result = ATNSerializer.getDecoded(atn, Arrays.asList(g.getTokenNames()));
		assertEquals(expecting, result);
	}

	@Test public void testPEGAchillesHeel() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : A | A B ;");
		String expecting =
			"max type 2\n" +
				"0:RULE_START 0\n" +
				"1:RULE_STOP 0\n" +
				"2:BASIC 0\n" +
				"3:BASIC 0\n" +
				"4:BASIC 0\n" +
				"5:BLOCK_START 0 6\n" +
				"6:BLOCK_END 0\n" +
				"7:BASIC 0\n" +
				"rule 0:0\n" +
				"0->5 EPSILON 0,0,0\n" +
				"2->6 ATOM 1,0,0\n" +
				"3->4 ATOM 1,0,0\n" +
				"4->6 ATOM 2,0,0\n" +
				"5->2 EPSILON 0,0,0\n" +
				"5->3 EPSILON 0,0,0\n" +
				"6->1 EPSILON 0,0,0\n" +
				"0:5\n";
		ATN atn = createATN(g, true);
		String result = ATNSerializer.getDecoded(atn, Arrays.asList(g.getTokenNames()));
		assertEquals(expecting, result);
	}

	@Test public void test3Alts() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : A | A B | A B C ;");
		String expecting =
			"max type 3\n" +
				"0:RULE_START 0\n" +
				"1:RULE_STOP 0\n" +
				"2:BASIC 0\n" +
				"3:BASIC 0\n" +
				"4:BASIC 0\n" +
				"5:BASIC 0\n" +
				"6:BASIC 0\n" +
				"7:BASIC 0\n" +
				"8:BLOCK_START 0 9\n" +
				"9:BLOCK_END 0\n" +
				"10:BASIC 0\n" +
				"rule 0:0\n" +
				"0->8 EPSILON 0,0,0\n" +
				"2->9 ATOM 1,0,0\n" +
				"3->4 ATOM 1,0,0\n" +
				"4->9 ATOM 2,0,0\n" +
				"5->6 ATOM 1,0,0\n" +
				"6->7 ATOM 2,0,0\n" +
				"7->9 ATOM 3,0,0\n" +
				"8->2 EPSILON 0,0,0\n" +
				"8->3 EPSILON 0,0,0\n" +
				"8->5 EPSILON 0,0,0\n" +
				"9->1 EPSILON 0,0,0\n" +
				"0:8\n";
		ATN atn = createATN(g, true);
		String result = ATNSerializer.getDecoded(atn, Arrays.asList(g.getTokenNames()));
		assertEquals(expecting, result);
	}

	@Test public void testSimpleLoop() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : A+ B ;");
		String expecting =
			"max type 2\n" +
				"0:RULE_START 0\n" +
				"1:RULE_STOP 0\n" +
				"2:BASIC 0\n" +
				"3:PLUS_BLOCK_START 0 4\n" +
				"4:BLOCK_END 0\n" +
				"5:PLUS_LOOP_BACK 0\n" +
				"6:LOOP_END 0 5\n" +
				"7:BASIC 0\n" +
				"8:BASIC 0\n" +
				"9:BASIC 0\n" +
				"rule 0:0\n" +
				"0->3 EPSILON 0,0,0\n" +
				"2->4 ATOM 1,0,0\n" +
				"3->2 EPSILON 0,0,0\n" +
				"4->5 EPSILON 0,0,0\n" +
				"5->3 EPSILON 0,0,0\n" +
				"5->6 EPSILON 0,0,0\n" +
				"6->7 EPSILON 0,0,0\n" +
				"7->8 ATOM 2,0,0\n" +
				"8->1 EPSILON 0,0,0\n" +
				"0:5\n";
		ATN atn = createATN(g, true);
		String result = ATNSerializer.getDecoded(atn, Arrays.asList(g.getTokenNames()));
		assertEquals(expecting, result);
	}

	@Test public void testRuleRef() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : e ;\n" +
			"e : E ;\n");
		String expecting =
			"max type 1\n" +
				"0:RULE_START 0\n" +
				"1:RULE_STOP 0\n" +
				"2:RULE_START 1\n" +
				"3:RULE_STOP 1\n" +
				"4:BASIC 0\n" +
				"5:BASIC 0\n" +
				"6:BASIC 1\n" +
				"7:BASIC 1\n" +
				"8:BASIC 1\n" +
				"rule 0:0\n" +
				"rule 1:2\n" +
				"0->4 EPSILON 0,0,0\n" +
				"2->6 EPSILON 0,0,0\n" +
				"4->5 RULE 2,1,0\n" +
				"5->1 EPSILON 0,0,0\n" +
				"6->7 ATOM 1,0,0\n" +
				"7->3 EPSILON 0,0,0\n";
		ATN atn = createATN(g, true);
		String result = ATNSerializer.getDecoded(atn, Arrays.asList(g.getTokenNames()));
		assertEquals(expecting, result);
	}

	@Test public void testLexerTwoRules() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"A : 'a' ;\n" +
			"B : 'b' ;\n");
		String expecting =
			"max type 2\n" +
			"0:TOKEN_START -1\n" +
			"1:RULE_START 0\n" +
			"2:RULE_STOP 0\n" +
			"3:RULE_START 1\n" +
			"4:RULE_STOP 1\n" +
			"5:BASIC 0\n" +
			"6:BASIC 0\n" +
			"7:BASIC 1\n" +
			"8:BASIC 1\n" +
			"rule 0:1 1\n" +
			"rule 1:3 2\n" +
			"mode 0:0\n" +
			"0->1 EPSILON 0,0,0\n" +
			"0->3 EPSILON 0,0,0\n" +
			"1->5 EPSILON 0,0,0\n" +
			"3->7 EPSILON 0,0,0\n" +
			"5->6 ATOM 97,0,0\n" +
			"6->2 EPSILON 0,0,0\n" +
			"7->8 ATOM 98,0,0\n" +
			"8->4 EPSILON 0,0,0\n" +
			"0:0\n";
		ATN atn = createATN(lg, true);
		String result = ATNSerializer.getDecoded(atn, Arrays.asList(lg.getTokenNames()));
		assertEquals(expecting, result);
	}

	@Test public void testLexerRange() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"INT : '0'..'9' ;\n");
		String expecting =
			"max type 1\n" +
			"0:TOKEN_START -1\n" +
			"1:RULE_START 0\n" +
			"2:RULE_STOP 0\n" +
			"3:BASIC 0\n" +
			"4:BASIC 0\n" +
			"rule 0:1 1\n" +
			"mode 0:0\n" +
			"0->1 EPSILON 0,0,0\n" +
			"1->3 EPSILON 0,0,0\n" +
			"3->4 RANGE 48,57,0\n" +
			"4->2 EPSILON 0,0,0\n" +
			"0:0\n";
		ATN atn = createATN(lg, true);
		String result = ATNSerializer.getDecoded(atn, Arrays.asList(lg.getTokenNames()));
		assertEquals(expecting, result);
	}

	@Test public void testLexerEOF() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"INT : 'a' EOF ;\n");
		String expecting =
			"max type 1\n" +
				"0:TOKEN_START -1\n" +
				"1:RULE_START 0\n" +
				"2:RULE_STOP 0\n" +
				"3:BASIC 0\n" +
				"4:BASIC 0\n" +
				"5:BASIC 0\n" +
				"rule 0:1 1\n" +
				"mode 0:0\n" +
				"0->1 EPSILON 0,0,0\n" +
				"1->3 EPSILON 0,0,0\n" +
				"3->4 ATOM 97,0,0\n" +
				"4->5 ATOM 0,0,1\n" +
				"5->2 EPSILON 0,0,0\n" +
				"0:0\n";
		ATN atn = createATN(lg, true);
		String result = ATNSerializer.getDecoded(atn, Arrays.asList(lg.getTokenNames()));
		assertEquals(expecting, result);
	}

	@Test public void testLexerEOFInSet() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"INT : 'a' (EOF|'\\n') ;\n");
		String expecting =
			"max type 1\n" +
				"0:TOKEN_START -1\n" +
				"1:RULE_START 0\n" +
				"2:RULE_STOP 0\n" +
				"3:BASIC 0\n" +
				"4:BASIC 0\n" +
				"5:BLOCK_START 0 6\n" +
				"6:BLOCK_END 0\n" +
				"rule 0:1 1\n" +
				"mode 0:0\n" +
				"0:EOF, '\\n'..'\\n'\n" +
				"0->1 EPSILON 0,0,0\n" +
				"1->3 EPSILON 0,0,0\n" +
				"3->5 ATOM 97,0,0\n" +
				"4->6 SET 0,0,0\n" +
				"5->4 EPSILON 0,0,0\n" +
				"6->2 EPSILON 0,0,0\n" +
				"0:0\n" +
				"1:5\n";
		ATN atn = createATN(lg, true);
		String result = ATNSerializer.getDecoded(atn, Arrays.asList(lg.getTokenNames()));
		assertEquals(expecting, result);
	}

	@Test public void testLexerLoops() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"INT : '0'..'9'+ ;\n");
		String expecting =
			"max type 1\n" +
				"0:TOKEN_START -1\n" +
				"1:RULE_START 0\n" +
				"2:RULE_STOP 0\n" +
				"3:BASIC 0\n" +
				"4:PLUS_BLOCK_START 0 5\n" +
				"5:BLOCK_END 0\n" +
				"6:PLUS_LOOP_BACK 0\n" +
				"7:LOOP_END 0 6\n" +
				"rule 0:1 1\n" +
				"mode 0:0\n" +
				"0->1 EPSILON 0,0,0\n" +
				"1->4 EPSILON 0,0,0\n" +
				"3->5 RANGE 48,57,0\n" +
				"4->3 EPSILON 0,0,0\n" +
				"5->6 EPSILON 0,0,0\n" +
				"6->4 EPSILON 0,0,0\n" +
				"6->7 EPSILON 0,0,0\n" +
				"7->2 EPSILON 0,0,0\n" +
				"0:0\n" +
				"1:6\n";
		ATN atn = createATN(lg, true);
		String result = ATNSerializer.getDecoded(atn, Arrays.asList(lg.getTokenNames()));
		assertEquals(expecting, result);
	}

	@Test public void testLexerAction() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"A : 'a' {a} ;\n" +
			"B : 'b' ;\n" +
			"C : 'c' {c} ;\n");
		String expecting =
			"max type 3\n" +
				"0:TOKEN_START -1\n" +
				"1:RULE_START 0\n" +
				"2:RULE_STOP 0\n" +
				"3:RULE_START 1\n" +
				"4:RULE_STOP 1\n" +
				"5:RULE_START 2\n" +
				"6:RULE_STOP 2\n" +
				"7:BASIC 0\n" +
				"8:BASIC 0\n" +
				"9:BASIC 0\n" +
				"10:BASIC 1\n" +
				"11:BASIC 1\n" +
				"12:BASIC 2\n" +
				"13:BASIC 2\n" +
				"14:BASIC 2\n" +
				"rule 0:1 1\n" +
				"rule 1:3 2\n" +
				"rule 2:5 3\n" +
				"mode 0:0\n" +
				"0->1 EPSILON 0,0,0\n" +
				"0->3 EPSILON 0,0,0\n" +
				"0->5 EPSILON 0,0,0\n" +
				"1->7 EPSILON 0,0,0\n" +
				"3->10 EPSILON 0,0,0\n" +
				"5->12 EPSILON 0,0,0\n" +
				"7->8 ATOM 97,0,0\n" +
				"8->9 ACTION 0,0,0\n" +
				"9->2 EPSILON 0,0,0\n" +
				"10->11 ATOM 98,0,0\n" +
				"11->4 EPSILON 0,0,0\n" +
				"12->13 ATOM 99,0,0\n" +
				"13->14 ACTION 2,1,0\n" +
				"14->6 EPSILON 0,0,0\n" +
				"0:0\n";
		ATN atn = createATN(lg, true);
		String result = ATNSerializer.getDecoded(atn, Arrays.asList(lg.getTokenNames()));
		assertEquals(expecting, result);
	}

	@Test public void testLexerNotSet() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"ID : ~('a'|'b')\n ;");
		String expecting =
			"max type 1\n" +
			"0:TOKEN_START -1\n" +
			"1:RULE_START 0\n" +
			"2:RULE_STOP 0\n" +
			"3:BASIC 0\n" +
			"4:BASIC 0\n" +
			"rule 0:1 1\n" +
			"mode 0:0\n" +
			"0:'a'..'b'\n" +
			"0->1 EPSILON 0,0,0\n" +
			"1->3 EPSILON 0,0,0\n" +
			"3->4 NOT_SET 0,0,0\n" +
			"4->2 EPSILON 0,0,0\n" +
			"0:0\n";
		ATN atn = createATN(lg, true);
		String result = ATNSerializer.getDecoded(atn, Arrays.asList(lg.getTokenNames()));
		assertEquals(expecting, result);
	}

	@Test public void testLexerSetWithRange() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"ID : ('a'|'b'|'e'|'p'..'t')\n ;");
		String expecting =
			"max type 1\n" +
			"0:TOKEN_START -1\n" +
			"1:RULE_START 0\n" +
			"2:RULE_STOP 0\n" +
			"3:BASIC 0\n" +
			"4:BASIC 0\n" +
			"rule 0:1 1\n" +
			"mode 0:0\n" +
			"0:'a'..'b', 'e'..'e', 'p'..'t'\n" +
			"0->1 EPSILON 0,0,0\n" +
			"1->3 EPSILON 0,0,0\n" +
			"3->4 SET 0,0,0\n" +
			"4->2 EPSILON 0,0,0\n" +
			"0:0\n";
		ATN atn = createATN(lg, true);
		String result = ATNSerializer.getDecoded(atn, Arrays.asList(lg.getTokenNames()));
		assertEquals(expecting, result);
	}

	@Test public void testLexerNotSetWithRange() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"ID : ~('a'|'b'|'e'|'p'..'t')\n ;");
		String expecting =
			"max type 1\n" +
			"0:TOKEN_START -1\n" +
			"1:RULE_START 0\n" +
			"2:RULE_STOP 0\n" +
			"3:BASIC 0\n" +
			"4:BASIC 0\n" +
			"rule 0:1 1\n" +
			"mode 0:0\n" +
			"0:'a'..'b', 'e'..'e', 'p'..'t'\n" +
			"0->1 EPSILON 0,0,0\n" +
			"1->3 EPSILON 0,0,0\n" +
			"3->4 NOT_SET 0,0,0\n" +
			"4->2 EPSILON 0,0,0\n" +
			"0:0\n";
		ATN atn = createATN(lg, true);
		String result = ATNSerializer.getDecoded(atn, Arrays.asList(lg.getTokenNames()));
		assertEquals(expecting, result);
	}

	@Test public void testLexerWildcardWithMode() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"ID : 'a'..'z'+ ;\n"+
			"mode CMT;" +
			"COMMENT : '*/' {skip(); popMode();} ;\n" +
			"JUNK : . {more();} ;\n");
		String expecting =
			"max type 3\n" +
				"0:TOKEN_START -1\n" +
				"1:TOKEN_START -1\n" +
				"2:RULE_START 0\n" +
				"3:RULE_STOP 0\n" +
				"4:RULE_START 1\n" +
				"5:RULE_STOP 1\n" +
				"6:RULE_START 2\n" +
				"7:RULE_STOP 2\n" +
				"8:BASIC 0\n" +
				"9:PLUS_BLOCK_START 0 10\n" +
				"10:BLOCK_END 0\n" +
				"11:PLUS_LOOP_BACK 0\n" +
				"12:LOOP_END 0 11\n" +
				"13:BASIC 1\n" +
				"14:BASIC 1\n" +
				"15:BASIC 1\n" +
				"16:BASIC 1\n" +
				"17:BASIC 1\n" +
				"18:BASIC 2\n" +
				"19:BASIC 2\n" +
				"20:BASIC 2\n" +
				"rule 0:2 1\n" +
				"rule 1:4 2\n" +
				"rule 2:6 3\n" +
				"mode 0:0\n" +
				"mode 1:1\n" +
				"0->2 EPSILON 0,0,0\n" +
				"1->4 EPSILON 0,0,0\n" +
				"1->6 EPSILON 0,0,0\n" +
				"2->9 EPSILON 0,0,0\n" +
				"4->13 EPSILON 0,0,0\n" +
				"6->18 EPSILON 0,0,0\n" +
				"8->10 RANGE 97,122,0\n" +
				"9->8 EPSILON 0,0,0\n" +
				"10->11 EPSILON 0,0,0\n" +
				"11->9 EPSILON 0,0,0\n" +
				"11->12 EPSILON 0,0,0\n" +
				"12->3 EPSILON 0,0,0\n" +
				"13->14 ATOM 42,0,0\n" +
				"14->15 ATOM 47,0,0\n" +
				"15->16 EPSILON 0,0,0\n" +
				"16->17 ACTION 1,0,0\n" +
				"17->5 EPSILON 0,0,0\n" +
				"18->19 WILDCARD 0,0,0\n" +
				"19->20 ACTION 2,1,0\n" +
				"20->7 EPSILON 0,0,0\n" +
				"0:0\n" +
				"1:1\n" +
				"2:11\n";
		ATN atn = createATN(lg, true);
		String result = ATNSerializer.getDecoded(atn, Arrays.asList(lg.getTokenNames()));
		assertEquals(expecting, result);
	}

	@Test public void testLexerNotSetWithRange2() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"ID : ~('a'|'b') ~('e'|'p'..'t')\n ;");
		String expecting =
			"max type 1\n" +
				"0:TOKEN_START -1\n" +
				"1:RULE_START 0\n" +
				"2:RULE_STOP 0\n" +
				"3:BASIC 0\n" +
				"4:BASIC 0\n" +
				"5:BASIC 0\n" +
				"rule 0:1 1\n" +
				"mode 0:0\n" +
				"0:'a'..'b'\n" +
				"1:'e'..'e', 'p'..'t'\n" +
				"0->1 EPSILON 0,0,0\n" +
				"1->3 EPSILON 0,0,0\n" +
				"3->4 NOT_SET 0,0,0\n" +
				"4->5 NOT_SET 1,0,0\n" +
				"5->2 EPSILON 0,0,0\n" +
				"0:0\n";
		ATN atn = createATN(lg, true);
		String result = ATNSerializer.getDecoded(atn, Arrays.asList(lg.getTokenNames()));
		assertEquals(expecting, result);
	}

	@Test public void testModeInLexer() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"A : 'a'\n ;\n" +
			"B : 'b';\n" +
			"mode M;\n" +
			"C : 'c';\n"+
			"D : 'd';\n");
		String expecting =
			"max type 4\n" +
			"0:TOKEN_START -1\n" +
			"1:TOKEN_START -1\n" +
			"2:RULE_START 0\n" +
			"3:RULE_STOP 0\n" +
			"4:RULE_START 1\n" +
			"5:RULE_STOP 1\n" +
			"6:RULE_START 2\n" +
			"7:RULE_STOP 2\n" +
			"8:RULE_START 3\n" +
			"9:RULE_STOP 3\n" +
			"10:BASIC 0\n" +
			"11:BASIC 0\n" +
			"12:BASIC 1\n" +
			"13:BASIC 1\n" +
			"14:BASIC 2\n" +
			"15:BASIC 2\n" +
			"16:BASIC 3\n" +
			"17:BASIC 3\n" +
			"rule 0:2 1\n" +
			"rule 1:4 2\n" +
			"rule 2:6 3\n" +
			"rule 3:8 4\n" +
			"mode 0:0\n" +
			"mode 1:1\n" +
			"0->2 EPSILON 0,0,0\n" +
			"0->4 EPSILON 0,0,0\n" +
			"1->6 EPSILON 0,0,0\n" +
			"1->8 EPSILON 0,0,0\n" +
			"2->10 EPSILON 0,0,0\n" +
			"4->12 EPSILON 0,0,0\n" +
			"6->14 EPSILON 0,0,0\n" +
			"8->16 EPSILON 0,0,0\n" +
			"10->11 ATOM 97,0,0\n" +
			"11->3 EPSILON 0,0,0\n" +
			"12->13 ATOM 98,0,0\n" +
			"13->5 EPSILON 0,0,0\n" +
			"14->15 ATOM 99,0,0\n" +
			"15->7 EPSILON 0,0,0\n" +
			"16->17 ATOM 100,0,0\n" +
			"17->9 EPSILON 0,0,0\n" +
			"0:0\n" +
			"1:1\n";
		ATN atn = createATN(lg, true);
		String result = ATNSerializer.getDecoded(atn, Arrays.asList(lg.getTokenNames()));
		assertEquals(expecting, result);
	}

	@Test public void test2ModesInLexer() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"A : 'a'\n ;\n" +
			"mode M;\n" +
			"B : 'b';\n" +
			"mode M2;\n" +
			"C : 'c';\n");
		String expecting =
			"max type 3\n" +
			"0:TOKEN_START -1\n" +
			"1:TOKEN_START -1\n" +
			"2:TOKEN_START -1\n" +
			"3:RULE_START 0\n" +
			"4:RULE_STOP 0\n" +
			"5:RULE_START 1\n" +
			"6:RULE_STOP 1\n" +
			"7:RULE_START 2\n" +
			"8:RULE_STOP 2\n" +
			"9:BASIC 0\n" +
			"10:BASIC 0\n" +
			"11:BASIC 1\n" +
			"12:BASIC 1\n" +
			"13:BASIC 2\n" +
			"14:BASIC 2\n" +
			"rule 0:3 1\n" +
			"rule 1:5 2\n" +
			"rule 2:7 3\n" +
			"mode 0:0\n" +
			"mode 1:1\n" +
			"mode 2:2\n" +
			"0->3 EPSILON 0,0,0\n" +
			"1->5 EPSILON 0,0,0\n" +
			"2->7 EPSILON 0,0,0\n" +
			"3->9 EPSILON 0,0,0\n" +
			"5->11 EPSILON 0,0,0\n" +
			"7->13 EPSILON 0,0,0\n" +
			"9->10 ATOM 97,0,0\n" +
			"10->4 EPSILON 0,0,0\n" +
			"11->12 ATOM 98,0,0\n" +
			"12->6 EPSILON 0,0,0\n" +
			"13->14 ATOM 99,0,0\n" +
			"14->8 EPSILON 0,0,0\n" +
			"0:0\n" +
			"1:1\n" +
			"2:2\n";
		ATN atn = createATN(lg, true);
		String result = ATNSerializer.getDecoded(atn, Arrays.asList(lg.getTokenNames()));
		assertEquals(expecting, result);
	}

}
