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
}
