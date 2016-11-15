package org.antlr.v4.test.runtime.descriptors;

import org.antlr.v4.test.runtime.BaseParserTestDescriptor;
import org.antlr.v4.test.runtime.CommentHasStringValue;

import java.util.Arrays;

import static org.antlr.v4.test.runtime.BaseRuntimeTest.JavaScriptTargets;

public class VisitorsDescriptors {
	public static boolean isJavaScriptTarget(String targetName) {
		boolean isJavaScriptTarget = Arrays.binarySearch(JavaScriptTargets, targetName)>=0;
		return isJavaScriptTarget;
	}

	public static class Basic extends BaseParserTestDescriptor {
		public String input = "1 2";
		/**
		(a 1 2)
		[ '1', '2' ]
		 */
		@CommentHasStringValue
		public String output;

		public String errors = "";
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 @parser::header {
		 <ImportVisitor(grammarName)>
		 }

		 @parser::members {
		 <BasicVisitor(grammarName)>
		 }

		 s
		 @after {
		 <ToStringTree("$ctx.r"):writeln()>
		 <WalkVisitor("$ctx.r")>
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

		@Override
		public boolean ignore(String targetName) { return !isJavaScriptTarget(targetName); }
	}

	public static class LR extends BaseParserTestDescriptor {
		public String input = "1+2*3";
		/**
		(e (e 1) + (e (e 2) * (e 3)))
		1,,2,,32 3 21 2 1
		 */
		@CommentHasStringValue
		public String output;

		public String errors = "";
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 @parser::header {
		 <ImportVisitor(grammarName)>
		 }

		 @parser::members {
		 <LRVisitor(grammarName)>
		 }

		 s
		 @after {
		 <ToStringTree("$ctx.r"):writeln()>
		 <WalkVisitor("$ctx.r")>
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

		@Override
		public boolean ignore(String targetName) { return !isJavaScriptTarget(targetName); }
	}

	public static class LRWithLabels extends BaseParserTestDescriptor {
		public String input = "1(2,3)";
		/**
		(e (e 1) ( (eList (e 2) , (e 3)) ))
		1,,2,,3,1 [13 6]
		 */
		@CommentHasStringValue
		public String output;

		public String errors = "";
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 @parser::header {
		 <ImportVisitor(grammarName)>
		 }

		 @parser::members {
		 <LRWithLabelsVisitor(grammarName)>
		 }

		 s
		 @after {
		 <ToStringTree("$ctx.r"):writeln()>
		 <WalkVisitor("$ctx.r")>
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

		@Override
		public boolean ignore(String targetName) { return !isJavaScriptTarget(targetName); }
	}

	public static abstract class RuleGetters extends BaseParserTestDescriptor {
		public String errors = "";
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 @parser::header {
		 <ImportVisitor(grammarName)>
		 }

		 @parser::members {
		 <RuleGetterVisitor(grammarName)>
		 }

		 s
		 @after {
		 <ToStringTree("$ctx.r"):writeln()>
		 <WalkVisitor("$ctx.r")>
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

		@Override
		public boolean ignore(String targetName) { return !isJavaScriptTarget(targetName); }
	}

	public static class RuleGetters_1 extends RuleGetters {
		public String input = "1 2";
		/**
		(a (b 1) (b 2))
		,1 2 1
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
		public String errors = "";
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 @parser::header {
		 <ImportVisitor(grammarName)>
		 }

		 @parser::members {
		 <TokenGetterVisitor(grammarName)>
		 }

		 s
		 @after {
		 <ToStringTree("$ctx.r"):writeln()>
		 <WalkVisitor("$ctx.r")>
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

		@Override
		public boolean ignore(String targetName) { return !isJavaScriptTarget(targetName); }
	}

	public static class TokenGetters_1 extends TokenGetters {
		public String input = "1 2";
		/**
		(a 1 2)
		,1 2 [1, 2]
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
