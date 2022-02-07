/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.misc.IntegerList;
import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.LexerGrammar;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class TestATNDeserialization extends BaseJavaToolTest {
	@Before
	@Override
	public void testSetUp() throws Exception {
		super.testSetUp();
	}

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

	@Test public void testATNDataWriterReaderCompact() {
		IntegerList integerList = new IntegerList();
		ATNDataWriter writer = new ATNDataWriter(integerList, "Java");
		assertEquals(1, writer.write(0));
		assertEquals(1, writer.write(-1));
		assertEquals(1, writer.write(42));
		assertEquals(2, writer.write(1 << 14));
		assertEquals(2, writer.write(0xFFFF));
		assertEquals(3, writer.write(Integer.MAX_VALUE));
		assertEquals(3, writer.write(Integer.MIN_VALUE));
		assertEquals(13, integerList.size());

		char[] charArray = Utils.toCharArray(integerList);
		ATNDataReader reader = new ATNDataReader(charArray);
		assertEquals(0, reader.read());
		assertEquals(-1, reader.read());
		assertEquals(42, reader.read());
		assertEquals(1 << 14, reader.read());
		assertEquals(0xFFFF, reader.read());
		assertEquals(Integer.MAX_VALUE, reader.read());
		assertEquals(Integer.MIN_VALUE, reader.read());
	}

	@Test public void testATNDataWriterReaderRaw() {
		IntegerList integerList = new IntegerList();
		ATNDataWriter writer = new ATNDataWriter(integerList, "Java");
		writer.writeInt32(0);
		writer.writeInt32(-1);
		writer.writeInt32(42);
		writer.writeInt32(1 << 14);
		writer.writeInt32(0xFFFF);
		writer.writeInt32(Integer.MAX_VALUE);
		writer.writeInt32(Integer.MIN_VALUE);
		assertEquals(7 * 2, integerList.size());

		char[] charArray = Utils.toCharArray(integerList);
		ATNDataReader reader = new ATNDataReader(charArray);
		assertEquals(0, reader.readInt32());
		assertEquals(-1, reader.readInt32());
		assertEquals(42, reader.readInt32());
		assertEquals(1 << 14, reader.readInt32());
		assertEquals(0xFFFF, reader.readInt32());
		assertEquals(Integer.MAX_VALUE, reader.readInt32());
		assertEquals(Integer.MIN_VALUE, reader.readInt32());
	}

	protected void checkDeserializationIsStable(Grammar g) {
		ATN atn = createATN(g, false);
		char[] data = Utils.toCharArray(ATNSerializer.getSerialized(atn, "Java"));
		String atnData = TestATNSerialization.getDecoded(atn, Arrays.asList(g.getTokenNames()));
		ATN atn2 = new ATNDeserializer().deserialize(data);
		String atn2Data = TestATNSerialization.getDecoded(atn2, Arrays.asList(g.getTokenNames()));

		assertEquals(atnData, atn2Data);
	}
}
