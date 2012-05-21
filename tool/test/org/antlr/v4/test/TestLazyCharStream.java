package org.antlr.v4.test;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.LazyCharStream;
import org.antlr.v4.runtime.misc.Interval;
import org.junit.Test;

import java.io.StringReader;

public class TestLazyCharStream extends BaseTest {
	@Test public void testNoChar() throws Exception {
		CharStream input = new LazyCharStream(
				new StringReader("")
		);
		assertEquals(CharStream.EOF, input.LA(1));
		input.consume();
		assertEquals(CharStream.EOF, input.LA(1));
		input.consume();
		assertEquals(CharStream.EOF, input.LA(1));
	}

	@Test public void test1Char() throws Exception {
		CharStream input = new LazyCharStream(
				new StringReader("x")
		);
		assertEquals('x', input.LA(1));
		input.consume();
		assertEquals(CharStream.EOF, input.LA(1));
	}

	@Test public void test2Char() throws Exception {
		CharStream input = new LazyCharStream(
				new StringReader("xy")
		);
		assertEquals('x', input.LA(1));
		input.consume();
		assertEquals('y', input.LA(1));
		input.consume();
		assertEquals(CharStream.EOF, input.LA(1));
	}

    @Test public void test2CharAhead() throws Exception {
   		CharStream input = new LazyCharStream(
   				new StringReader("xy")
   		);
   		assertEquals('x', input.LA(1));
   		assertEquals('y', input.LA(2));
   		assertEquals(CharStream.EOF, input.LA(3));
   	}

    @Test public void testBufferExpand() throws Exception {
   		CharStream input = new LazyCharStream(
   				new StringReader("01234"),
                2 // buff size 2
   		);
   		assertEquals('0', input.LA(1));
        assertEquals('1', input.LA(2));
        assertEquals('2', input.LA(3));
        assertEquals('3', input.LA(4));
        assertEquals('4', input.LA(5));
   		assertEquals(CharStream.EOF, input.LA(6));
   	}

    @Test public void testBufferWrapSize1() throws Exception {
   		CharStream input = new LazyCharStream(
   				new StringReader("01234"),
                1 // buff size 1
   		);
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
   		assertEquals(CharStream.EOF, input.LA(1));
   	}

    @Test public void testBufferWrapSize2() throws Exception {
   		CharStream input = new LazyCharStream(
   				new StringReader("01234"),
                2 // buff size 2
   		);
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
   		assertEquals(CharStream.EOF, input.LA(1));
   	}

    @Test public void test1Mark() throws Exception {
   		CharStream input = new LazyCharStream(
   				new StringReader("xyz")
   		);
   		int m = input.mark();
   		assertEquals('x', input.LA(1));
   		assertEquals('y', input.LA(2));
   		assertEquals('z', input.LA(3));
   		input.release(m);
   		assertEquals(CharStream.EOF, input.LA(4));
   	}

    @Test public void test2Mark() throws Exception {
   		CharStream input = new LazyCharStream(
   				new StringReader("xyz"),
                2
   		);
   		assertEquals('x', input.LA(1));
        input.consume();
        int m1 = input.mark();
   		assertEquals('y', input.LA(1));
        input.consume();
        int m2 = input.mark();
   		assertEquals('z', input.LA(1));
        input.release(m2); // noop since not earliest in buf
        input.consume();
        input.release(m1);
   		assertEquals(CharStream.EOF, input.LA(1));
   	}

	@Test public void testLazy1CharAtTime() throws Exception {
		CharStream input = new LazyCharStream(
				new StringReader("x")
		);
		assertEquals(0, input.size());
		assertEquals("x", input.getText(Interval.of(0,0)));
		assertEquals(1, input.size());
		input.consume(); // x already loaded so size is still 1 after this
		input.consume(); // now we have x EOF
		assertEquals(2, input.size());
		assertEquals(CharStream.EOF, input.LA(1));
	}
}
