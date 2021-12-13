/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.descriptors;

import org.antlr.v4.test.runtime.BaseLexerTestDescriptor;

public class LexerErrorsDescriptors {
	public static class DFAToATNThatFailsBackToDFA extends BaseLexerTestDescriptor {
		public String input = "ababx";
		public String output = """
		[@0,0:1='ab',<1>,1:0]
		[@1,2:3='ab',<1>,1:2]
		[@2,5:4='<EOF>',<-1>,1:5]
""";

		public String errors = "line 1:4 token recognition error at: 'x'\n";
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 A : 'ab' ;
		 B : 'abc' ;
""";

	}

	public static class DFAToATNThatMatchesThenFailsInATN extends BaseLexerTestDescriptor {
		public String input = "ababcx";
		public String output = """
		[@0,0:1='ab',<1>,1:0]
		[@1,2:4='abc',<2>,1:2]
		[@2,6:5='<EOF>',<-1>,1:6]
""";

		public String errors = "line 1:5 token recognition error at: 'x'\n";
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 A : 'ab' ;
		 B : 'abc' ;
		 C : 'abcd' ;
""";

	}

	public static abstract class EnforcedGreedyNestedBraces extends BaseLexerTestDescriptor {
		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 ACTION : '{' (ACTION | ~[{}])* '}';
		 WS : [ \\r\\n\\t]+ -> skip;
""";

	}

	public static class EnforcedGreedyNestedBraces_1 extends EnforcedGreedyNestedBraces {
		public String input = "{ { } }";
		public String output = """
		[@0,0:6='{ { } }',<1>,1:0]
		[@1,7:6='<EOF>',<-1>,1:7]
""";
	}

	public static class EnforcedGreedyNestedBraces_2 extends EnforcedGreedyNestedBraces {
		public String input = "{ { }";
		public String output = "[@0,5:4='<EOF>',<-1>,1:5]\n";
		public String errors = "line 1:0 token recognition error at: '{ { }'\n";
	}

	public static class ErrorInMiddle extends BaseLexerTestDescriptor {
		public String input = "abx";
		public String output = "[@0,3:2='<EOF>',<-1>,1:3]\n";
		public String errors = "line 1:0 token recognition error at: 'abx'\n";
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 A : 'abc' ;
""";

	}

	public static class InvalidCharAtStart extends BaseLexerTestDescriptor {
		public String input = "x";
		public String output = "[@0,1:0='<EOF>',<-1>,1:1]\n";
		public String errors = "line 1:0 token recognition error at: 'x'\n";
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 A : 'a' 'b' ;
""";

	}

	public static class InvalidCharAtStartAfterDFACache extends BaseLexerTestDescriptor {
		public String input = "abx";
		public String output = """
		[@0,0:1='ab',<1>,1:0]
		[@1,3:2='<EOF>',<-1>,1:3]
""";

		public String errors = "line 1:2 token recognition error at: 'x'\n";
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 A : 'a' 'b' ;
""";

	}

	public static class InvalidCharInToken extends BaseLexerTestDescriptor {
		public String input = "ax";
		public String output = "[@0,2:1='<EOF>',<-1>,1:2]\n";
		public String errors = "line 1:0 token recognition error at: 'ax'\n";
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 A : 'a' 'b' ;
""";

	}

	public static class InvalidCharInTokenAfterDFACache extends BaseLexerTestDescriptor {
		public String input = "abax";
		public String output = """
		[@0,0:1='ab',<1>,1:0]
		[@1,4:3='<EOF>',<-1>,1:4]
""";

		public String errors = "line 1:2 token recognition error at: 'ax'\n";
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 A : 'a' 'b' ;
""";

	}

    // 	 This is a regression test for #45 "NullPointerException in LexerATNSimulator.execDFA".
    // 	 https://github.com/antlr/antlr4/issues/46
	public static class LexerExecDFA extends BaseLexerTestDescriptor {
		public String input = "x : x";
		public String output = """
		[@0,0:0='x',<3>,1:0]
		[@1,2:2=':',<1>,1:2]
		[@2,4:4='x',<3>,1:4]
		[@3,5:4='<EOF>',<-1>,1:5]
""";

		public String errors = """
		line 1:1 token recognition error at: ' '
		line 1:3 token recognition error at: ' '
""";

		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 COLON : ':' ;
		 PTR : '->' ;
		 ID : [a-z]+;
""";
	}

	public static abstract class StringsEmbeddedInActions extends BaseLexerTestDescriptor {
		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		// ST interprets \\ as \ so we need \\\\ to get \\
		public String grammar = """
				 lexer grammar L;
				 ACTION2 : '[' (STRING | ~'"')*? ']';
				 STRING : '"' ('\\\\\\\\' '"' | .)*? '"';
				 WS : [ \\t\\r\\n]+ -> skip;
		""";
	}

	public static class StringsEmbeddedInActions_1 extends StringsEmbeddedInActions {
		public String input = "[\"foo\"]";
		public String output = """
		[@0,0:6='["foo"]',<1>,1:0]
		[@1,7:6='<EOF>',<-1>,1:7]
""";
	}

	public static class StringsEmbeddedInActions_2 extends StringsEmbeddedInActions {
		public String input = "[\"foo]";
		public String output = "[@0,6:5='<EOF>',<-1>,1:6]\n";
		public String errors = "line 1:0 token recognition error at: '[\"foo]'\n";
	}
}
