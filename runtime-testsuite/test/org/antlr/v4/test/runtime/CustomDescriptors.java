/*
 * Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime;

import java.net.URI;
import java.nio.file.Paths;
import java.util.*;

public class CustomDescriptors {
	public final static HashMap<String, RuntimeTestDescriptor[]> descriptors;
	private final static URI uri;

	static {
		uri = Paths.get(RuntimeTestUtils.runtimeTestsuitePath.toString(),
						"test", "org", "antlr", "v4", "test", "runtime", "CustomDescriptors.java").toUri();

		descriptors = new HashMap<>();
		descriptors.put("LexerExec",
				new RuntimeTestDescriptor[]{
						getLineSeparatorLfDescriptor(),
						getLineSeparatorCrLfDescriptor(),
						getLargeLexerDescriptor(),
						getAtnStatesSizeMoreThan65535Descriptor()
				});
		descriptors.put("ParserExec",
				new RuntimeTestDescriptor[] {
						getMultiTokenAlternativeDescriptor(),
						getEscapedCharactersInTokenLiteralsDescriptor()
				});
	}

	private static RuntimeTestDescriptor getLineSeparatorLfDescriptor() {
		return new RuntimeTestDescriptor(
				GrammarType.Lexer,
				"LineSeparatorLf",
				"",
				"1\n2\n3",
				"[@0,0:0='1',<1>,1:0]\n" +
						"[@1,1:1='\\n',<2>,1:1]\n" +
						"[@2,2:2='2',<1>,2:0]\n" +
						"[@3,3:3='\\n',<2>,2:1]\n" +
						"[@4,4:4='3',<1>,3:0]\n" +
						"[@5,5:4='<EOF>',<-1>,3:1]\n",
				"",
				null,
				"L",
				"lexer grammar L;\n" +
						"T: ~'\\n'+;\n" +
						"SEPARATOR: '\\n';",
				null, false, false, null, uri);
	}

	private static RuntimeTestDescriptor getLineSeparatorCrLfDescriptor() {
		return new RuntimeTestDescriptor(
				GrammarType.Lexer,
				"LineSeparatorCrLf",
				"",
				"1\r\n2\r\n3",
				"[@0,0:0='1',<1>,1:0]\n" +
						"[@1,1:2='\\r\\n',<2>,1:1]\n" +
						"[@2,3:3='2',<1>,2:0]\n" +
						"[@3,4:5='\\r\\n',<2>,2:1]\n" +
						"[@4,6:6='3',<1>,3:0]\n" +
						"[@5,7:6='<EOF>',<-1>,3:1]\n",
				"",
				"",
				"L",
				"lexer grammar L;\n" +
						"T: ~'\\r'+;\n" +
						"SEPARATOR: '\\r\\n';",
				null, false, false, null, uri);
	}

	private static RuntimeTestDescriptor getLargeLexerDescriptor() {
		final int tokensCount = 4000;
		final String grammarName = "L";

		StringBuilder grammar = new StringBuilder();
		grammar.append("lexer grammar ").append(grammarName).append(";\n");
		grammar.append("WS: [ \\t\\r\\n]+ -> skip;\n");
		for (int i = 0; i < tokensCount; i++) {
			grammar.append("KW").append(i).append(" : 'KW' '").append(i).append("';\n");
		}

		return new RuntimeTestDescriptor(
				GrammarType.Lexer,
				"LargeLexer",
				"This is a regression test for antlr/antlr4#76 \"Serialized ATN strings\n" +
						"should be split when longer than 2^16 bytes (class file limitation)\"\n" +
						"https://github.com/antlr/antlr4/issues/76",
				"KW400",
				"[@0,0:4='KW400',<402>,1:0]\n" +
						"[@1,5:4='<EOF>',<-1>,1:5]\n",
				"",
				"",
				grammarName,
				grammar.toString(),
				null, false, false, null, uri);
	}

	private static RuntimeTestDescriptor getAtnStatesSizeMoreThan65535Descriptor() {
		// I tried playing around with different sizes, and I think 1002 works for Go but 1003 does not;
		// the executing lexer gets a token syntax error for T208 or something like that
		final int tokensCount = 1024;
		final String suffix = String.join("", Collections.nCopies(70, "_"));

		final String grammarName = "L";
		StringBuilder grammar = new StringBuilder();
		grammar.append("lexer grammar ").append(grammarName).append(";\n");
		grammar.append('\n');
		StringBuilder input = new StringBuilder();
		StringBuilder output = new StringBuilder();
		int startOffset;
		int stopOffset = -2;
		for (int i = 0; i < tokensCount; i++) {
			String ruleName = String.format("T_%06d", i);
			String value = ruleName+suffix;
			grammar.append(ruleName).append(": '").append(value).append("';\n");
			input.append(value).append('\n');

			startOffset = stopOffset + 2;
			stopOffset += value.length() + 1;

			output.append("[@").append(i).append(',').append(startOffset).append(':').append(stopOffset)
					.append("='").append(value).append("',<").append(i + 1).append(">,").append(i + 1)
					.append(":0]\n");
		}

		grammar.append("\n");
		grammar.append("WS: [ \\t\\r\\n]+ -> skip;\n");

		startOffset = stopOffset + 2;
		stopOffset = startOffset - 1;
		output.append("[@").append(tokensCount).append(',').append(startOffset).append(':').append(stopOffset)
				.append("='<EOF>',<-1>,").append(tokensCount + 1).append(":0]\n");

		return new RuntimeTestDescriptor(
				GrammarType.Lexer,
				"AtnStatesSizeMoreThan65535",
				"Regression for https://github.com/antlr/antlr4/issues/1863",
				input.toString(),
				output.toString(),
				"",
				"",
				grammarName,
				grammar.toString(),
				null, false, false,
				new String[] {"CSharp", "Python2", "Python3", "Go", "PHP", "Swift", "JavaScript", "Dart"}, uri);
	}

	private static RuntimeTestDescriptor getMultiTokenAlternativeDescriptor() {
		final int tokensCount = 64;

		StringBuilder rule = new StringBuilder("r1: ");
		StringBuilder tokens = new StringBuilder();
		StringBuilder input = new StringBuilder();
		StringBuilder output = new StringBuilder();

		for (int i = 0; i < tokensCount; i++) {
			String currentToken = "T" + i;
			rule.append(currentToken);
			if (i < tokensCount - 1) {
				rule.append(" | ");
			} else {
				rule.append(";");
			}
			tokens.append(currentToken).append(": '").append(currentToken).append("';\n");
			input.append(currentToken).append(" ");
			output.append(currentToken);
		}
		String currentToken = "T" + tokensCount;
		tokens.append(currentToken).append(": '").append(currentToken).append("';\n");
		input.append(currentToken).append(" ");
		output.append(currentToken);

		String grammar = "grammar P;\n" +
				"r: (r1 | T" + tokensCount + ")+ EOF {<writeln(\"$text\")>};\n" +
				rule + "\n" +
				tokens + "\n" +
				"WS: [ ]+ -> skip;";

		return new RuntimeTestDescriptor(
				GrammarType.Parser,
				"MultiTokenAlternative",
				"https://github.com/antlr/antlr4/issues/3698, https://github.com/antlr/antlr4/issues/3703",
				input.toString(),
				output + "\n",
				"",
				"r",
				"P",
				grammar,
				null, false, false, null, uri);
	}

	private static RuntimeTestDescriptor getEscapedCharactersInTokenLiteralsDescriptor() {
		String grammar = "grammar EscapedCharactersInTokenLiterals;\n" +
				"\n" +
				"r: t+ {<write(\"$text\")>};\n" +
				"t\n" +
				"    : '\\t'\n" +
				"    | '\\b'\n" +
				"    | '\\n'\n" +
				"    | '\\r'\n" +
				"    | '\\f'\n" +
				"    | '\\''\n" +
				"    | '\"'\n" +
				"    | '\\\\\\\\'\n" +
				"    | '\\u0007'\n" +
				"    | '\\u000B'\n" +
				"    | '\\u001B'\n" +
				"    | '?'\n" +
				"    | '\\u0000'\n" +
				"    | '$'\n" +
				"    | 'X'\n" +
				"    ;\n" +
				"\n" +
				"// Java\n" +
				"TAB: '\\t';\n" +
				"BACK_SPACE: '\\b';\n" +
				"LINE_FEED: '\\n';\n" +
				"CARRIAGE_RETURN: '\\r';\n" +
				"FORM_FEED: '\\f';\n" +
				"QUOTE: '\\'';\n" +
				"DOUBLE_QUOTE: '\"';\n" +
				"BACK_SLASH: '\\\\\\\\';\n" +
				"\n" +
				"// C++\n" +
				"ALERT: '\\u0007';\n" +
				"VERTICAL_TAB: '\\u000B';\n" +
				"ESCAPE: '\\u001B';\n" +
				"QUESTION: '?';\n" +
				"\n" +
				"// C#\n" +
				"NULL_CHAR: '\\u0000';\n" +
				"\n" +
				"// Dart & PHP\n" +
				"DOLLAR: '$';\n" +
				"\n" +
				"// NORMAL:\n" +
				"X: 'X';\n" +
				"\n";

		String input = "\t\b\n\f'\"\\\u0007\u000B\u001B?\u0000$X";

		return new RuntimeTestDescriptor(
				GrammarType.Parser,
				"EscapedCharactersInTokenLiterals",
				"https://github.com/antlr/antlr4/issues/2281, https://github.com/antlr/antlr4/issues/2885",
				input,
				input,
				"",
				"r",
				"EscapedCharactersInTokenLiterals",
				grammar,
				null, false, false, null, uri);
	}
}
