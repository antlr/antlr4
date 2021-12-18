/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.descriptors;

import org.antlr.v4.test.runtime.BaseParserTestDescriptor;
import org.antlr.v4.test.runtime.CommentHasStringValue;

public class SetsDescriptors {
	public static class CharSetLiteral extends BaseParserTestDescriptor {
		public String input = "A a B b";
		/**
		A
		a
		B
		b
		 */
		@CommentHasStringValue
		public String output;

		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : (A {<writeln("$A.text")>})+ ;
		 A : [AaBb] ;
		 WS : (' '|'\n')+ -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class ComplementSet extends BaseParserTestDescriptor {
		public String input = "a";
		public String output = null;
		/**
		line 1:0 token recognition error at: 'a'
		line 1:1 missing {} at '<EOF>'
		 */
		@CommentHasStringValue
		public String errors;

		public String startRule = "parse";
		public String grammarName = "T";

		/**
		 grammar T;
		 parse : ~NEW_LINE;
		 NEW_LINE: '\\r'? '\\n';
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class LexerOptionalSet extends BaseParserTestDescriptor {
		public String input = "ac";
		public String output = "ac\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : A {<InputText():writeln()>} ;
		 A : ('a'|'b')? 'c' ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class LexerPlusSet extends BaseParserTestDescriptor {
		public String input = "abaac";
		public String output = "abaac\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : A {<InputText():writeln()>} ;
		 A : ('a'|'b')+ 'c' ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class LexerStarSet extends BaseParserTestDescriptor {
		public String input = "abaac";
		public String output = "abaac\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : A {<InputText():writeln()>} ;
		 A : ('a'|'b')* 'c' ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class NotChar extends BaseParserTestDescriptor {
		public String input = "x";
		public String output = "x\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : A {<writeln("$A.text")>} ;
		 A : ~'b' ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class NotCharSet extends BaseParserTestDescriptor {
		public String input = "x";
		public String output = "x\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : A {<writeln("$A.text")>} ;
		 A : ~('b'|'c') ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class NotCharSetWithLabel extends BaseParserTestDescriptor {
		public String input = "x";
		public String output = "x\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : A {<writeln("$A.text")>} ;
		 A : h=~('b'|'c') ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class NotCharSetWithRuleRef3 extends BaseParserTestDescriptor {
		public String input = "x";
		public String output = "x\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : A {<writeln("$A.text")>} ;
		 A : ('a'|B) ;  // this doesn't collapse to set but works
		 fragment
		 B : ~('a'|'c') ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class OptionalLexerSingleElement extends BaseParserTestDescriptor {
		public String input = "bc";
		public String output = "bc\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : A {<InputText():writeln()>} ;
		 A : 'b'? 'c' ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class OptionalSet extends BaseParserTestDescriptor {
		public String input = "ac";
		public String output = "ac\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : ('a'|'b')? 'c' {<InputText():writeln()>} ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class OptionalSingleElement extends BaseParserTestDescriptor {
		public String input = "bc";
		public String output = "bc\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : A? 'c' {<InputText():writeln()>} ;
		 A : 'b' ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class ParserNotSet extends BaseParserTestDescriptor {
		public String input = "zz";
		public String output = "z\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : t=~('x'|'y') 'z' {<writeln("$t.text")>} ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class ParserNotToken extends BaseParserTestDescriptor {
		public String input = "zz";
		public String output = "zz\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : ~'x' 'z' {<InputText():writeln()>} ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class ParserNotTokenWithLabel extends BaseParserTestDescriptor {
		public String input = "zz";
		public String output = "z\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : t=~'x' 'z' {<writeln("$t.text")>} ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class ParserSet extends BaseParserTestDescriptor {
		public String input = "x";
		public String output = "x\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : t=('x'|'y') {<writeln("$t.text")>} ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class PlusLexerSingleElement extends BaseParserTestDescriptor {
		public String input = "bbbbc";
		public String output = "bbbbc\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : A {<InputText():writeln()>} ;
		 A : 'b'+ 'c' ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class PlusSet extends BaseParserTestDescriptor {
		public String input = "abaac";
		public String output = "abaac\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : ('a'|'b')+ 'c' {<InputText():writeln()>} ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class RuleAsSet extends BaseParserTestDescriptor {
		public String input = "b";
		public String output = "b\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a @after {<InputText():writeln()>} : 'a' | 'b' |'c' ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class SeqDoesNotBecomeSet extends BaseParserTestDescriptor {
		public String input = "34";
		public String output = "34\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : C {<InputText():writeln()>} ;
		 fragment A : '1' | '2';
		 fragment B : '3' '4';
		 C : A | B;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static abstract class StarLexerSingleElement extends BaseParserTestDescriptor {
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : A {<InputText():writeln()>} ;
		 A : 'b'* 'c' ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class StarLexerSingleElement_1 extends StarLexerSingleElement {
		public String input = "bbbbc";
		public String output = "bbbbc\n";
	}

	public static class StarLexerSingleElement_2 extends StarLexerSingleElement {
		public String input = "c";
		public String output = "c\n";
	}

	public static class StarSet extends BaseParserTestDescriptor {
		public String input = "abaac";
		public String output = "abaac\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : ('a'|'b')* 'c' {<InputText():writeln()>} ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class UnicodeUnescapedBMPSet extends BaseParserTestDescriptor {
		public String input = "a\u00E4\u3042\u4E9Cc";
		public String output = "a\u00E4\u3042\u4E9Cc\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : LETTERS {<InputText():writeln()>} ;
		 // These are actually not escaped -- Java passes the
		 // raw unescaped Unicode values to the grammar compiler.
		 LETTERS : ('a'|'\u00E4'|'\u4E9C'|'\u3042')* 'c';
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class UnicodeUnescapedBMPRangeSet extends BaseParserTestDescriptor {
		public String input = "a\u00E1\u00E4\u00E1\u00E2\u00E5d";
		public String output = "a\u00E1\u00E4\u00E1\u00E2\u00E5d\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : LETTERS* 'd' {<InputText():writeln()>} ;
		 // These are actually not escaped -- Java passes the
		 // raw unescaped Unicode values to the grammar compiler.
		 LETTERS : ('a'|'\u00E0'..'\u00E5');
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class UnicodeEscapedBMPSet extends BaseParserTestDescriptor {
		public String input = "a\u00E4\u3042\u4E9Cc";
		public String output = "a\u00E4\u3042\u4E9Cc\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : LETTERS {<InputText():writeln()>} ;
		 // Note the double-backslash to avoid Java passing
		 // unescaped values as part of the grammar.
		 LETTERS : ('a'|'\\u00E4'|'\\u4E9C'|'\\u3042')* 'c';
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class UnicodeEscapedBMPRangeSet extends BaseParserTestDescriptor {
		public String input = "a\u00E1\u00E4\u00E1\u00E2\u00E5d";
		public String output = "a\u00E1\u00E4\u00E1\u00E2\u00E5d\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : LETTERS* 'd' {<InputText():writeln()>} ;
		 // Note the double-backslash to avoid Java passing
		 // unescaped values as part of the grammar.
		 LETTERS : ('a'|'\\u00E0'..'\\u00E5');
		 */
		@CommentHasStringValue
		public String grammar;

	}

