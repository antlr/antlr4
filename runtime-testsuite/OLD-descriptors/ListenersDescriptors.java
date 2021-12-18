/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.descriptors;

import org.antlr.v4.test.runtime.BaseParserTestDescriptor;
import org.antlr.v4.test.runtime.CommentHasStringValue;

public class ListenersDescriptors {
	public static class Basic extends BaseParserTestDescriptor {
		public String input = "1 2";
		/**
		(a 1 2)
		1
		2
		 */
		@CommentHasStringValue
		public String output;

		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;

		 <ImportListener("T")>
		 <BasicListener("T")>

		 s
		 @after {
		 <ContextRuleFunction("$ctx", "r"):ToStringTree():writeln()>
		 <ContextRuleFunction("$ctx", "r"):WalkListener()>
		 }
		   : r=a ;
		 a : INT INT
		   | ID
		   ;
		 MULT: '*' ;
		 ADD : '+' ;
		 INT : [0-9]+ ;
		 ID  : [a-z]+ ;
		 WS : [ \t\n]+ -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class LR extends BaseParserTestDescriptor {
		public String input = "1+2*3";
		/**
		(e (e 1) + (e (e 2) * (e 3)))
		1
		2
		3
		2 3 2
		1 2 1
		 */
		@CommentHasStringValue
		public String output;

		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;

		 <ImportListener("T")>
		 <LRListener("T")>

		 s
		 @after {
		 <ContextRuleFunction("$ctx", "r"):ToStringTree():writeln()>
		 <ContextRuleFunction("$ctx", "r"):WalkListener()>
		 }
		 	: r=e ;
		 e : e op='*' e
		 	| e op='+' e
		 	| INT
		 	;
		 MULT: '*' ;
		 ADD : '+' ;
		 INT : [0-9]+ ;
		 ID  : [a-z]+ ;
		 WS : [ \t\n]+ -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class LRWithLabels extends BaseParserTestDescriptor {
		public String input = "1(2,3)";
		/**
		(e (e 1) ( (eList (e 2) , (e 3)) ))
		1
		2
		3
		1 [13 6]
		 */
		@CommentHasStringValue
		public String output;

		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;

		 <ImportListener("T")>
		 <LRWithLabelsListener("T")>

		 s
		 @after {
		 <ContextRuleFunction("$ctx", "r"):ToStringTree():writeln()>
		 <ContextRuleFunction("$ctx", "r"):WalkListener()>
		 }
		   : r=e ;
		 e : e '(' eList ')' # Call
		   | INT             # Int
		   ;
		 eList : e (',' e)* ;
		 MULT: '*' ;
		 ADD : '+' ;
		 INT : [0-9]+ ;
		 ID  : [a-z]+ ;
		 WS : [ \t\n]+ -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static abstract class RuleGetters extends BaseParserTestDescriptor {
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;

		 <ImportListener("T")>
		 <RuleGetterListener("T")>

		 s
		 @after {
		 <ContextRuleFunction("$ctx", "r"):ToStringTree():writeln()>
		 <ContextRuleFunction("$ctx", "r"):WalkListener()>
		 }
		   : r=a ;
		 a : b b		// forces list
		   | b		// a list still
		   ;
		 b : ID | INT;
		 MULT: '*' ;
		 ADD : '+' ;
		 INT : [0-9]+ ;
		 ID  : [a-z]+ ;
		 WS : [ \t\n]+ -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class RuleGetters_1 extends RuleGetters {
		public String input = "1 2";
		/**
		(a (b 1) (b 2))
		1 2 1
		 */
		@CommentHasStringValue
		public String output;
	}

	public static class RuleGetters_2 extends RuleGetters {
		public String input = "abc";
		/**
		(a (b abc))
		abc
		 */
		@CommentHasStringValue
		public String output;
	}

	public static abstract class TokenGetters extends BaseParserTestDescriptor {
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;

		 <ImportListener("T")>
		 <TokenGetterListener("T")>

		 s
		 @after {
		 <ContextRuleFunction("$ctx", "r"):ToStringTree():writeln()>
		 <ContextRuleFunction("$ctx", "r"):WalkListener()>
		 }
		   : r=a ;
		 a : INT INT
		   | ID
		   ;
		 MULT: '*' ;
		 ADD : '+' ;
		 INT : [0-9]+ ;
		 ID  : [a-z]+ ;
		 WS : [ \t\n]+ -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class TokenGetters_1 extends TokenGetters {
		public String input = "1 2";
		/**
		(a 1 2)
		1 2 [1, 2]
		 */
		@CommentHasStringValue
		public String output;
	}

	public static class TokenGetters_2 extends TokenGetters {
		public String input = "abc";
		/**
		(a abc)
		[@0,0:2='abc',<4>,1:0]
		 */
		@CommentHasStringValue
		public String output;
	}
}
