/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.antlr.v4.test;

import org.antlr.v4.runtime.TokenRewriteStream;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.tool.LexerGrammar;
import org.antlr.v4.tool.interp.LexerInterpreter;
import org.junit.Test;

public class TestTokenRewriteStream extends BaseTest {

    /** Public default constructor used by TestRig */
    public TestTokenRewriteStream() {
    }

	@Test public void testInsertBeforeIndex0() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		LexerInterpreter lexInterp = new LexerInterpreter(g, "abc");
		TokenRewriteStream tokens = new TokenRewriteStream(lexInterp);
		tokens.insertBefore(0, "0");
		String result = tokens.toString();
		String expecting = "0abc";
		assertEquals(expecting, result);
	}

	@Test public void testInsertAfterLastIndex() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abc";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.insertAfter(2, "x");
		String result = tokens.toString();
		String expecting = "abcx";
		assertEquals(expecting, result);
	}

	@Test public void test2InsertBeforeAfterMiddleIndex() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abc";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.fill();
		tokens.insertBefore(1, "x");
		tokens.insertAfter(1, "x");
		String result = tokens.toString();
		String expecting = "axbxc";
		assertEquals(expecting, result);
	}

	@Test public void testReplaceIndex0() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abc";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.fill();
		tokens.replace(0, "x");
		String result = tokens.toString();
		String expecting = "xbc";
		assertEquals(expecting, result);
	}

	@Test public void testReplaceLastIndex() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abc";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.fill();
		tokens.replace(2, "x");
		String result = tokens.toString();
		String expecting = "abx";
		assertEquals(expecting, result);
	}

	@Test public void testReplaceMiddleIndex() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abc";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.fill();
		tokens.replace(1, "x");
		String result = tokens.toString();
		String expecting = "axc";
		assertEquals(expecting, result);
	}

    @Test public void testToStringStartStop() throws Exception {
        LexerGrammar g = new LexerGrammar(
            "lexer grammar t;\n"+
            "ID : 'a'..'z'+;\n" +
            "INT : '0'..'9'+;\n" +
            "SEMI : ';';\n" +
            "MUL : '*';\n" +
            "ASSIGN : '=';\n" +
            "WS : ' '+;\n");
        // Tokens: 0123456789
        // Input:  x = 3 * 0;
        String input = "x = 3 * 0;";
        LexerInterpreter lexEngine = new LexerInterpreter(g, input);
        TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
        tokens.fill();
        tokens.replace(4, 8, "0"); // replace 3 * 0 with 0

        String result = tokens.toOriginalString();
        String expecting = "x = 3 * 0;";
        assertEquals(expecting, result);

        result = tokens.toString();
        expecting = "x = 0;";
        assertEquals(expecting, result);

        result = tokens.getText(Interval.of(0, 9));
        expecting = "x = 0;";
        assertEquals(expecting, result);

        result = tokens.getText(Interval.of(4, 8));
        expecting = "0";
        assertEquals(expecting, result);
    }

    @Test public void testToStringStartStop2() throws Exception {
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
        String input = "x = 3 * 0 + 2 * 0;";
        LexerInterpreter lexEngine = new LexerInterpreter(g, input);
        TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
        tokens.fill();

        String result = tokens.toOriginalString();
        String expecting = "x = 3 * 0 + 2 * 0;";
        assertEquals(expecting, result);

        tokens.replace(4, 8, "0"); // replace 3 * 0 with 0
        result = tokens.toString();
        expecting = "x = 0 + 2 * 0;";
        assertEquals(expecting, result);

        result = tokens.getText(Interval.of(0, 17));
        expecting = "x = 0 + 2 * 0;";
        assertEquals(expecting, result);

        result = tokens.getText(Interval.of(4, 8));
        expecting = "0";
        assertEquals(expecting, result);

        result = tokens.getText(Interval.of(0, 8));
        expecting = "x = 0";
        assertEquals(expecting, result);

        result = tokens.getText(Interval.of(12, 16));
        expecting = "2 * 0";
        assertEquals(expecting, result);

        tokens.insertAfter(17, "// comment");
        result = tokens.getText(Interval.of(12, 18));
        expecting = "2 * 0;// comment";
        assertEquals(expecting, result);

        result = tokens.getText(Interval.of(0, 8)); // try again after insert at end
        expecting = "x = 0";
        assertEquals(expecting, result);
    }


    @Test public void test2ReplaceMiddleIndex() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abc";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.fill();
		tokens.replace(1, "x");
		tokens.replace(1, "y");
		String result = tokens.toString();
		String expecting = "ayc";
		assertEquals(expecting, result);
	}

    @Test public void test2ReplaceMiddleIndex1InsertBefore() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abc";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.fill();
        tokens.insertBefore(0, "_");
        tokens.replace(1, "x");
		tokens.replace(1, "y");
		String result = tokens.toString();
		String expecting = "_ayc";
		assertEquals(expecting, result);
	}

	@Test public void testReplaceThenDeleteMiddleIndex() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abc";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.fill();
		tokens.replace(1, "x");
		tokens.delete(1);
		String result = tokens.toString();
		String expecting = "ac";
		assertEquals(expecting, result);
	}

	@Test public void testInsertInPriorReplace() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abc";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.fill();
		tokens.replace(0, 2, "x");
		tokens.insertBefore(1, "0");
		Exception exc = null;
		try {
			tokens.toString();
		}
		catch (IllegalArgumentException iae) {
			exc = iae;
		}
		String expecting = "insert op <InsertBeforeOp@[@1,1:1='b',<4>,1:1]:\"0\"> within boundaries of previous <ReplaceOp@[@0,0:0='a',<3>,1:0]..[@2,2:2='c',<5>,1:2]:\"x\">";
		assertNotNull(exc);
		assertEquals(expecting, exc.getMessage());
	}

	@Test public void testInsertThenReplaceSameIndex() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abc";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.fill();
		tokens.insertBefore(0, "0");
		tokens.replace(0, "x"); // supercedes insert at 0
		String result = tokens.toString();
		String expecting = "0xbc";
		assertEquals(expecting, result);
	}

	@Test public void test2InsertMiddleIndex() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abc";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.fill();
		tokens.insertBefore(1, "x");
		tokens.insertBefore(1, "y");
		String result = tokens.toString();
		String expecting = "ayxbc";
		assertEquals(expecting, result);
	}

	@Test public void test2InsertThenReplaceIndex0() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abc";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.fill();
		tokens.insertBefore(0, "x");
		tokens.insertBefore(0, "y");
		tokens.replace(0, "z");
		String result = tokens.toString();
		String expecting = "yxzbc";
		assertEquals(expecting, result);
	}

	@Test public void testReplaceThenInsertBeforeLastIndex() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abc";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.fill();
		tokens.replace(2, "x");
		tokens.insertBefore(2, "y");
		String result = tokens.toString();
		String expecting = "abyx";
		assertEquals(expecting, result);
	}

	@Test public void testInsertThenReplaceLastIndex() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abc";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.fill();
		tokens.insertBefore(2, "y");
		tokens.replace(2, "x");
		String result = tokens.toString();
		String expecting = "abyx";
		assertEquals(expecting, result);
	}

	@Test public void testReplaceThenInsertAfterLastIndex() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abc";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.fill();
		tokens.replace(2, "x");
		tokens.insertAfter(2, "y");
		String result = tokens.toString();
		String expecting = "abxy";
		assertEquals(expecting, result);
	}

	@Test public void testReplaceRangeThenInsertAtLeftEdge() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abcccba";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.fill();
		tokens.replace(2, 4, "x");
		tokens.insertBefore(2, "y");
		String result = tokens.toString();
		String expecting = "abyxba";
		assertEquals(expecting, result);
	}

	@Test public void testReplaceRangeThenInsertAtRightEdge() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abcccba";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.fill();
		tokens.replace(2, 4, "x");
		tokens.insertBefore(4, "y"); // no effect; within range of a replace
		Exception exc = null;
		try {
			tokens.toString();
		}
		catch (IllegalArgumentException iae) {
			exc = iae;
		}
		String expecting = "insert op <InsertBeforeOp@[@4,4:4='c',<5>,1:4]:\"y\"> within boundaries of previous <ReplaceOp@[@2,2:2='c',<5>,1:2]..[@4,4:4='c',<5>,1:4]:\"x\">";
		assertNotNull(exc);
		assertEquals(expecting, exc.getMessage());
	}

	@Test public void testReplaceRangeThenInsertAfterRightEdge() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abcccba";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.fill();
		tokens.replace(2, 4, "x");
		tokens.insertAfter(4, "y");
		String result = tokens.toString();
		String expecting = "abxyba";
		assertEquals(expecting, result);
	}

	@Test public void testReplaceAll() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abcccba";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.fill();
		tokens.replace(0, 6, "x");
		String result = tokens.toString();
		String expecting = "x";
		assertEquals(expecting, result);
	}

	@Test public void testReplaceSubsetThenFetch() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abcccba";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.fill();
		tokens.replace(2, 4, "xyz");
		String result = tokens.getText(Interval.of(0, 6));
		String expecting = "abxyzba";
		assertEquals(expecting, result);
	}

	@Test public void testReplaceThenReplaceSuperset() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abcccba";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.fill();
		tokens.replace(2, 4, "xyz");
		tokens.replace(3, 5, "foo"); // overlaps, error
		Exception exc = null;
		try {
			tokens.toString();
		}
		catch (IllegalArgumentException iae) {
			exc = iae;
		}
		String expecting = "replace op boundaries of <ReplaceOp@[@3,3:3='c',<5>,1:3]..[@5,5:5='b',<4>,1:5]:\"foo\"> overlap with previous <ReplaceOp@[@2,2:2='c',<5>,1:2]..[@4,4:4='c',<5>,1:4]:\"xyz\">";
		assertNotNull(exc);
		assertEquals(expecting, exc.getMessage());
	}

	@Test public void testReplaceThenReplaceLowerIndexedSuperset() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abcccba";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.fill();
		tokens.replace(2, 4, "xyz");
		tokens.replace(1, 3, "foo"); // overlap, error
		Exception exc = null;
		try {
			tokens.toString();
		}
		catch (IllegalArgumentException iae) {
			exc = iae;
		}
		String expecting = "replace op boundaries of <ReplaceOp@[@1,1:1='b',<4>,1:1]..[@3,3:3='c',<5>,1:3]:\"foo\"> overlap with previous <ReplaceOp@[@2,2:2='c',<5>,1:2]..[@4,4:4='c',<5>,1:4]:\"xyz\">";
		assertNotNull(exc);
		assertEquals(expecting, exc.getMessage());
	}

	@Test public void testReplaceSingleMiddleThenOverlappingSuperset() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abcba";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.fill();
		tokens.replace(2, 2, "xyz");
		tokens.replace(0, 3, "foo");
		String result = tokens.toString();
		String expecting = "fooa";
		assertEquals(expecting, result);
	}

	// June 2, 2008 I rewrote core of rewrite engine; just adding lots more tests here

	@Test public void testCombineInserts() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abc";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.fill();
		tokens.insertBefore(0, "x");
		tokens.insertBefore(0, "y");
		String result = tokens.toString();
		String expecting = "yxabc";
		assertEquals(expecting, result);
	}

	@Test public void testCombine3Inserts() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abc";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.fill();
		tokens.insertBefore(1, "x");
		tokens.insertBefore(0, "y");
		tokens.insertBefore(1, "z");
		String result = tokens.toString();
		String expecting = "yazxbc";
		assertEquals(expecting, result);
	}

	@Test public void testCombineInsertOnLeftWithReplace() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abc";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.fill();
		tokens.replace(0, 2, "foo");
		tokens.insertBefore(0, "z"); // combine with left edge of rewrite
		String result = tokens.toString();
		String expecting = "zfoo";
		assertEquals(expecting, result);
	}

	@Test public void testCombineInsertOnLeftWithDelete() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abc";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.fill();
		tokens.delete(0, 2);
		tokens.insertBefore(0, "z"); // combine with left edge of rewrite
		String result = tokens.toString();
		String expecting = "z"; // make sure combo is not znull
		assertEquals(expecting, result);
	}

	@Test public void testDisjointInserts() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abc";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.fill();
		tokens.insertBefore(1, "x");
		tokens.insertBefore(2, "y");
		tokens.insertBefore(0, "z");
		String result = tokens.toString();
		String expecting = "zaxbyc";
		assertEquals(expecting, result);
	}

	@Test public void testOverlappingReplace() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abcc";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.fill();
		tokens.replace(1, 2, "foo");
		tokens.replace(0, 3, "bar"); // wipes prior nested replace
		String result = tokens.toString();
		String expecting = "bar";
		assertEquals(expecting, result);
	}

	@Test public void testOverlappingReplace2() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abcc";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.fill();
		tokens.replace(0, 3, "bar");
		tokens.replace(1, 2, "foo"); // cannot split earlier replace
		Exception exc = null;
		try {
			tokens.toString();
		}
		catch (IllegalArgumentException iae) {
			exc = iae;
		}
		String expecting = "replace op boundaries of <ReplaceOp@[@1,1:1='b',<4>,1:1]..[@2,2:2='c',<5>,1:2]:\"foo\"> overlap with previous <ReplaceOp@[@0,0:0='a',<3>,1:0]..[@3,3:3='c',<5>,1:3]:\"bar\">";
		assertNotNull(exc);
		assertEquals(expecting, exc.getMessage());
	}

	@Test public void testOverlappingReplace3() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abcc";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.fill();
		tokens.replace(1, 2, "foo");
		tokens.replace(0, 2, "bar"); // wipes prior nested replace
		String result = tokens.toString();
		String expecting = "barc";
		assertEquals(expecting, result);
	}

	@Test public void testOverlappingReplace4() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abcc";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.fill();
		tokens.replace(1, 2, "foo");
		tokens.replace(1, 3, "bar"); // wipes prior nested replace
		String result = tokens.toString();
		String expecting = "abar";
		assertEquals(expecting, result);
	}

	@Test public void testDropIdenticalReplace() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abcc";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.fill();
		tokens.replace(1, 2, "foo");
		tokens.replace(1, 2, "foo"); // drop previous, identical
		String result = tokens.toString();
		String expecting = "afooc";
		assertEquals(expecting, result);
	}

	@Test public void testDropPrevCoveredInsert() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abc";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.fill();
		tokens.insertBefore(1, "foo");
		tokens.replace(1, 2, "foo"); // kill prev insert
		String result = tokens.toString();
		String expecting = "afoofoo";
		assertEquals(expecting, result);
	}

	@Test public void testLeaveAloneDisjointInsert() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abcc";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.fill();
		tokens.insertBefore(1, "x");
		tokens.replace(2, 3, "foo");
		String result = tokens.toString();
		String expecting = "axbfoo";
		assertEquals(expecting, result);
	}

	@Test public void testLeaveAloneDisjointInsert2() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abcc";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.fill();
		tokens.replace(2, 3, "foo");
		tokens.insertBefore(1, "x");
		String result = tokens.toString();
		String expecting = "axbfoo";
		assertEquals(expecting, result);
	}

	@Test public void testInsertBeforeTokenThenDeleteThatToken() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar t;\n"+
			"A : 'a';\n" +
			"B : 'b';\n" +
			"C : 'c';\n");
		String input = "abc";
		LexerInterpreter lexEngine = new LexerInterpreter(g, input);
		TokenRewriteStream tokens = new TokenRewriteStream(lexEngine);
		tokens.fill();
		tokens.insertBefore(2, "y");
		tokens.delete(2);
		String result = tokens.toString();
		String expecting = "aby";
		assertEquals(expecting, result);
	}

}
