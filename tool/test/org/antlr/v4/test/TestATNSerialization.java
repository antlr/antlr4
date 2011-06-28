package org.antlr.v4.test;

import org.antlr.v4.automata.ATNSerializer;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.tool.*;
import org.junit.Test;

public class TestATNSerialization extends BaseTest {
	@Test public void testSimpleNoBlock() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : A B ;");
		String expecting =
			"max type 4\n" +
			"0:RULE_START 0\n" +
			"1:RULE_STOP 0\n" +
			"2:BASIC 0\n" +
			"3:BASIC 0\n" +
			"4:BASIC 0\n" +
			"5:BASIC 0\n" +
			"6:BASIC 0\n" +
			"rule 1:0 0,0\n" +
			"0->2 EPSILON 0,0\n" +
			"1->6 ATOM -1,0\n" +
			"2->3 ATOM 3,0\n" +
			"3->4 EPSILON 0,0\n" +
			"4->5 ATOM 4,0\n" +
			"5->1 EPSILON 0,0\n";
		ATN atn = createATN(g);
		String result = ATNSerializer.getDecoded(g, atn);
		assertEquals(expecting, result);
	}

	@Test public void testNot() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"tokens {A; B; C;}\n" +
			"a : ~A ;");
		String expecting =
			"max type 5\n" +
			"0:RULE_START 0\n" +
			"1:RULE_STOP 0\n" +
			"2:BASIC 0\n" +
			"3:BASIC 0\n" +
			"4:BASIC 0\n" +
			"rule 1:0 0,0\n" +
			"0->2 EPSILON 0,0\n" +
			"1->4 ATOM -1,0\n" +
			"2->3 NOT_ATOM 3,0\n" +
			"3->1 EPSILON 0,0\n";
		ATN atn = createATN(g);
		String result = ATNSerializer.getDecoded(g, atn);
		assertEquals(expecting, result);
	}

	@Test public void testWildcard() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"tokens {A; B; C;}\n" +
			"a : . ;");
		String expecting =
			"max type 5\n" +
			"0:RULE_START 0\n" +
			"1:RULE_STOP 0\n" +
			"2:BASIC 0\n" +
			"3:BASIC 0\n" +
			"4:BASIC 0\n" +
			"rule 1:0 0,0\n" +
			"0->2 EPSILON 0,0\n" +
			"1->4 ATOM -1,0\n" +
			"2->3 WILDCARD 0,0\n" +
			"3->1 EPSILON 0,0\n";
		ATN atn = createATN(g);
		String result = ATNSerializer.getDecoded(g, atn);
		assertEquals(expecting, result);
	}

	@Test public void testPEGAchillesHeel() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : A | A B ;");
		String expecting =
			"max type 4\n" +
			"0:RULE_START 0\n" +
			"1:RULE_STOP 0\n" +
			"2:BASIC 0\n" +
			"3:BASIC 0\n" +
			"4:BASIC 0\n" +
			"5:BASIC 0\n" +
			"6:BASIC 0\n" +
			"7:BASIC 0\n" +
			"8:BLOCK_START 0\n" +
			"9:BLOCK_END 0\n" +
			"10:BASIC 0\n" +
			"rule 1:0 0,0\n" +
			"0->8 EPSILON 0,0\n" +
			"1->10 ATOM -1,0\n" +
			"2->3 ATOM 3,0\n" +
			"3->9 EPSILON 0,0\n" +
			"4->5 ATOM 3,0\n" +
			"5->6 EPSILON 0,0\n" +
			"6->7 ATOM 4,0\n" +
			"7->9 EPSILON 0,0\n" +
			"8->2 EPSILON 0,0\n" +
			"8->4 EPSILON 0,0\n" +
			"9->1 EPSILON 0,0\n" +
			"0:8\n";
		ATN atn = createATN(g);
		String result = ATNSerializer.getDecoded(g, atn);
		assertEquals(expecting, result);
	}

	@Test public void test3Alts() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : A | A B | A B C ;");
		String expecting =
			"max type 5\n" +
			"0:RULE_START 0\n" +
			"1:RULE_STOP 0\n" +
			"2:BASIC 0\n" +
			"3:BASIC 0\n" +
			"4:BASIC 0\n" +
			"5:BASIC 0\n" +
			"6:BASIC 0\n" +
			"7:BASIC 0\n" +
			"8:BASIC 0\n" +
			"9:BASIC 0\n" +
			"10:BASIC 0\n" +
			"11:BASIC 0\n" +
			"12:BASIC 0\n" +
			"13:BASIC 0\n" +
			"14:BLOCK_START 0\n" +
			"15:BLOCK_END 0\n" +
			"16:BASIC 0\n" +
			"rule 1:0 0,0\n" +
			"0->14 EPSILON 0,0\n" +
			"1->16 ATOM -1,0\n" +
			"2->3 ATOM 3,0\n" +
			"3->15 EPSILON 0,0\n" +
			"4->5 ATOM 3,0\n" +
			"5->6 EPSILON 0,0\n" +
			"6->7 ATOM 4,0\n" +
			"7->15 EPSILON 0,0\n" +
			"8->9 ATOM 3,0\n" +
			"9->10 EPSILON 0,0\n" +
			"10->11 ATOM 4,0\n" +
			"11->12 EPSILON 0,0\n" +
			"12->13 ATOM 5,0\n" +
			"13->15 EPSILON 0,0\n" +
			"14->2 EPSILON 0,0\n" +
			"14->4 EPSILON 0,0\n" +
			"14->8 EPSILON 0,0\n" +
			"15->1 EPSILON 0,0\n" +
			"0:14\n";
		ATN atn = createATN(g);
		String result = ATNSerializer.getDecoded(g, atn);
		assertEquals(expecting, result);
	}

	@Test public void testSimpleLoop() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : A+ B ;");
		String expecting =
			"max type 4\n" +
			"0:RULE_START 0\n" +
			"1:RULE_STOP 0\n" +
			"2:BASIC 0\n" +
			"3:BASIC 0\n" +
			"4:PLUS_BLOCK_START 0\n" +
			"5:BLOCK_END 0\n" +
			"6:PLUS_LOOP_BACK 0\n" +
			"7:BASIC 0\n" +
			"8:BASIC 0\n" +
			"9:BASIC 0\n" +
			"10:BASIC 0\n" +
			"rule 1:0 0,0\n" +
			"0->4 EPSILON 0,0\n" +
			"1->10 ATOM -1,0\n" +
			"2->3 ATOM 3,0\n" +
			"3->5 EPSILON 0,0\n" +
			"4->2 EPSILON 0,0\n" +
			"5->6 EPSILON 0,0\n" +
			"6->2 EPSILON 0,0\n" +
			"6->7 EPSILON 0,0\n" +
			"7->8 EPSILON 0,0\n" +
			"8->9 ATOM 4,0\n" +
			"9->1 EPSILON 0,0\n" +
			"0:4\n" +
			"1:4\n" +
			"2:6\n";
		ATN atn = createATN(g);
		String result = ATNSerializer.getDecoded(g, atn);
		assertEquals(expecting, result);
	}

	@Test public void testRuleRef() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : e ;\n" +
			"e : E ;\n");
		String expecting =
			"max type 3\n" +
			"0:RULE_START 0\n" +
			"1:RULE_STOP 0\n" +
			"2:RULE_START 1\n" +
			"3:RULE_STOP 1\n" +
			"4:BASIC 0\n" +
			"5:BASIC 0\n" +
			"6:BASIC 1\n" +
			"7:BASIC 1\n" +
			"8:BASIC 1\n" +
			"rule 1:0 0,0\n" +
			"rule 2:2 0,0\n" +
			"0->4 EPSILON 0,0\n" +
			"1->8 ATOM -1,0\n" +
			"2->6 EPSILON 0,0\n" +
			"3->5 EPSILON 0,0\n" +
			"4->5 RULE 2,1\n" +
			"5->1 EPSILON 0,0\n" +
			"6->7 ATOM 3,0\n" +
			"7->3 EPSILON 0,0\n";
		ATN atn = createATN(g);
		String result = ATNSerializer.getDecoded(g, atn);
		assertEquals(expecting, result);
	}

	@Test public void testLexerTwoRules() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"A : 'a' ;\n" +
			"B : 'b' ;\n");
		String expecting =
			"max type 4\n" +
			"0:TOKEN_START 0\n" +
			"1:RULE_START 0\n" +
			"2:RULE_STOP 0\n" +
			"3:RULE_START 1\n" +
			"4:RULE_STOP 1\n" +
			"5:BASIC 0\n" +
			"6:BASIC 0\n" +
			"7:BASIC 1\n" +
			"8:BASIC 1\n" +
			"rule 1:1 3,0\n" +
			"rule 2:3 4,0\n" +
			"mode 0:0\n" +
			"0->1 EPSILON 0,0\n" +
			"0->3 EPSILON 0,0\n" +
			"1->5 EPSILON 0,0\n" +
			"3->7 EPSILON 0,0\n" +
			"5->6 ATOM 97,0\n" +
			"6->2 EPSILON 0,0\n" +
			"7->8 ATOM 98,0\n" +
			"8->4 EPSILON 0,0\n" +
			"0:0\n";
		ATN atn = createATN(lg);
		String result = ATNSerializer.getDecoded(lg, atn);
		assertEquals(expecting, result);
	}

	@Test public void testLexerRange() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"INT : '0'..'9' ;\n");
		String expecting =
			"max type 3\n" +
			"0:TOKEN_START 0\n" +
			"1:RULE_START 0\n" +
			"2:RULE_STOP 0\n" +
			"3:BASIC 0\n" +
			"4:BASIC 0\n" +
			"rule 1:1 3,0\n" +
			"mode 0:0\n" +
			"0->1 EPSILON 0,0\n" +
			"1->3 EPSILON 0,0\n" +
			"3->4 RANGE 48,57\n" +
			"4->2 EPSILON 0,0\n" +
			"0:0\n";
		ATN atn = createATN(lg);
		String result = ATNSerializer.getDecoded(lg, atn);
		assertEquals(expecting, result);
	}

	@Test public void testLexerLoops() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"INT : '0'..'9'+ ;\n");
		String expecting =
			"max type 3\n" +
			"0:TOKEN_START 0\n" +
			"1:RULE_START 0\n" +
			"2:RULE_STOP 0\n" +
			"3:BASIC 0\n" +
			"4:BASIC 0\n" +
			"5:PLUS_BLOCK_START 0\n" +
			"6:BLOCK_END 0\n" +
			"7:PLUS_LOOP_BACK 0\n" +
			"8:BASIC 0\n" +
			"rule 1:1 3,0\n" +
			"mode 0:0\n" +
			"0->1 EPSILON 0,0\n" +
			"1->5 EPSILON 0,0\n" +
			"3->4 RANGE 48,57\n" +
			"4->6 EPSILON 0,0\n" +
			"5->3 EPSILON 0,0\n" +
			"6->7 EPSILON 0,0\n" +
			"7->3 EPSILON 0,0\n" +
			"7->8 EPSILON 0,0\n" +
			"8->2 EPSILON 0,0\n" +
			"0:0\n" +
			"1:5\n" +
			"2:5\n" +
			"3:7\n";
		ATN atn = createATN(lg);
		String result = ATNSerializer.getDecoded(lg, atn);
		assertEquals(expecting, result);
	}

	@Test public void testLexerAction() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"A : 'a' {a} ;\n" +
			"B : 'b' ;\n" +
			"C : 'c' {c} ;\n");
		String expecting =
			"max type 5\n" +
			"0:TOKEN_START 0\n" +
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
			"rule 1:1 3,1\n" +
			"rule 2:3 4,0\n" +
			"rule 3:5 5,2\n" +
			"mode 0:0\n" +
			"0->1 EPSILON 0,0\n" +
			"0->3 EPSILON 0,0\n" +
			"0->5 EPSILON 0,0\n" +
			"1->7 EPSILON 0,0\n" +
			"3->10 EPSILON 0,0\n" +
			"5->12 EPSILON 0,0\n" +
			"7->8 ATOM 97,0\n" +
			"8->9 EPSILON 0,0\n" +
			"9->2 EPSILON 0,0\n" +
			"10->11 ATOM 98,0\n" +
			"11->4 EPSILON 0,0\n" +
			"12->13 ATOM 99,0\n" +
			"13->14 EPSILON 0,0\n" +
			"14->6 EPSILON 0,0\n" +
			"0:0\n";
		ATN atn = createATN(lg);
		String result = ATNSerializer.getDecoded(lg, atn);
		assertEquals(expecting, result);
	}

	@Test public void testLexerNotSet() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"ID : ~('a'|'b')\n ;");
		String expecting =
			"max type 3\n" +
			"0:TOKEN_START 0\n" +
			"1:RULE_START 0\n" +
			"2:RULE_STOP 0\n" +
			"3:BASIC 0\n" +
			"4:BASIC 0\n" +
			"rule 1:1 3,0\n" +
			"mode 0:0\n" +
			"0:'a'..'b'\n" +
			"0->1 EPSILON 0,0\n" +
			"1->3 EPSILON 0,0\n" +
			"3->4 NOT_SET 0,0\n" +
			"4->2 EPSILON 0,0\n" +
			"0:0\n";
		ATN atn = createATN(lg);
		String result = ATNSerializer.getDecoded(lg, atn);
		assertEquals(expecting, result);
	}

	@Test public void testLexerNotSetWithRange() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"ID : ~('a'|'b'|'e'|'p'..'t')\n ;");
		String expecting =
			"max type 3\n" +
			"0:TOKEN_START 0\n" +
			"1:RULE_START 0\n" +
			"2:RULE_STOP 0\n" +
			"3:BASIC 0\n" +
			"4:BASIC 0\n" +
			"rule 1:1 3,0\n" +
			"mode 0:0\n" +
			"0:'a'..'b', 'e'..'e', 'p'..'t'\n" +
			"0->1 EPSILON 0,0\n" +
			"1->3 EPSILON 0,0\n" +
			"3->4 NOT_SET 0,0\n" +
			"4->2 EPSILON 0,0\n" +
			"0:0\n";
		ATN atn = createATN(lg);
		String result = ATNSerializer.getDecoded(lg, atn);
		assertEquals(expecting, result);
	}

	@Test public void testLexerNotSetWithRange2() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"ID : ~('a'|'b') ~('e'|'p'..'t')\n ;");
		String expecting =
			"max type 3\n" +
			"0:TOKEN_START 0\n" +
			"1:RULE_START 0\n" +
			"2:RULE_STOP 0\n" +
			"3:BASIC 0\n" +
			"4:BASIC 0\n" +
			"5:BASIC 0\n" +
			"6:BASIC 0\n" +
			"rule 1:1 3,0\n" +
			"mode 0:0\n" +
			"0:'a'..'b'\n" +
			"1:'e'..'e', 'p'..'t'\n" +
			"0->1 EPSILON 0,0\n" +
			"1->3 EPSILON 0,0\n" +
			"3->4 NOT_SET 0,0\n" +
			"4->5 EPSILON 0,0\n" +
			"5->6 NOT_SET 1,0\n" +
			"6->2 EPSILON 0,0\n" +
			"0:0\n";
		ATN atn = createATN(lg);
		String result = ATNSerializer.getDecoded(lg, atn);
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
			"max type 5\n" +
			"0:TOKEN_START 0\n" +
			"1:TOKEN_START 0\n" +
			"2:TOKEN_START 0\n" +
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
			"rule 1:3 3,0\n" +
			"rule 2:5 4,0\n" +
			"rule 3:7 5,0\n" +
			"mode 0:0\n" +
			"mode 1:1\n" +
			"mode 2:2\n" +
			"0->3 EPSILON 0,0\n" +
			"1->5 EPSILON 0,0\n" +
			"2->7 EPSILON 0,0\n" +
			"3->9 EPSILON 0,0\n" +
			"5->11 EPSILON 0,0\n" +
			"7->13 EPSILON 0,0\n" +
			"9->10 ATOM 97,0\n" +
			"10->4 EPSILON 0,0\n" +
			"11->12 ATOM 98,0\n" +
			"12->6 EPSILON 0,0\n" +
			"13->14 ATOM 99,0\n" +
			"14->8 EPSILON 0,0\n" +
			"0:0\n" +
			"1:1\n" +
			"2:2\n";
		ATN atn = createATN(lg);
		String result = ATNSerializer.getDecoded(lg, atn);
		assertEquals(expecting, result);
	}

}
