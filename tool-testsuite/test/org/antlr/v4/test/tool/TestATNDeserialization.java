/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ATNSerializer;
import org.antlr.v4.runtime.misc.IntegerList;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.LexerGrammar;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.antlr.v4.runtime.atn.ATNDeserializer.encodeIntsWith16BitWords;
import static org.antlr.v4.runtime.atn.ATNDeserializer.decodeIntsEncodedAs16BitWords;
import static org.antlr.v4.test.tool.ToolTestUtils.createATN;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestATNDeserialization {
	@Test public void testSimpleNoBlock() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : A B ;");
		checkDeserializationIsStable(g);
	}

	@Test public void testEOF() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : EOF ;");
		checkDeserializationIsStable(g);
	}

	@Test public void testEOFInSet() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : (EOF|A) ;");
		checkDeserializationIsStable(g);
	}

	@Test public void testNot() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"tokens {A, B, C}\n" +
			"a : ~A ;");
		checkDeserializationIsStable(g);
	}

	@Test public void testWildcard() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"tokens {A, B, C}\n" +
			"a : . ;");
		checkDeserializationIsStable(g);
	}

	@Test public void testPEGAchillesHeel() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : A | A B ;");
		checkDeserializationIsStable(g);
	}

	@Test public void test3Alts() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : A | A B | A B C ;");
		checkDeserializationIsStable(g);
	}

	@Test public void testSimpleLoop() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : A+ B ;");
		checkDeserializationIsStable(g);
	}

	@Test public void testRuleRef() throws Exception {
		Grammar g = new Grammar(
			"parser grammar T;\n"+
			"a : e ;\n" +
			"e : E ;\n");
		checkDeserializationIsStable(g);
	}

	@Test public void testLexerTwoRules() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"A : 'a' ;\n" +
			"B : 'b' ;\n");
		checkDeserializationIsStable(lg);
	}

	@Test public void testLexerEOF() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"A : 'a' EOF ;\n");
		checkDeserializationIsStable(lg);
	}

	@Test public void testLexerEOFInSet() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"A : 'a' (EOF|'\\n') ;\n");
		checkDeserializationIsStable(lg);
	}

	@Test public void testLexerRange() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"INT : '0'..'9' ;\n");
		checkDeserializationIsStable(lg);
	}

	@Test public void testLexerLoops() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"INT : '0'..'9'+ ;\n");
		checkDeserializationIsStable(lg);
	}

	@Test public void testLexerNotSet() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"ID : ~('a'|'b')\n ;");
		checkDeserializationIsStable(lg);
	}

	@Test public void testLexerNotSetWithRange() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"ID : ~('a'|'b'|'e'|'p'..'t')\n ;");
		checkDeserializationIsStable(lg);
	}

	@Test public void testLexerNotSetWithRange2() throws Exception {
		LexerGrammar lg = new LexerGrammar(
			"lexer grammar L;\n"+
			"ID : ~('a'|'b') ~('e'|'p'..'t')\n ;");
		checkDeserializationIsStable(lg);
	}

	@Test public void test2ModesInLexer() throws Exception {
		LexerGrammar lg = new LexerGrammar(
				"lexer grammar L;\n"+
						"A : 'a'\n ;\n" +
						"mode M;\n" +
						"B : 'b';\n" +
						"mode M2;\n" +
						"C : 'c';\n");
		checkDeserializationIsStable(lg);
	}

	@Test public void testLastValidBMPCharInSet() throws Exception {
		LexerGrammar lg = new LexerGrammar(
				"lexer grammar L;\n" +
						"ID : 'Ā'..'\\uFFFC'; // FFFD+ are not valid char\n");
		checkDeserializationIsStable(lg);
	}

	protected void checkDeserializationIsStable(Grammar g) {
		ATN atn = createATN(g, false);
		IntegerList serialized = ATNSerializer.getSerialized(atn);
		String atnData = new ATNDescriber(atn, Arrays.asList(g.getTokenNames())).decode(serialized.toArray());

		IntegerList serialized16 = encodeIntsWith16BitWords(serialized);
		int[] ints16 = serialized16.toArray();
		char[] chars = new char[ints16.length];
		for (int i = 0; i < ints16.length; i++) {
			chars[i] = (char)ints16[i];
		}
		int[] serialized32 = decodeIntsEncodedAs16BitWords(chars, true);

		assertArrayEquals(serialized.toArray(), serialized32);

		ATN atn2 = new ATNDeserializer().deserialize(serialized.toArray());
		IntegerList serialized1 = ATNSerializer.getSerialized(atn2);
		String atn2Data = new ATNDescriber(atn2, Arrays.asList(g.getTokenNames())).decode(serialized1.toArray());

		assertEquals(atnData, atn2Data);
	}
}
