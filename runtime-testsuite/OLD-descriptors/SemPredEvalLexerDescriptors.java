/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.descriptors;

import org.antlr.v4.test.runtime.BaseLexerTestDescriptor;
import org.antlr.v4.test.runtime.CommentHasStringValue;

public class SemPredEvalLexerDescriptors {
	// Test for https://github.com/antlr/antlr4/issues/958
	public static class RuleSempredFunction extends BaseLexerTestDescriptor {
		public String input = "aaa";
		/**
		 [@0,0:0='a',<1>,1:0]
		 [@1,1:1='a',<1>,1:1]
		 [@2,2:2='a',<1>,1:2]
		 [@3,3:2='<EOF>',<-1>,1:3]
		 */
		@CommentHasStringValue
		public String output;

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		/**
		 lexer grammar L;
		 T : 'a' {<True()>}? ;
		 */
		@CommentHasStringValue
		public String grammar;
	}

	public static class DisableRule extends BaseLexerTestDescriptor {
		public String input = "enum abc";
		/**
		[@0,0:3='enum',<2>,1:0]
		[@1,5:7='abc',<3>,1:5]
		[@2,8:7='<EOF>',<-1>,1:8]
		s0-' '->:s5=>4
		s0-'a'->:s6=>3
		s0-'e'->:s1=>3
		:s1=>3-'n'->:s2=>3
		:s2=>3-'u'->:s3=>3
		:s6=>3-'b'->:s6=>3
		:s6=>3-'c'->:s6=>3
		 */
		@CommentHasStringValue
		public String output;

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		/**
		 lexer grammar L;
		 E1 : 'enum' { <False()> }? ;
		 E2 : 'enum' { <True()> }? ;  // winner not E1 or ID
		 ID : 'a'..'z'+ ;
		 WS : (' '|'\n') -> skip;
		 */
		@CommentHasStringValue
		public String grammar;

		@Override
		public boolean showDFA() { return true; }
	}

	public static class EnumNotID extends BaseLexerTestDescriptor {
		public String input = "enum abc enum";
		/**
		[@0,0:3='enum',<1>,1:0]
		[@1,5:7='abc',<2>,1:5]
		[@2,9:12='enum',<1>,1:9]
		[@3,13:12='<EOF>',<-1>,1:13]
		s0-' '->:s3=>3
		 */
		@CommentHasStringValue // 		<! no edges in DFA for enum/id. all paths lead to pred. !>

		public String output;

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		/**
		 lexer grammar L;
		 ENUM : [a-z]+  { <TextEquals("enum")> }? ;
		 ID : [a-z]+  ;
		 WS : (' '|'\n') -> skip;
		 */
		@CommentHasStringValue
		public String grammar;

		@Override
		public boolean showDFA() { return true; }
	}

	public static class IDnotEnum extends BaseLexerTestDescriptor {
		public String input = "enum abc enum";
		/**
		[@0,0:3='enum',<2>,1:0]
		[@1,5:7='abc',<2>,1:5]
		[@2,9:12='enum',<2>,1:9]
		[@3,13:12='<EOF>',<-1>,1:13]
		s0-' '->:s2=>3
		 */
		@CommentHasStringValue // 		<! no edges in DFA for enum/id. all paths lead to pred. !>

		public String output;

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		/**
		 lexer grammar L;
		 ENUM : [a-z]+  { <False()> }? ;
		 ID : [a-z]+  ;
		 WS : (' '|'\n') -> skip;
		 */
		@CommentHasStringValue
		public String grammar;

		@Override
		public boolean showDFA() { return true; }
	}

	public static class IDvsEnum extends BaseLexerTestDescriptor {
		public String input = "enum abc enum";

		/**
		[@0,0:3='enum',<2>,1:0]
		[@1,5:7='abc',<2>,1:5]
		[@2,9:12='enum',<2>,1:9]
		[@3,13:12='<EOF>',<-1>,1:13]
		s0-' '->:s5=>3
		s0-'a'->:s4=>2
		s0-'e'->:s1=>2
		:s1=>2-'n'->:s2=>2
		:s2=>2-'u'->:s3=>2
		:s4=>2-'b'->:s4=>2
		:s4=>2-'c'->:s4=>2
		 */
		@CommentHasStringValue // 	no 'm'-> transition...conflicts with pred
		public String output;

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		/**
		 lexer grammar L;
		 ENUM : 'enum' { <False()> }? ;
		 ID : 'a'..'z'+ ;
		 WS : (' '|'\n') -> skip;
		 */
		@CommentHasStringValue
		public String grammar;

		@Override
		public boolean showDFA() { return true; }
	}

	public static class Indent extends BaseLexerTestDescriptor {
		public String input = "abc\n  def  \n";
		/**
		INDENT
		[@0,0:2='abc',<1>,1:0]
		[@1,3:3='\n',<3>,1:3]
		[@2,4:5='  ',<2>,2:0]
		[@3,6:8='def',<1>,2:2]
		[@4,9:10='  ',<4>,2:5]
		[@5,11:11='\n',<3>,2:7]
		[@6,12:11='<EOF>',<-1>,3:0]
		s0-'
		'->:s2=>3
		s0-'a'->:s1=>1
		s0-'d'->:s1=>1
		:s1=>1-'b'->:s1=>1
		:s1=>1-'c'->:s1=>1
		:s1=>1-'e'->:s1=>1
		:s1=>1-'f'->:s1=>1
		 */
		@CommentHasStringValue
		public String output;

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		/**
		 lexer grammar L;
		 ID : [a-z]+  ;
		 INDENT : [ \t]+ { <TokenStartColumnEquals("0")> }?
		          { <writeln("\"INDENT\"")> }  ;
		 NL : '\n';
		 WS : [ \t]+ ;
		 */
		@CommentHasStringValue
		public String grammar;

		@Override
		public boolean showDFA() { return true; }
	}

	public static class LexerInputPositionSensitivePredicates extends BaseLexerTestDescriptor {
		public String input = "a cde\nabcde\n";
		/**
		a
		cde
		ab
		cde
		[@0,0:0='a',<1>,1:0]
		[@1,2:4='cde',<2>,1:2]
		[@2,6:7='ab',<1>,2:0]
		[@3,8:10='cde',<2>,2:2]
		[@4,12:11='<EOF>',<-1>,3:0]
		 */
		@CommentHasStringValue
		public String output;

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		/**
		 lexer grammar L;
		 WORD1 : ID1+ { <Text():writeln()> } ;
		 WORD2 : ID2+ { <Text():writeln()> } ;
		 fragment ID1 : { <Column()> \< 2 }? [a-zA-Z];
		 fragment ID2 : { <Column()> >= 2 }? [a-zA-Z];
		 WS : (' '|'\n') -> skip;
		 */
		@CommentHasStringValue
		public String grammar;

		@Override
		public boolean showDFA() { return true; }
	}

	public static class PredicatedKeywords extends BaseLexerTestDescriptor {
		public String input = "enum enu a";
		/**
		enum!
		ID enu
		ID a
		[@0,0:3='enum',<1>,1:0]
		[@1,5:7='enu',<2>,1:5]
		[@2,9:9='a',<2>,1:9]
		[@3,10:9='<EOF>',<-1>,1:10]
		 */
		@CommentHasStringValue
		public String output;

		public String errors = null;
		public String startRule = "";
		public String grammarName = "L";

		/**
		 lexer grammar L;
		 ENUM : [a-z]+ { <TextEquals("enum")> }? { <writeln("\"enum!\"")> } ;
		 ID   : [a-z]+ { <PlusText("ID "):writeln()> } ;
		 WS   : [ \n] -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;
	}
}