	// TODO(bhamiltoncx): This needs to be an error, the V3
	// runtime used by the tool doesn't really understand unescaped code points >
	// U+FFFF.
	// public static class UnicodeUnescapedSMPSet extends BaseParserTestDescriptor {
	//	public String input = new StringBuilder()
	//			.append("a")
	//			.appendCodePoint(0x1D5C2)
	//			.appendCodePoint(0x1D5CE)
	//			.appendCodePoint(0x1D5BA)
	//			.append("c")
	//			.toString();
	//	public String output = new StringBuilder()
	//			.append("a")
	//			.appendCodePoint(0x1D5C2)
	//			.appendCodePoint(0x1D5CE)
	//			.appendCodePoint(0x1D5BA)
	//			.append("c\n")
	//			.toString();
	//	public String errors = null;
	//	public String startRule = "a";
	//	public String grammarName = "T";

	//	/**
	//	 grammar T;
	//	 a : LETTERS  {<InputText():writeln()>} ;
	//	 // These are actually not escaped -- Java passes the
	//	 // raw unescaped Unicode values to the grammar compiler.
	//	 //
	//	 // Each sequence is the UTF-16 encoding of a raw Unicode
	//	 // SMP code point.
	//	 LETTERS : ('a'|'\uD835\uDDBA'|'\uD835\uDDBE'|'\uD835\uDDC2'|'\uD835\uDDC8'|'\uD835\uDDCE')* 'c';
	//	 */
	//	@CommentHasStringValue
	//	public String grammar;

	// }

