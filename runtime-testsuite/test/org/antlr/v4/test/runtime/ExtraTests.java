package org.antlr.v4.test.runtime;

public class ExtraTests {
	static RuntimeTestDescriptor getLineSeparatorLfTest(String targetName) {
		UniversalRuntimeTestDescriptor result = new UniversalRuntimeTestDescriptor();
		result.name = "LineSeparatorLf";
		result.targetName = targetName;
		result.testType = "Lexer";
		result.grammar = "lexer grammar L;\n" +
				"T: ~'\\n'+;\n" +
				"SEPARATOR: '\\n';";
		result.grammarName = "L";
		result.input = "1\n2\n3";
		result.output = "[@0,0:0='1',<1>,1:0]\n" +
				"[@1,1:1='\\n',<2>,1:1]\n" +
				"[@2,2:2='2',<1>,2:0]\n" +
				"[@3,3:3='\\n',<2>,2:1]\n" +
				"[@4,4:4='3',<1>,3:0]\n" +
				"[@5,5:4='<EOF>',<-1>,3:1]\n";
		return result;
	}

	static RuntimeTestDescriptor getLineSeparatorCrLfTest(String targetName) {
		UniversalRuntimeTestDescriptor result = new UniversalRuntimeTestDescriptor();
		result.name = "LineSeparatorCrLf";
		result.targetName = targetName;
		result.testType = "Lexer";
		result.grammar = "lexer grammar L;\n" +
				"T: ~'\\r'+;\n" +
				"SEPARATOR: '\\r\\n';";
		result.grammarName = "L";
		result.input = "1\r\n2\r\n3";
		result.output = "[@0,0:0='1',<1>,1:0]\n" +
				"[@1,1:2='\\r\\n',<2>,1:1]\n" +
				"[@2,3:3='2',<1>,2:0]\n" +
				"[@3,4:5='\\r\\n',<2>,2:1]\n" +
				"[@4,6:6='3',<1>,3:0]\n" +
				"[@5,7:6='<EOF>',<-1>,3:1]\n";
		return result;
	}

	static RuntimeTestDescriptor getLargeLexerDescriptor(String targetName) {
		UniversalRuntimeTestDescriptor result = new UniversalRuntimeTestDescriptor();
		result.name = "LargeLexer";
		result.notes = "This is a regression test for antlr/antlr4#76 \"Serialized ATN strings\n" +
				"should be split when longer than 2^16 bytes (class file limitation)\"\n" +
				"https://github.com/antlr/antlr4/issues/76";
		result.targetName = targetName;
		result.testType = "Lexer";

		final int tokensCount = 4000;

		String grammarName = "L";
		StringBuilder grammar = new StringBuilder();
		grammar.append("lexer grammar ").append(grammarName).append(";\n");
		grammar.append("WS: [ \\t\\r\\n]+ -> skip;\n");
		for (int i = 0; i < tokensCount; i++) {
			grammar.append("KW").append(i).append(" : 'KW' '").append(i).append("';\n");
		}

		result.grammar = grammar.toString();
		result.grammarName = grammarName;
		result.input = "KW400";
		result.output = "[@0,0:4='KW400',<402>,1:0]\n" +
				"[@1,5:4='<EOF>',<-1>,1:5]\n";
		return result;
	}
}
