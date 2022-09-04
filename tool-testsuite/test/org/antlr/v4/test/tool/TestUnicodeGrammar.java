/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import org.antlr.v4.gui.Trees;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.LexerInterpreter;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.GrammarParserInterpreter;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUnicodeGrammar {
	@Test
	public void unicodeBMPLiteralInGrammar() throws Exception {
		String grammarText =
			"grammar Unicode;\n" +
			"r : 'hello' WORLD;\n" +
			"WORLD : ('world' | '\\u4E16\\u754C' | '\\u1000\\u1019\\u1039\\u1018\\u102C' );\n" +
			"WS : [ \\t\\r\\n]+ -> skip;\n";
		String inputText = "hello \u4E16\u754C";
		assertEquals(
				"(r:1 " + inputText + ")",
				parseTreeForGrammarWithInput(
						grammarText,
						"r",
						inputText));
	}

	// TODO: This test cannot pass unless we change either the grammar
	// parser to decode surrogate pair literals to code points (which
	// would break existing clients) or to treat them as an
	// alternative:
	//
	// '\\uD83C\\uDF0D' -> ('\\u{1F30E}' | '\\uD83C\\uDF0D')
	//
	// but I worry that might cause parse ambiguity if we're not careful.
	//@Test
	public void unicodeSurrogatePairLiteralInGrammar() throws Exception {
		String grammarText =
			"grammar Unicode;\n" +
			"r : 'hello' WORLD;\n" +
			"WORLD : ('\\uD83C\\uDF0D' | '\\uD83C\\uDF0E' | '\\uD83C\\uDF0F' );\n" +
			"WS : [ \\t\\r\\n]+ -> skip;\n";
		String inputText = new StringBuilder("hello ")
				.appendCodePoint(0x1F30E)
				.toString();
		assertEquals(
				"(r:1 " + inputText + ")",
				parseTreeForGrammarWithInput(
						grammarText,
						"r",
						inputText));
	}

	@Test
	public void unicodeSMPLiteralInGrammar() throws Exception {
		String grammarText =
			"grammar Unicode;\n" +
			"r : 'hello' WORLD;\n" +
			"WORLD : ('\\u{1F30D}' | '\\u{1F30E}' | '\\u{1F30F}' );\n" +
			"WS : [ \\t\\r\\n]+ -> skip;\n";
		String inputText = new StringBuilder("hello ")
				.appendCodePoint(0x1F30E)
				.toString();
		assertEquals(
				"(r:1 " + inputText + ")",
				parseTreeForGrammarWithInput(
						grammarText,
						"r",
						inputText));
	}

	@Test
	public void unicodeSMPRangeInGrammar() throws Exception {
		String grammarText =
			"grammar Unicode;\n" +
			"r : 'hello' WORLD;\n" +
			"WORLD : ('\\u{1F30D}'..'\\u{1F30F}' );\n" +
			"WS : [ \\t\\r\\n]+ -> skip;\n";
		String inputText = new StringBuilder("hello ")
				.appendCodePoint(0x1F30E)
				.toString();
		assertEquals(
				"(r:1 " + inputText + ")",
				parseTreeForGrammarWithInput(
						grammarText,
						"r",
						inputText));
	}

	@Test
	public void matchingDanglingSurrogateInInput() throws Exception {
		String grammarText =
			"grammar Unicode;\n" +
			"r : 'hello' WORLD;\n" +
			"WORLD : ('\\uD83C' | '\\uD83D' | '\\uD83E' );\n" +
			"WS : [ \\t\\r\\n]+ -> skip;\n";
		String inputText = "hello \uD83C";
		assertEquals(
				"(r:1 " + inputText + ")",
				parseTreeForGrammarWithInput(
						grammarText,
						"r",
						inputText));
	}

	@Test
	public void binaryGrammar() throws Exception {
		String grammarText =
			"grammar Binary;\n" +
			"r : HEADER PACKET+ FOOTER;\n" +
			"HEADER : '\\u0002\\u0000\\u0001\\u0007';\n" +
			"PACKET : '\\u00D0' ('\\u00D1' | '\\u00D2' | '\\u00D3') +;\n" +
			"FOOTER : '\\u00FF';\n";
		byte[] toParse = new byte[] {
				(byte)0x02, (byte)0x00, (byte)0x01, (byte)0x07,
				(byte)0xD0, (byte)0xD2, (byte)0xD2, (byte)0xD3, (byte)0xD3, (byte)0xD3,
				(byte)0xD0, (byte)0xD3, (byte)0xD3, (byte)0xD1,
				(byte)0xFF
		};
		CharStream charStream;
		try (ByteArrayInputStream is = new ByteArrayInputStream(toParse);
		     // Note we use ISO_8859_1 to treat all byte values as Unicode "characters" from
		     // U+0000 to U+00FF.
		     InputStreamReader isr = new InputStreamReader(is, StandardCharsets.ISO_8859_1)) {
			charStream = new ANTLRInputStream(isr);
		}
		Grammar grammar = new Grammar(grammarText);
		LexerInterpreter lexEngine = grammar.createLexerInterpreter(charStream);
		CommonTokenStream tokens = new CommonTokenStream(lexEngine);
		GrammarParserInterpreter parser = grammar.createGrammarParserInterpreter(tokens);
		ParseTree parseTree = parser.parse(grammar.rules.get("r").index);
		InterpreterTreeTextProvider nodeTextProvider =
				new InterpreterTreeTextProvider(grammar.getRuleNames());
		String result = Trees.toStringTree(parseTree, nodeTextProvider);

		assertEquals(
				"(r:1 \u0002\u0000\u0001\u0007 \u00D0\u00D2\u00D2\u00D3\u00D3\u00D3 \u00D0\u00D3\u00D3\u00D1 \u00FF)",
				result);
	}

	private static String parseTreeForGrammarWithInput(
			String grammarText,
			String rootRule,
			String inputText) throws Exception {
		Grammar grammar = new Grammar(grammarText);
		LexerInterpreter lexEngine = grammar.createLexerInterpreter(
				CharStreams.fromString(inputText));
		CommonTokenStream tokens = new CommonTokenStream(lexEngine);
		GrammarParserInterpreter parser = grammar.createGrammarParserInterpreter(tokens);
		ParseTree parseTree = parser.parse(grammar.rules.get(rootRule).index);
		InterpreterTreeTextProvider nodeTextProvider =
				new InterpreterTreeTextProvider(grammar.getRuleNames());
		return Trees.toStringTree(parseTree, nodeTextProvider);
	}
}
