package org.antlr.v4.test;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.UnbufferedTokenStream;
import org.antlr.v4.tool.LexerGrammar;
import org.antlr.v4.tool.interp.LexerInterpreter;
import org.junit.Test;

import java.io.StringReader;

public class TestUnbufferedTokenStream extends BaseTest {
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
        LexerInterpreter lexEngine = new LexerInterpreter(g);
			lexEngine.setInput(input);
        TokenStream<Token> tokens = new UnbufferedTokenStream<Token>(lexEngine);

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
        LexerInterpreter lexEngine = new LexerInterpreter(g);
			lexEngine.setInput(input);
		UnbufferedTokenStream<Token> tokens = new UnbufferedTokenStream<Token>(lexEngine);

		assertEquals("[[@0,0:0='x',<1>,1:0]]", tokens.getBuffer().toString());
		assertEquals("x", tokens.LT(1).getText());
		assertEquals("[[@0,0:0='x',<1>,1:0]]", tokens.getBuffer().toString());
		tokens.consume();
		assertEquals(" ", tokens.LT(1).getText());
		assertEquals("[[@1,1:1=' ',<7>,1:1]]", tokens.getBuffer().toString());
		tokens.consume();
		assertEquals("=", tokens.LT(1).getText());
		assertEquals("[[@2,2:2='=',<4>,1:2]]", tokens.getBuffer().toString());
		tokens.consume();
		assertEquals(" ", tokens.LT(1).getText());
		assertEquals("[[@3,3:3=' ',<7>,1:3]]", tokens.getBuffer().toString());
		tokens.consume();
		assertEquals("302", tokens.LT(1).getText());
		assertEquals("[[@4,4:6='302',<2>,1:4]]", tokens.getBuffer().toString());
		tokens.consume();
		assertEquals(";", tokens.LT(1).getText());
		assertEquals("[[@5,7:7=';',<3>,1:7]]", tokens.getBuffer().toString());
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
        LexerInterpreter lexEngine = new LexerInterpreter(g);
			lexEngine.setInput(input);
		UnbufferedTokenStream<Token> tokens = new UnbufferedTokenStream<Token>(lexEngine);

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
        LexerInterpreter lexEngine = new LexerInterpreter(g);
		lexEngine.setInput(input);
		UnbufferedTokenStream<Token> tokens = new UnbufferedTokenStream<Token>(lexEngine);

		int m = tokens.mark();
		assertEquals("[[@0,0:0='x',<1>,1:0]]", tokens.getBuffer().toString());
		assertEquals("x", tokens.LT(1).getText());
		tokens.consume(); // consume x
		assertEquals("[[@0,0:0='x',<1>,1:0], [@1,1:1=' ',<7>,1:1]]", tokens.getBuffer().toString());
		tokens.consume(); // ' '
		tokens.consume(); // =
		tokens.consume(); // ' '
		assertEquals("302", tokens.LT(1).getText());
		tokens.release(m); // "x = 302" is in buffer. next consume() should kill buffer
		tokens.consume(); // 302
		tokens.consume(); // ' '
		m = tokens.mark(); // mark at the +
		assertEquals("+", tokens.LT(1).getText());
		tokens.consume(); // '+'
		tokens.consume(); // ' '
		tokens.consume(); // 1
		tokens.consume(); // ;
		assertEquals("<EOF>", tokens.LT(1).getText());
		assertEquals("[[@6,8:8='+',<5>,1:8], [@7,9:9=' ',<7>,1:9]," +
					 " [@8,10:10='1',<2>,1:10], [@9,11:11=';',<3>,1:11]," +
					 " [@10,12:11='<EOF>',<-1>,1:12]]",
					 tokens.getBuffer().toString());
    }
}
