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

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.LexerInterpreter;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.UnbufferedTokenStream;
import org.antlr.v4.test.runtime.java.BaseJavaTest;
import org.antlr.v4.tool.LexerGrammar;
import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("unused")
public class TestUnbufferedTokenStream extends BaseJavaTest {
	@Before
	@Override
	public void testSetUp() throws Exception {
		super.testSetUp();
	}

	@Test public void testLookahead() throws Exception {
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
        // Input:  x = 302;
        CharStream input = new ANTLRInputStream(
			new StringReader("x = 302;")
		);
        LexerInterpreter lexEngine = g.createLexerInterpreter(input);
        TokenStream tokens = new UnbufferedTokenStream<Token>(lexEngine);

		assertEquals("x", tokens.LT(1).getText());
		assertEquals(" ", tokens.LT(2).getText());
		assertEquals("=", tokens.LT(3).getText());
		assertEquals(" ", tokens.LT(4).getText());
		assertEquals("302", tokens.LT(5).getText());
		assertEquals(";", tokens.LT(6).getText());
    }

	@Test public void testNoBuffering() throws Exception {
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
        // Input:  x = 302;
        CharStream input = new ANTLRInputStream(
			new StringReader("x = 302;")
		);
        LexerInterpreter lexEngine = g.createLexerInterpreter(input);
		TestingUnbufferedTokenStream<Token> tokens = new TestingUnbufferedTokenStream<Token>(lexEngine);

		assertEquals("[[@0,0:0='x',<1>,1:0]]", tokens.getBuffer().toString());
		assertEquals("x", tokens.LT(1).getText());
		tokens.consume(); // move to WS
		assertEquals(" ", tokens.LT(1).getText());
		assertEquals("[[@1,1:1=' ',<7>,1:1]]", tokens.getRemainingBuffer().toString());
		tokens.consume();
		assertEquals("=", tokens.LT(1).getText());
		assertEquals("[[@2,2:2='=',<4>,1:2]]", tokens.getRemainingBuffer().toString());
		tokens.consume();
		assertEquals(" ", tokens.LT(1).getText());
		assertEquals("[[@3,3:3=' ',<7>,1:3]]", tokens.getRemainingBuffer().toString());
		tokens.consume();
		assertEquals("302", tokens.LT(1).getText());
		assertEquals("[[@4,4:6='302',<2>,1:4]]", tokens.getRemainingBuffer().toString());
		tokens.consume();
		assertEquals(";", tokens.LT(1).getText());
		assertEquals("[[@5,7:7=';',<3>,1:7]]", tokens.getRemainingBuffer().toString());
    }

	@Test public void testMarkStart() throws Exception {
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
        // Input:  x = 302;
        CharStream input = new ANTLRInputStream(
			new StringReader("x = 302;")
		);
        LexerInterpreter lexEngine = g.createLexerInterpreter(input);
		TestingUnbufferedTokenStream<Token> tokens = new TestingUnbufferedTokenStream<Token>(lexEngine);

		int m = tokens.mark();
		assertEquals("[[@0,0:0='x',<1>,1:0]]", tokens.getBuffer().toString());
		assertEquals("x", tokens.LT(1).getText());
		tokens.consume(); // consume x
		assertEquals("[[@0,0:0='x',<1>,1:0], [@1,1:1=' ',<7>,1:1]]", tokens.getBuffer().toString());
		tokens.consume(); // ' '
		tokens.consume(); // =
		tokens.consume(); // ' '
		tokens.consume(); // 302
		tokens.consume(); // ;
		assertEquals("[[@0,0:0='x',<1>,1:0], [@1,1:1=' ',<7>,1:1]," +
					 " [@2,2:2='=',<4>,1:2], [@3,3:3=' ',<7>,1:3]," +
					 " [@4,4:6='302',<2>,1:4], [@5,7:7=';',<3>,1:7]," +
					 " [@6,8:7='<EOF>',<-1>,1:8]]",
					 tokens.getBuffer().toString());
    }

	@Test public void testMarkThenRelease() throws Exception {
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
        // Input:  x = 302;
        CharStream input = new ANTLRInputStream(
			new StringReader("x = 302 + 1;")
		);
        LexerInterpreter lexEngine = g.createLexerInterpreter(input);
		TestingUnbufferedTokenStream<Token> tokens = new TestingUnbufferedTokenStream<Token>(lexEngine);

		int m = tokens.mark();
		assertEquals("[[@0,0:0='x',<1>,1:0]]", tokens.getBuffer().toString());
		assertEquals("x", tokens.LT(1).getText());
		tokens.consume(); // consume x
		assertEquals("[[@0,0:0='x',<1>,1:0], [@1,1:1=' ',<7>,1:1]]", tokens.getBuffer().toString());
		tokens.consume(); // ' '
		tokens.consume(); // =
		tokens.consume(); // ' '
		assertEquals("302", tokens.LT(1).getText());
		tokens.release(m); // "x = 302" is in buffer. will kill buffer
		tokens.consume(); // 302
		tokens.consume(); // ' '
		m = tokens.mark(); // mark at the +
		assertEquals("+", tokens.LT(1).getText());
		tokens.consume(); // '+'
		tokens.consume(); // ' '
		tokens.consume(); // 1
		tokens.consume(); // ;
		assertEquals("<EOF>", tokens.LT(1).getText());
		// we marked at the +, so that should be the start of the buffer
		assertEquals("[[@6,8:8='+',<5>,1:8], [@7,9:9=' ',<7>,1:9]," +
					 " [@8,10:10='1',<2>,1:10], [@9,11:11=';',<3>,1:11]," +
					 " [@10,12:11='<EOF>',<-1>,1:12]]",
					 tokens.getBuffer().toString());
		tokens.release(m);
    }

	protected static class TestingUnbufferedTokenStream<T extends Token> extends UnbufferedTokenStream<T> {

		public TestingUnbufferedTokenStream(TokenSource tokenSource) {
			super(tokenSource);
		}

		/** For testing.  What's in moving window into token stream from
		 *  current index, LT(1) or tokens[p], to end of buffer?
		 */
		protected List<? extends Token> getRemainingBuffer() {
			if ( n==0 ) {
				return Collections.emptyList();
			}

			return Arrays.asList(tokens).subList(p, n);
		}

		/** For testing.  What's in moving window buffer into data stream.
		 *  From 0..p-1 have been consume.
		 */
		protected List<? extends Token> getBuffer() {
			if ( n==0 ) {
				return Collections.emptyList();
			}

			return Arrays.asList(tokens).subList(0, n);
		}

	}
}
