/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.descriptors;

import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.test.runtime.BaseLexerTestDescriptor;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LexerExecDescriptors {
	public static class ActionPlacement extends BaseLexerTestDescriptor {
		public String input = "ab";
		public String output = """
		stuff0:
		stuff1: a
		stuff2: ab
		ab
		[@0,0:1='ab',<1>,1:0]
		[@1,2:1='<EOF>',<-1>,1:2]
""";

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 I : ({<PlusText("stuff fail: "):writeln()>} 'a'
		 | {<PlusText("stuff0:"):writeln()>}
		 		'a' {<PlusText("stuff1: "):writeln()>}
		 		'b' {<PlusText("stuff2: "):writeln()>})
		 		{<Text():writeln()>} ;
		 WS : (' '|'\\n') -> skip ;
		 J : .;
""";

	}

	public static class CharSet extends BaseLexerTestDescriptor {
		public String input = "34\n 34";
		public String output = """
		I
		I
		[@0,0:1='34',<1>,1:0]
		[@1,4:5='34',<1>,2:1]
		[@2,6:5='<EOF>',<-1>,2:3]
""";

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 I : '0'..'9'+ {<writeln("\\"I\\"")>} ;
		 WS : [ \\n\\u000D] -> skip ;
""";

	}

	/* regression test for antlr/antlr4#1925 */
	public static class UnicodeCharSet extends BaseLexerTestDescriptor {
		public String input = "均";
		public String output = """
		 [@0,0:0='均',<1>,1:0]
		 [@1,1:0='<EOF>',<-1>,1:1]
""";

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 ID : ([A-Z_]|'\u0100'..'\uFFFE') ([A-Z_0-9]|'\u0100'..'\uFFFE')*;
""";

	}

	public static class CharSetInSet extends BaseLexerTestDescriptor {
		public String input = "a x";
		public String output = """
		I
		I
		[@0,0:0='a',<1>,1:0]
		[@1,2:2='x',<1>,1:2]
		[@2,3:2='<EOF>',<-1>,1:3]
""";

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 I : (~[ab \\\n]|'a')  {<writeln("\\"I\\"")>} ;
		 WS : [ \\n\\u000D]+ -> skip ;
""";

	}

	public static class CharSetNot extends BaseLexerTestDescriptor {
		public String input = "xaf";
		public String output = """
		I
		[@0,0:2='xaf',<1>,1:0]
		[@1,3:2='<EOF>',<-1>,1:3]
""";

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 I : ~[ab \\n] ~[ \\ncd]* {<writeln("\\"I\\"")>} ;
		 WS : [ \\n\\u000D]+ -> skip ;
""";

	}

	public static class CharSetPlus extends BaseLexerTestDescriptor {
		public String input = "34\n 34";
		public String output = """
		I
		I
		[@0,0:1='34',<1>,1:0]
		[@1,4:5='34',<1>,2:1]
		[@2,6:5='<EOF>',<-1>,2:3]
""";

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 I : '0'..'9'+ {<writeln("\\"I\\"")>} ;
		 WS : [ \\n\\u000D]+ -> skip ;
""";

	}

	public static class CharSetRange extends BaseLexerTestDescriptor {
		public String input = "34\n 34 a2 abc \n   ";
		public String output = """
		I
		I
		ID
		ID
		[@0,0:1='34',<1>,1:0]
		[@1,4:5='34',<1>,2:1]
		[@2,7:8='a2',<2>,2:4]
		[@3,10:12='abc',<2>,2:7]
		[@4,18:17='<EOF>',<-1>,3:3]
""";

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 I : [0-9]+ {<writeln("\\"I\\"")>} ;
		 ID : [a-zA-Z] [a-zA-Z0-9]* {<writeln("\\"ID\\"")>} ;
		 WS : [ \\n\\u0009\r]+ -> skip ;
""";

	}

	public static class CharSetWithEscapedChar extends BaseLexerTestDescriptor {
		public String input = "- ] ";
		public String output = """
		DASHBRACK
		DASHBRACK
		[@0,0:0='-',<1>,1:0]
		[@1,2:2=']',<1>,1:2]
		[@2,4:3='<EOF>',<-1>,1:4]
""";

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 DASHBRACK : [\\-\\]]+ {<writeln("\\"DASHBRACK\\"")>} ;
		 WS : [ \\n]+ -> skip ;
""";

	}

	public static class CharSetWithMissingEscapeChar extends BaseLexerTestDescriptor {
		public String input = "34 ";
		public String output = """
		I
		[@0,0:1='34',<1>,1:0]
		[@1,3:2='<EOF>',<-1>,1:3]
""";

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 I : [0-9]+ {<writeln("\\"I\\"")>} ;
		 WS : [ \\n]+ -> skip ;
""";

	}

	public static class CharSetWithQuote1 extends BaseLexerTestDescriptor {
		public String input = "b\"a";
		public String output = """
		A
		[@0,0:2='b"a',<1>,1:0]
		[@1,3:2='<EOF>',<-1>,1:3]
""";

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 A : ["a-z]+ {<writeln("\\"A\\"")>} ;
		 WS : [ \\n\t]+ -> skip ;
""";

	}

	public static class CharSetWithQuote2 extends BaseLexerTestDescriptor {
		public String input = "b\"\\a";
		public String output = """
		A
		[@0,0:3='b"\\a',<1>,1:0]
		[@1,4:3='<EOF>',<-1>,1:4]
""";

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 A : ["\\\\ab]+ {<writeln("\\"A\\"")>} ;
		 WS : [ \\n\t]+ -> skip ;
""";

	}

	public static class EOFByItself extends BaseLexerTestDescriptor {
		public String input = "";
		public String output = """
		[@0,0:-1='<EOF>',<1>,1:0]
		[@1,0:-1='<EOF>',<-1>,1:0]
""";

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 DONE : EOF ;
		 A : 'a';
""";

	}

	public static abstract class EOFSuffixInFirstRule extends BaseLexerTestDescriptor {
		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 A : 'a' EOF ;
		 B : 'a';
		 C : 'c';
""";

	}

	public static class EOFSuffixInFirstRule_1 extends EOFSuffixInFirstRule {
		public String input = "";
		public String output = "[@0,0:-1='<EOF>',<-1>,1:0]\n";
	}

	public static class EOFSuffixInFirstRule_2 extends EOFSuffixInFirstRule {
		public String input = "a";
		public String output = """
		[@0,0:0='a',<1>,1:0]
		[@1,1:0='<EOF>',<-1>,1:1]
""";
	}

	public static class GreedyClosure extends BaseLexerTestDescriptor {
		public String input = """
		//blah
		//blah
""";

		public String output = """
		[@0,0:13='//blah\\n//blah\\n',<1>,1:0]
		[@1,14:13='<EOF>',<-1>,3:0]
""";

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 CMT : '//' .*? '\\n' CMT*;
		 WS : (' '|'\t')+;
""";

	}

	public static class GreedyConfigs extends BaseLexerTestDescriptor {
		public String input = "ab";
		public String output = """
		ab
		[@0,0:1='ab',<1>,1:0]
		[@1,2:1='<EOF>',<-1>,1:2]
""";

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 I : ('a' | 'ab') {<Text():writeln()>} ;
		 WS : (' '|'\\n') -> skip ;
		 J : .;
""";

	}

	public static class GreedyOptional extends BaseLexerTestDescriptor {
		public String input = """
		//blah
		//blah
""";

		public String output = """
		[@0,0:13='//blah\\n//blah\\n',<1>,1:0]
		[@1,14:13='<EOF>',<-1>,3:0]
""";

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 CMT : '//' .*? '\\n' CMT?;
		 WS : (' '|'\t')+;
""";

	}

	public static class GreedyPositiveClosure extends BaseLexerTestDescriptor {
		public String input = """
		//blah
		//blah
""";

		public String output = """
		[@0,0:13='//blah\\n//blah\\n',<1>,1:0]
		[@1,14:13='<EOF>',<-1>,3:0]
""";

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 CMT : ('//' .*? '\\n')+;
		 WS : (' '|'\t')+;
""";

	}

	public static class HexVsID extends BaseLexerTestDescriptor {
		public String input = "x 0 1 a.b a.l";
		public String output = """
		[@0,0:0='x',<5>,1:0]
		[@1,1:1=' ',<6>,1:1]
		[@2,2:2='0',<2>,1:2]
		[@3,3:3=' ',<6>,1:3]
		[@4,4:4='1',<2>,1:4]
		[@5,5:5=' ',<6>,1:5]
		[@6,6:6='a',<5>,1:6]
		[@7,7:7='.',<4>,1:7]
		[@8,8:8='b',<5>,1:8]
		[@9,9:9=' ',<6>,1:9]
		[@10,10:10='a',<5>,1:10]
		[@11,11:11='.',<4>,1:11]
		[@12,12:12='l',<5>,1:12]
		[@13,13:12='<EOF>',<-1>,1:13]
""";

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 HexLiteral : '0' ('x'|'X') HexDigit+ ;
		 DecimalLiteral : ('0' | '1'..'9' '0'..'9'*) ;
		 FloatingPointLiteral : ('0x' | '0X') HexDigit* ('.' HexDigit*)? ;
		 DOT : '.' ;
		 ID : 'a'..'z'+ ;
		 fragment HexDigit : ('0'..'9'|'a'..'f'|'A'..'F') ;
		 WS : (' '|'\\n')+;
""";

	}

	public static class KeywordID extends BaseLexerTestDescriptor {
		public String input = "end eend ending a";
		public String output = """
		[@0,0:2='end',<1>,1:0]
		[@1,3:3=' ',<3>,1:3]
		[@2,4:7='eend',<2>,1:4]
		[@3,8:8=' ',<3>,1:8]
		[@4,9:14='ending',<2>,1:9]
		[@5,15:15=' ',<3>,1:15]
		[@6,16:16='a',<2>,1:16]
		[@7,17:16='<EOF>',<-1>,1:17]
""";

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 KEND : 'end' ; // has priority
		 ID : 'a'..'z'+ ;
		 WS : (' '|'\\n')+;
""";

	}

	public static class NonGreedyClosure extends BaseLexerTestDescriptor {
		public String input = """
		//blah
		//blah
""";

		public String output = """
		[@0,0:6='//blah\\n',<1>,1:0]
		[@1,7:13='//blah\\n',<1>,2:0]
		[@2,14:13='<EOF>',<-1>,3:0]
""";

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 CMT : '//' .*? '\\n' CMT*?;
		 WS : (' '|'\t')+;
""";

	}

	public static class NonGreedyConfigs extends BaseLexerTestDescriptor {
		public String input = "ab";
		public String output = """
		a
		b
		[@0,0:0='a',<1>,1:0]
		[@1,1:1='b',<3>,1:1]
		[@2,2:1='<EOF>',<-1>,1:2]
""";

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 I : .*? ('a' | 'ab') {<Text():writeln()>} ;
		 WS : (' '|'\\n') -> skip ;
		 J : . {<Text():writeln()>};
""";

	}

	public static class NonGreedyOptional extends BaseLexerTestDescriptor {
		public String input = """
		//blah
		//blah
""";

		public String output = """
		[@0,0:6='//blah\\n',<1>,1:0]
		[@1,7:13='//blah\\n',<1>,2:0]
		[@2,14:13='<EOF>',<-1>,3:0]
""";

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 CMT : '//' .*? '\\n' CMT??;
		 WS : (' '|'\t')+;
""";

	}

	public static class NonGreedyPositiveClosure extends BaseLexerTestDescriptor {
		public String input = """
		//blah
		//blah
""";

		public String output = """
		[@0,0:6='//blah\\n',<1>,1:0]
		[@1,7:13='//blah\\n',<1>,2:0]
		[@2,14:13='<EOF>',<-1>,3:0]
""";

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 CMT : ('//' .*? '\\n')+?;
		 WS : (' '|'\t')+;
""";

	}

	public static class NonGreedyTermination1 extends BaseLexerTestDescriptor {
		public String input = "\"hi\"\"mom\"";
		public String output = """
		[@0,0:3='"hi"',<1>,1:0]
		[@1,4:8='"mom"',<1>,1:4]
		[@2,9:8='<EOF>',<-1>,1:9]
""";

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 STRING : '"' ('""' | .)*? '"';
""";

	}

	public static class NonGreedyTermination2 extends BaseLexerTestDescriptor {
		public String input = "\"\"\"mom\"";
		public String output = """
		[@0,0:6='\"""mom"',<1>,1:0]
		[@1,7:6='<EOF>',<-1>,1:7]
""";

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 STRING : '"' ('""' | .)+? '"';
""";

	}

	/*
	 * This is a regression test for antlr/antlr4#224: "Parentheses without
	 * quantifier in lexer rules have unclear effect".
	 * https://github.com/antlr/antlr4/issues/224
	 */
	public static class Parentheses extends BaseLexerTestDescriptor {
		public String input = "-.-.-!";
		public String output = """
		[@0,0:4='-.-.-',<1>,1:0]
		[@1,5:5='!',<3>,1:5]
		[@2,6:5='<EOF>',<-1>,1:6]
""";

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 START_BLOCK: '-.-.-';
		 ID : (LETTER SEPARATOR) (LETTER SEPARATOR)+;
		 fragment LETTER: L_A|L_K;
		 fragment L_A: '.-';
		 fragment L_K: '-.-';
		 SEPARATOR: '!';
""";

	}

	public static class PositionAdjustingLexer extends BaseLexerTestDescriptor {
		public String input = """
		tokens
		tokens {
		notLabel
		label1 =
		label2 +=
		notLabel
""";

		public String output = """
		 [@0,0:5='tokens',<6>,1:0]
		 [@1,7:12='tokens',<4>,2:0]
		 [@2,14:14='{',<3>,2:7]
		 [@3,16:23='notLabel',<6>,3:0]
		 [@4,25:30='label1',<5>,4:0]
		 [@5,32:32='=',<1>,4:7]
		 [@6,34:39='label2',<5>,5:0]
		 [@7,41:42='+=',<2>,5:7]
		 [@8,44:51='notLabel',<6>,6:0]
		 [@9,53:52='<EOF>',<-1>,7:0]
""";

		public String errors = null;
		public String startRule = "";
		public String grammarName = "PositionAdjustingLexer";

		public String grammar = """
		 lexer grammar PositionAdjustingLexer;

		 @definitions {
		 <PositionAdjustingLexerDef()>
		 }

		 @members {
		 <PositionAdjustingLexer()>
		 }

		 ASSIGN : '=' ;
		 PLUS_ASSIGN : '+=' ;
		 LCURLY:	'{';

		 // 'tokens' followed by '{'
		 TOKENS : 'tokens' IGNORED '{';

		 // IDENTIFIER followed by '+=' or '='
		 LABEL
		 	:	IDENTIFIER IGNORED '+'? '='
		 	;

		 IDENTIFIER
		 	:	[a-zA-Z_] [a-zA-Z0-9_]*
		 	;

		 fragment
		 IGNORED
		 	:	[ \t\r\\n]*
		 	;

		 NEWLINE
		 	:	[\r\\n]+ -> skip
		 	;

		 WS
		 	:	[ \t]+ -> skip
		 	;
""";

	}

	public static class QuoteTranslation extends BaseLexerTestDescriptor {
		public String input = "\"";
		public String output = """
		[@0,0:0='"',<1>,1:0]
		[@1,1:0='<EOF>',<-1>,1:1]
""";

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 QUOTE : '"' ; // make sure this compiles
""";

	}

	public static abstract class RecursiveLexerRuleRefWithWildcardPlus extends BaseLexerTestDescriptor {
		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 CMT : '/*' (CMT | .)+? '*' '/' ;
		 WS : (' '|'\\n')+;
""";

	}

	public static class RecursiveLexerRuleRefWithWildcardPlus_1 extends RecursiveLexerRuleRefWithWildcardPlus {
		public String input =
			"/* ick */\n"+
			"/* /* */\n"+
			"/* /*nested*/ */\n"; // stuff on end of comment matches another rule

		public String output =
			"[@0,0:8='/* ick */',<1>,1:0]\n"+
			"[@1,9:9='\\n',<2>,1:9]\n"+
			"[@2,10:34='/* /* */\\n/* /*nested*/ */',<1>,2:0]\n"+
			"[@3,35:35='\\n',<2>,3:16]\n"+
			"[@4,36:35='<EOF>',<-1>,4:0]\n";
	}

	public static class RecursiveLexerRuleRefWithWildcardPlus_2 extends RecursiveLexerRuleRefWithWildcardPlus {
		public String input =
			"/* ick */x\n"+
			"/* /* */x\n"+
			"/* /*nested*/ */x\n";

		public String output =
			"[@0,0:8='/* ick */',<1>,1:0]\n"+
			"[@1,10:10='\\n',<2>,1:10]\n"+
			"[@2,11:36='/* /* */x\\n/* /*nested*/ */',<1>,2:0]\n"+
			"[@3,38:38='\\n',<2>,3:17]\n"+
			"[@4,39:38='<EOF>',<-1>,4:0]\n";

		public String errors = """
		line 1:9 token recognition error at: 'x'
		line 3:16 token recognition error at: 'x'
""";
	}

	public static abstract class RecursiveLexerRuleRefWithWildcardStar extends BaseLexerTestDescriptor {
		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 CMT : '/*' (CMT | .)*? '*' '/' ;
		 WS : (' '|'\\n')+;
""";

	}

	public static class RecursiveLexerRuleRefWithWildcardStar_1 extends RecursiveLexerRuleRefWithWildcardStar {
		public String input =
			"/* ick */\n"+
			"/* /* */\n"+
			"/* /*nested*/ */\n";

		public String output =
			"[@0,0:8='/* ick */',<1>,1:0]\n"+
			"[@1,9:9='\\n',<2>,1:9]\n"+
			"[@2,10:34='/* /* */\\n/* /*nested*/ */',<1>,2:0]\n"+
			"[@3,35:35='\\n',<2>,3:16]\n"+
			"[@4,36:35='<EOF>',<-1>,4:0]\n";
	}

	public static class RecursiveLexerRuleRefWithWildcardStar_2 extends RecursiveLexerRuleRefWithWildcardStar {
		public String input =
			"/* ick */x\n"+
			"/* /* */x\n"+
			"/* /*nested*/ */x\n";

		public String output =
			"[@0,0:8='/* ick */',<1>,1:0]\n"+
			"[@1,10:10='\\n',<2>,1:10]\n"+
			"[@2,11:36='/* /* */x\\n/* /*nested*/ */',<1>,2:0]\n"+
			"[@3,38:38='\\n',<2>,3:17]\n"+
			"[@4,39:38='<EOF>',<-1>,4:0]\n";

		public String errors = """
		line 1:9 token recognition error at: 'x'
		line 3:16 token recognition error at: 'x'
""";
	}

	public static class RefToRuleDoesNotSetTokenNorEmitAnother extends BaseLexerTestDescriptor {
		public String input = "34 -21 3";

		// EOF has no length so range is 8:7 not 8:8
		public String output = """
		[@0,0:1='34',<2>,1:0]
		[@1,3:5='-21',<1>,1:3]
		[@2,7:7='3',<2>,1:7]
		[@3,8:7='<EOF>',<-1>,1:8]
""";

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 A : '-' I ;
		 I : '0'..'9'+ ;
		 WS : (' '|'\\n') -> skip ;
""";

	}

	public static class Slashes extends BaseLexerTestDescriptor {
		public String input = "\\ / \\/ /\\";
		public String output = """
		[@0,0:0='\',<1>,1:0]
		[@1,2:2='/',<2>,1:2]
		[@2,4:5='\\/',<3>,1:4]
		[@3,7:8='/\',<4>,1:7]
		[@4,9:8='<EOF>',<-1>,1:9]
""";

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 Backslash : '\\\\';
		 Slash : '/';
		 Vee : '\\\\/';
		 Wedge : '/\\\\';
		 WS : [ \t] -> skip;
""";

	}

    //
    // 	 This is a regression test for antlr/antlr4#687 "Empty zero-length tokens
    // 	 cannot have lexer commands" and antlr/antlr4#688 "Lexer cannot match
    // 	 zero-length tokens"
    // 	 https://github.com/antlr/antlr4/issues/687
    // 	 https://github.com/antlr/antlr4/issues/688
    //
	public static class ZeroLengthToken extends BaseLexerTestDescriptor {
		public String input = "'xxx'";
		public String output = """
		[@0,0:4=''xxx'',<1>,1:0]
		[@1,5:4='<EOF>',<-1>,1:5]
""";

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 BeginString
		 	:	'\'' -> more, pushMode(StringMode)
		 	;
		 mode StringMode;
		 	StringMode_X : 'x' -> more;
		 	StringMode_Done : -> more, mode(EndStringMode);
		 mode EndStringMode;
		 	EndString : '\'' -> popMode;
""";
	}

    //
    // 	 This is a regression test for antlr/antlr4#76 "Serialized ATN strings
    // 	 should be split when longer than 2^16 bytes (class file limitation)"
    // 	 https://github.com/antlr/antlr4/issues/76
    //
	public static class LargeLexer extends BaseLexerTestDescriptor {
		public String input = "KW400";
		public String output = """
		[@0,0:4='KW400',<402>,1:0]
		[@1,5:4='<EOF>',<-1>,1:5]
""";

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		// Look for grammar as resource
		@Override
		public Pair<String, String> getGrammar() {
			String grammar = null;

			final ClassLoader loader = Thread.currentThread().getContextClassLoader();
			final URL stuff = loader.getResource("org/antlr/v4/test/runtime/LargeLexer.g4");
			try {
				grammar = new String(Files.readAllBytes(Paths.get(stuff.toURI())));
			}
			catch (Exception e) {
				System.err.println("Cannot find grammar org/antlr/v4/test/runtime/LargeLexer.g4");
			}

			return new Pair<>(grammarName, grammar);
		}
	}

    //
    // 	 This is a regression test for antlr/antlr4#2709 "PHP target generates
    // 	 invalid output when $ is used as part of the literal in lexer rule"
    // 	 https://github.com/antlr/antlr4/issues/2709
    // 	/
	public static class EscapeTargetStringLiteral extends BaseLexerTestDescriptor {
		public String output = """
		[@0,0:-1='<EOF>',<-1>,1:0]
""";

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 ACTION_WITH_DOLLAR: '$ACTION';
""";

		@Override
		public boolean ignore(String targetName) {
			return !targetName.equals("PHP");
		}
	}

	// https://github.com/antlr/antlr4/issues/1943
	public static class StackoverflowDueToNotEscapedHyphen extends BaseLexerTestDescriptor {
		public String input = "word";

		public String output = """
		 [@0,0:3='word',<1>,1:0]
		 [@1,4:3='<EOF>',<-1>,1:4]
""";

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		public String grammar = """
		 lexer grammar L;
		 WORD   : [a-z-+]+;
""";
	}
}