	public static class UnicodeEscapedSMPSet extends BaseParserTestDescriptor {
		public String input = new StringBuilder()
				.append("a")
				.appendCodePoint(0x1D5C2)
				.appendCodePoint(0x1D5CE)
				.appendCodePoint(0x1D5BA)
				.append("c")
				.toString();
		public String output = new StringBuilder()
				.append("a")
				.appendCodePoint(0x1D5C2)
				.appendCodePoint(0x1D5CE)
				.appendCodePoint(0x1D5BA)
				.append("c\n")
				.toString();
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : LETTERS  {<InputText():writeln()>} ;
		 // Note the double-backslash to avoid Java passing
		 // unescaped values as part of the grammar.
		 LETTERS : ('a'|'\\u{1D5BA}'|'\\u{1D5BE}'|'\\u{1D5C2}'|'\\u{1D5C8}'|'\\u{1D5CE}')* 'c';
		 */
		@CommentHasStringValue
		public String grammar;

	}

	// Turns out Tool.java uses ANTLR 3's runtime, which means it can't use
	// CodePointCharStream to understand unescaped code points > U+FFFF.
	//
	// TODO(bhamiltoncx): This needs to be an error, since we don't currently plan
	// to port Tool.java to use ANTLR 4's runtime.

	// public static class UnicodeUnescapedSMPRangeSet extends BaseParserTestDescriptor {
	//	public String input = new StringBuilder()
	//			.append("a")
	//			.appendCodePoint(0x1D5C2)
	//			.appendCodePoint(0x1D5CE)
	//			.appendCodePoint(0x1D5BA)
	//			.append("d")
	//			.toString();
	//	public String output = new StringBuilder()
	//			.append("a")
	//			.appendCodePoint(0x1D5C2)
	//			.appendCodePoint(0x1D5CE)
	//			.appendCodePoint(0x1D5BA)
	//			.append("d\n")
	//			.toString();
	//	public String errors = null;
	//	public String startRule = "a";
	//	public String grammarName = "T";

	//	/**
	//	 grammar T;
	//	 a : LETTERS* 'd' {<InputText():writeln()>} ;
	//	 // These are actually not escaped -- Java passes the
	//	 // raw unescaped Unicode values to the grammar compiler.
	//	 LETTERS : ('a'|'\uD83D\uDE00'..'\uD83E\uDD43');
	//	 */
	//	@CommentHasStringValue
	//	public String grammar;

	// }

	public static class UnicodeEscapedSMPRangeSet extends BaseParserTestDescriptor {
		public String input = new StringBuilder()
				.append("a")
				.appendCodePoint(0x1F609)
				.appendCodePoint(0x1F942)
				.appendCodePoint(0x1F700)
				.append("d")
				.toString();
		public String output = new StringBuilder()
				.append("a")
				.appendCodePoint(0x1F609)
				.appendCodePoint(0x1F942)
				.appendCodePoint(0x1F700)
				.append("d\n")
				.toString();
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : LETTERS* 'd' {<InputText():writeln()>} ;
		 // Note the double-backslash to avoid Java passing
		 // unescaped values as part of the grammar.
		 LETTERS : ('a'|'\\u{1F600}'..'\\u{1F943}');
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class UnicodeEscapedSMPRangeSetMismatch extends BaseParserTestDescriptor {
		// Test the code points just before and just after the range.
		public String input = new StringBuilder()
				.append("a")
				.appendCodePoint(0x1F5FF)
				.appendCodePoint(0x1F944)
				.append("d")
				.toString();
		public String output = "ad\n";
		public String errors = new StringBuilder()
				.append("line 1:1 token recognition error at: '")
				.appendCodePoint(0x1F5FF)
				.append("'\n")
				.append("line 1:2 token recognition error at: '")
				.appendCodePoint(0x1F944)
				.append("'\n")
				.toString();
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : LETTERS* 'd' {<InputText():writeln()>} ;
		 // Note the double-backslash to avoid Java passing
		 // unescaped values as part of the grammar.
		 LETTERS : ('a'|'\\u{1F600}'..'\\u{1F943}');
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class UnicodeNegatedBMPSetIncludesSMPCodePoints extends BaseParserTestDescriptor {
		public String input = "a\uD83D\uDE33\uD83D\uDE21\uD83D\uDE1D\uD83E\uDD13c";
		public String output = "a\uD83D\uDE33\uD83D\uDE21\uD83D\uDE1D\uD83E\uDD13c\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : LETTERS {<InputText():writeln()>} ;
		 LETTERS : 'a' ~('b')+ 'c';
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class UnicodeNegatedSMPSetIncludesBMPCodePoints extends BaseParserTestDescriptor {
		public String input = "abc";
		public String output = "abc\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : LETTERS {<InputText():writeln()>} ;
		 LETTERS : 'a' ~('\\u{1F600}'..'\\u{1F943}')+ 'c';
		 */
		@CommentHasStringValue
		public String grammar;

	}
}
