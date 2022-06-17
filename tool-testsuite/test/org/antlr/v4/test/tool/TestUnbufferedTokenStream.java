/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.LexerInterpreter;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.UnbufferedTokenStream;
import org.antlr.v4.tool.LexerGrammar;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("unused")
public class TestUnbufferedTokenStream {
	@Test
	public void testLookahead() throws Exception {
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
