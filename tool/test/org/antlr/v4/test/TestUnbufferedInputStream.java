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

import org.antlr.v4.runtime.ANTLRUnbufferedInputStream;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.tool.LexerGrammar;
import org.antlr.v4.tool.interp.LexerInterpreter;
import org.junit.Test;

import java.io.StringBufferInputStream;

public class TestUnbufferedInputStream extends BaseTest {
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
        CharStream input = new ANTLRUnbufferedInputStream(
                new StringBufferInputStream("x = 3 * 0 + 2 * 0;")
        );
        LexerInterpreter lexEngine = new LexerInterpreter(g);
        lexEngine.setInput(input);
        BufferedTokenStream tokens = new BufferedTokenStream(lexEngine);
        System.out.println(tokens);
        String result = tokens.LT(1).getText();
        String expecting = "x";
        assertEquals(expecting, result);
    }


}
