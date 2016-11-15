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
		public String output = "";
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
}
