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
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.LexerInterpreter;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.test.runtime.java.BaseJavaTest;
import org.antlr.v4.tool.LexerGrammar;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestBufferedTokenStream extends BaseJavaTest {
	@Before
	@Override
	public void testSetUp() throws Exception {
		super.testSetUp();
	}

	protected TokenStream createTokenStream(TokenSource src) {
		return new BufferedTokenStream(src);
	}

	@Test public void testFirstToken() throws Exception {
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
        CharStream input = new ANTLRInputStream("x = 3 * 0 + 2 * 0;");
        LexerInterpreter lexEngine = g.createLexerInterpreter(input);
        TokenStream tokens = createTokenStream(lexEngine);

        String result = tokens.LT(1).getText();
        String expecting = "x";
        assertEquals(expecting, result);
    }

    @Test public void test2ndToken() throws Exception {
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
        CharStream input = new ANTLRInputStream("x = 3 * 0 + 2 * 0;");
        LexerInterpreter lexEngine = g.createLexerInterpreter(input);
        TokenStream tokens = createTokenStream(lexEngine);

        String result = tokens.LT(2).getText();
        String expecting = " ";
        assertEquals(expecting, result);
    }

    @Test public void testCompleteBuffer() throws Exception {
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
        CharStream input = new ANTLRInputStream("x = 3 * 0 + 2 * 0;");
        LexerInterpreter lexEngine = g.createLexerInterpreter(input);
        TokenStream tokens = createTokenStream(lexEngine);

        int i = 1;
        Token t = tokens.LT(i);
        while ( t.getType()!=Token.EOF ) {
            i++;
            t = tokens.LT(i);
        }
        tokens.LT(i++); // push it past end
        tokens.LT(i++);

        String result = tokens.getText();
        String expecting = "x = 3 * 0 + 2 * 0;";
        assertEquals(expecting, result);
    }

    @Test public void testCompleteBufferAfterConsuming() throws Exception {
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
        CharStream input = new ANTLRInputStream("x = 3 * 0 + 2 * 0;");
        LexerInterpreter lexEngine = g.createLexerInterpreter(input);
        TokenStream tokens = createTokenStream(lexEngine);

        Token t = tokens.LT(1);
        while ( t.getType()!=Token.EOF ) {
            tokens.consume();
            t = tokens.LT(1);
        }

        String result = tokens.getText();
        String expecting = "x = 3 * 0 + 2 * 0;";
        assertEquals(expecting, result);
    }

    @Test public void testLookback() throws Exception {
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
        CharStream input = new ANTLRInputStream("x = 3 * 0 + 2 * 0;");
        LexerInterpreter lexEngine = g.createLexerInterpreter(input);
        TokenStream tokens = createTokenStream(lexEngine);

        tokens.consume(); // get x into buffer
        Token t = tokens.LT(-1);
        assertEquals("x", t.getText());

        tokens.consume();
        tokens.consume(); // consume '='
        t = tokens.LT(-3);
        assertEquals("x", t.getText());
        t = tokens.LT(-2);
        assertEquals(" ", t.getText());
        t = tokens.LT(-1);
        assertEquals("=", t.getText());
    }

}
