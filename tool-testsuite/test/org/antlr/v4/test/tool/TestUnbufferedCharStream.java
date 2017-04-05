/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenFactory;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.LexerInterpreter;
import org.antlr.v4.runtime.UnbufferedCharStream;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.tool.LexerGrammar;
import org.junit.Before;
import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("unused")
public class TestUnbufferedCharStream extends BaseJavaToolTest {
	@Before
	@Override
	public void testSetUp() throws Exception {
		super.testSetUp();
	}

	@Test public void testNoChar() throws Exception {
		CharStream input = createStream("");
		assertEquals(IntStream.EOF, input.LA(1));
		assertEquals(IntStream.EOF, input.LA(2));
	}

	/**
	 * The {@link IntStream} interface does not specify the behavior when the
	 * EOF symbol is consumed, but {@link UnbufferedCharStream} handles this
	 * particular case by throwing an {@link IllegalStateException}.
	 */
	@Test(expected = IllegalStateException.class)
	public void testConsumeEOF() throws Exception {
		CharStream input = createStream("");
		assertEquals(IntStream.EOF, input.LA(1));
		input.consume();
		input.consume();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNegativeSeek() {
		CharStream input = createStream("");
		input.seek(-1);
	}

	@Test
	public void testSeekPastEOF() {
		CharStream input = createStream("");
		assertEquals(0, input.index());
		input.seek(1);
		assertEquals(0, input.index());
	}

	/**
	 * The {@link IntStream} interface does not specify the behavior when marks
	 * are not released in the reversed order they were created, but
	 * {@link UnbufferedCharStream} handles this case by throwing an
	 * {@link IllegalStateException}.
	 */
	@Test(expected = IllegalStateException.class)
	public void testMarkReleaseOutOfOrder() {
		CharStream input = createStream("");
		int m1 = input.mark();
		int m2 = input.mark();
		input.release(m1);
	}

	/**
	 * The {@link IntStream} interface does not specify the behavior when a mark
	 * is released twice, but {@link UnbufferedCharStream} handles this case by
	 * throwing an {@link IllegalStateException}.
	 */
	@Test(expected = IllegalStateException.class)
	public void testMarkReleasedTwice() {
		CharStream input = createStream("");
		int m1 = input.mark();
		input.release(m1);
		input.release(m1);
	}

	/**
	 * The {@link IntStream} interface does not specify the behavior when a mark
	 * is released twice, but {@link UnbufferedCharStream} handles this case by
	 * throwing an {@link IllegalStateException}.
	 */
	@Test(expected = IllegalStateException.class)
	public void testNestedMarkReleasedTwice() {
		CharStream input = createStream("");
		int m1 = input.mark();
		int m2 = input.mark();
		input.release(m2);
		input.release(m2);
	}

	/**
	 * It is not valid to pass a mark to {@link IntStream#seek}, but
	 * {@link UnbufferedCharStream} creates marks in such a way that this
	 * invalid usage results in an {@link IllegalArgumentException}.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testMarkPassedToSeek() {
		CharStream input = createStream("");
		int m1 = input.mark();
		input.seek(m1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSeekBeforeBufferStart() {
		CharStream input = createStream("xyz");
		input.consume();
		int m1 = input.mark();
		assertEquals(1, input.index());
		input.consume();
		input.seek(0);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetTextBeforeBufferStart() {
		CharStream input = createStream("xyz");
		input.consume();
		int m1 = input.mark();
		assertEquals(1, input.index());
		input.getText(new Interval(0, 1));
	}

	@Test
	public void testGetTextInMarkedRange() {
		CharStream input = createStream("xyz");
		input.consume();
		int m1 = input.mark();
		assertEquals(1, input.index());
		input.consume();
		input.consume();
		assertEquals("yz", input.getText(new Interval(1, 2)));
	}

	@Test
	public void testLastChar() {
		CharStream input = createStream("abcdef");

		input.consume();
		assertEquals('a', input.LA(-1));

		int m1 = input.mark();
		input.consume();
		input.consume();
		input.consume();
		assertEquals('d', input.LA(-1));

		input.seek(2);
		assertEquals('b', input.LA(-1));

		input.release(m1);
		input.seek(3);
		assertEquals('c', input.LA(-1));
		// this special case is not required by the IntStream interface, but
		// UnbufferedCharStream allows it so we have to make sure the resulting
		// state is consistent
		input.seek(2);
		assertEquals('b', input.LA(-1));
	}

	@Test public void test1Char() throws Exception {
		TestingUnbufferedCharStream input = createStream("x");
		assertEquals('x', input.LA(1));
		input.consume();
		assertEquals(IntStream.EOF, input.LA(1));
		String r = input.getRemainingBuffer();
		assertEquals("\uFFFF", r); // shouldn't include x
		assertEquals("\uFFFF", input.getBuffer()); // whole buffer
	}

	@Test public void test2Char() throws Exception {
		TestingUnbufferedCharStream input = createStream("xy");
		assertEquals('x', input.LA(1));
		input.consume();
		assertEquals('y', input.LA(1));
		assertEquals("y", input.getRemainingBuffer()); // shouldn't include x
		assertEquals("y", input.getBuffer());
		input.consume();
		assertEquals(IntStream.EOF, input.LA(1));
		assertEquals("\uFFFF", input.getBuffer());
	}

    @Test public void test2CharAhead() throws Exception {
   		CharStream input = createStream("xy");
   		assertEquals('x', input.LA(1));
   		assertEquals('y', input.LA(2));
   		assertEquals(IntStream.EOF, input.LA(3));
   	}

    @Test public void testBufferExpand() throws Exception {
		TestingUnbufferedCharStream input = createStream("01234", 2);
   		assertEquals('0', input.LA(1));
        assertEquals('1', input.LA(2));
        assertEquals('2', input.LA(3));
        assertEquals('3', input.LA(4));
        assertEquals('4', input.LA(5));
		assertEquals("01234", input.getBuffer());
   		assertEquals(IntStream.EOF, input.LA(6));
   	}

    @Test public void testBufferWrapSize1() throws Exception {
   		CharStream input = createStream("01234", 1);
        assertEquals('0', input.LA(1));
        input.consume();
        assertEquals('1', input.LA(1));
        input.consume();
        assertEquals('2', input.LA(1));
        input.consume();
        assertEquals('3', input.LA(1));
        input.consume();
        assertEquals('4', input.LA(1));
        input.consume();
   		assertEquals(IntStream.EOF, input.LA(1));
   	}

    @Test public void testBufferWrapSize2() throws Exception {
   		CharStream input = createStream("01234", 2);
        assertEquals('0', input.LA(1));
        input.consume();
        assertEquals('1', input.LA(1));
        input.consume();
        assertEquals('2', input.LA(1));
        input.consume();
        assertEquals('3', input.LA(1));
        input.consume();
        assertEquals('4', input.LA(1));
        input.consume();
   		assertEquals(IntStream.EOF, input.LA(1));
   	}

	@Test public void test1Mark() throws Exception {
		TestingUnbufferedCharStream input = createStream("xyz");
		int m = input.mark();
		assertEquals('x', input.LA(1));
		assertEquals('y', input.LA(2));
		assertEquals('z', input.LA(3));
		input.release(m);
		assertEquals(IntStream.EOF, input.LA(4));
		assertEquals("xyz\uFFFF", input.getBuffer());
	}

	@Test public void test1MarkWithConsumesInSequence() throws Exception {
		TestingUnbufferedCharStream input = createStream("xyz");
		int m = input.mark();
		input.consume(); // x, moves to y
		input.consume(); // y
		input.consume(); // z, moves to EOF
		assertEquals(IntStream.EOF, input.LA(1));
		assertEquals("xyz\uFFFF", input.getBuffer());
		input.release(m); // wipes buffer
		assertEquals("\uFFFF", input.getBuffer());
	}

    @Test public void test2Mark() throws Exception {
		TestingUnbufferedCharStream input = createStream("xyz", 100);
   		assertEquals('x', input.LA(1));
        input.consume(); // reset buffer index (p) to 0
        int m1 = input.mark();
   		assertEquals('y', input.LA(1));
        input.consume();
        int m2 = input.mark();
		assertEquals("yz", input.getBuffer());
        input.release(m2); // drop to 1 marker
        input.consume();
        input.release(m1); // shifts remaining char to beginning
   		assertEquals(IntStream.EOF, input.LA(1));
		assertEquals("\uFFFF", input.getBuffer());
   	}

    @Test public void testAFewTokens() throws Exception {
        LexerGrammar g = new LexerGrammar(
                "lexer grammar t;\n"+
				"ID : 'a'..'z'+;\n" +
				"INT : '0'..'9'+;\n" +
				"SEMI : ';';\n" +
				"ASSIGN : '=';\n" +
				"PLUS : '+';\n" +
				"MULT : '*';\n" +
				"WS : ' '+;\n");
        // Tokens: 012345678901234567
        // Input:  x = 3 * 0 + 2 * 0;
		TestingUnbufferedCharStream input = createStream("x = 302 * 91 + 20234234 * 0;");
        LexerInterpreter lexEngine = g.createLexerInterpreter(input);
		// copy text into tokens from char stream
		lexEngine.setTokenFactory(new CommonTokenFactory(true));
		CommonTokenStream tokens = new CommonTokenStream(lexEngine);
        String result = tokens.LT(1).getText();
        String expecting = "x";
        assertEquals(expecting, result);
		tokens.fill();
		expecting =
			"[[@0,0:0='x',<1>,1:0], [@1,1:1=' ',<7>,1:1], [@2,2:2='=',<4>,1:2]," +
			" [@3,3:3=' ',<7>,1:3], [@4,4:6='302',<2>,1:4], [@5,7:7=' ',<7>,1:7]," +
			" [@6,8:8='*',<6>,1:8], [@7,9:9=' ',<7>,1:9], [@8,10:11='91',<2>,1:10]," +
			" [@9,12:12=' ',<7>,1:12], [@10,13:13='+',<5>,1:13], [@11,14:14=' ',<7>,1:14]," +
			" [@12,15:22='20234234',<2>,1:15], [@13,23:23=' ',<7>,1:23]," +
			" [@14,24:24='*',<6>,1:24], [@15,25:25=' ',<7>,1:25], [@16,26:26='0',<2>,1:26]," +
			" [@17,27:27=';',<3>,1:27], [@18,28:27='',<-1>,1:28]]";
		assertEquals(expecting, tokens.getTokens().toString());
    }

	@Test public void testUnicodeSMP() throws Exception {
		TestingUnbufferedCharStream input = createStream("\uD83C\uDF0E");
		assertEquals(0x1F30E, input.LA(1));
		assertEquals("\uD83C\uDF0E", input.getBuffer());
		input.consume();
		assertEquals(IntStream.EOF, input.LA(1));
		assertEquals("\uFFFF", input.getBuffer());
	}

	@Test(expected = RuntimeException.class)
	public void testDanglingHighSurrogateAtEOFThrows() throws Exception {
		createStream("\uD83C");
	}

	@Test(expected = RuntimeException.class)
	public void testDanglingHighSurrogateThrows() throws Exception {
		createStream("\uD83C\u0123");
	}

	@Test(expected = RuntimeException.class)
	public void testDanglingLowSurrogateThrows() throws Exception {
		createStream("\uDF0E");
	}

	protected static TestingUnbufferedCharStream createStream(String text) {
		return new TestingUnbufferedCharStream(new StringReader(text));
	}

	protected static TestingUnbufferedCharStream createStream(String text, int bufferSize) {
		return new TestingUnbufferedCharStream(new StringReader(text), bufferSize);
	}

	protected static class TestingUnbufferedCharStream extends UnbufferedCharStream {

		public TestingUnbufferedCharStream(Reader input) {
			super(input);
		}

		public TestingUnbufferedCharStream(Reader input, int bufferSize) {
			super(input, bufferSize);
		}

		/** For testing.  What's in moving window into data stream from
		 *  current index, LA(1) or data[p], to end of buffer?
		 */
		public String getRemainingBuffer() {
			if ( n==0 ) return "";
			int len = n;
			if (data[len-1] == IntStream.EOF) {
				// Don't pass -1 to new String().
				return new String(data,p,len-p-1) + "\uFFFF";
			} else {
				return new String(data,p,len-p);
			}
		}

		/** For testing.  What's in moving window buffer into data stream.
		 *  From 0..p-1 have been consume.
		 */
		public String getBuffer() {
			if ( n==0 ) return "";
			int len = n;
			// Don't pass -1 to new String().
			if (data[len-1] == IntStream.EOF) {
				// Don't pass -1 to new String().
				return new String(data,0,len-1) + "\uFFFF";
			} else {
				return new String(data,0,len);
			}
		}

	}
}
