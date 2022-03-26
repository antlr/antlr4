package org.antlr.v4.test.runtime;

import java.util.*;

public class GeneratedLexerDescriptors {
	static RuntimeTestDescriptor getLineSeparatorLfDescriptor(String targetName) {
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

	static RuntimeTestDescriptor getLineSeparatorCrLfDescriptor(String targetName) {
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

	static RuntimeTestDescriptor getAtnStatesSizeMoreThan65535Descriptor(String targetName) {
		UniversalRuntimeTestDescriptor result = new UniversalRuntimeTestDescriptor();
		result.name = "AtnStatesSizeMoreThan65535";
		result.notes = "Regression for https://github.com/antlr/antlr4/issues/1863";
		result.targetName = targetName;
		result.testType = "Lexer";

		// I tried playing around with different sizes and I think 1002 works for Go but 1003 does not;
		// the executing lexer gets a token syntax error for T208 or something like that
		final int tokensCount = 1024;
		final String suffix = String.join("", Collections.nCopies(70, "_"));

		String grammarName = "L";
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

		result.grammar = grammar.toString();
		result.grammarName = grammarName;
		result.input = input.toString();
		result.output = output.toString();

		// We seem to get memory errors and so I am turning this off during CI
		List<String> all = Arrays.asList(
//				"CSharp", "Python2", "Python3", "Cpp", "Go", "PHP", "Swift", "Java", "JavaScript", "Node", "Dart"
				"CSharp", "Python2", "Python3", "Go", "PHP", "Swift", "JavaScript", "Node", "Dart"
		);
		result.skipTargets.addAll(all);

//		result.skipTargets.add("Node"); // doesn't terminate
//		result.skipTargets.add("PHP"); // "Allowed memory size of 134217728 bytes exhausted (tried to allocate 16384 bytes)..."
//		result.skipTargets.add("Go"); // syntax error
		return result;
	}
}
