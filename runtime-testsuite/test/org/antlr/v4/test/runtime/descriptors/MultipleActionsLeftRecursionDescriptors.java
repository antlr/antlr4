package org.antlr.v4.test.runtime.descriptors;

import org.antlr.v4.test.runtime.BaseParserTestDescriptor;
import org.antlr.v4.test.runtime.CommentHasStringValue;

public class MultipleActionsLeftRecursionDescriptors {
	


	/*
	 * This is a regression test for antlr/antlr4#625 "Duplicate action breaks
	 * operator precedence"
	 * https://github.com/antlr/antlr4/issues/625
	 */
	public static abstract class MultipleActionsPredicatesOptions extends BaseParserTestDescriptor {
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s @after {<ToStringTree("$ctx"):writeln()>} : e ;
		 e : a=e op=('*'|'/') b=e  {}{<True()>}?
		   | a=e op=('+'|'-') b=e  {}\<p=3>{<True()>}?\<fail='Message'>
		   | INT {}{}
		   | '(' x=e ')' {}{}
		   ;
		 INT : '0'..'9'+ ;
		 WS : (' '|'\n') -> skip;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class MultipleActionsPredicatesOptions_1 extends MultipleActionsPredicatesOptions {
		public String input = "4";
		public String output = "(s (e 4))\n";
	}

	public static class MultipleActionsPredicatesOptions_2 extends MultipleActionsPredicatesOptions {
		public String input = "1*2/3";
		public String output = "(s (e (e (e 1) * (e 2)) / (e 3)))\n";
	}

	public static class MultipleActionsPredicatesOptions_3 extends MultipleActionsPredicatesOptions {
		public String input = "(1/2)*3";
		public String output = "(s (e (e ( (e (e 1) / (e 2)) )) * (e 3)))\n";
	}

	/*
	 * This is a regression test for antlr/antlr4#625 "Duplicate action breaks
	 * operator precedence"
	 * https://github.com/antlr/antlr4/issues/625
	 */
	public static abstract class MultipleActions extends BaseParserTestDescriptor {
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s @after {<ToStringTree("$ctx"):writeln()>} : e ;
		 e : a=e op=('*'|'/') b=e  {}{}
		   | INT {}{}
		   | '(' x=e ')' {}{}
		   ;
		 INT : '0'..'9'+ ;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class MultipleActions_1 extends MultipleActions {
		public String input = "4";
		public String output = "(s (e 4))\n";
		public String errors = null;
	}

	public static class MultipleActions_2 extends MultipleActions {
		public String input = "1*2/3";
		public String output = "(s (e (e (e 1) * (e 2)) / (e 3)))\n";
	}

	public static class MultipleActions_3 extends MultipleActions {
		public String input = "(1/2)*3";
		public String output = "(s (e (e ( (e (e 1) / (e 2)) )) * (e 3)))\n";
	}

	/*
	 * This is a regression test for antlr/antlr4#433 "Not all context accessor
	 * methods are generated when an alternative rule label is used for multiple
	 * alternatives".
	 * https://github.com/antlr/antlr4/issues/433
	 */
	public static abstract class MultipleAlternativesWithCommonLabel extends BaseParserTestDescriptor {
		public String startRule = "s";
		public String grammarName = "T";
		public String errors = null;

		/**
		 grammar T;
		 s : e {<writeln("$e.v")>};
		 e returns [int v]
		   : e '*' e     {$v = <Cast("BinaryContext","$ctx"):ContextMember({<Production("e")>(0)}, {<Result("v")>})> * <Cast("BinaryContext","$ctx"):ContextMember({<Production("e")>(1)}, {<Result("v")>})>;}  # binary
		   | e '+' e     {$v = <Cast("BinaryContext","$ctx"):ContextMember({<Production("e")>(0)}, {<Result("v")>})> + <Cast("BinaryContext","$ctx"):ContextMember({<Production("e")>(1)}, {<Result("v")>})>;}  # binary
		   | INT         {$v = $INT.int;}                   # anInt
		   | '(' e ')'   {$v = $e.v;}                       # parens
		   | left=e INC  {<Cast("UnaryContext","$ctx"):Concat(".INC() != null"):Assert()>$v = $left.v + 1;}      # unary
		   | left=e DEC  {<Cast("UnaryContext","$ctx"):Concat(".DEC() != null"):Assert()>$v = $left.v - 1;}      # unary
		   | ID          {<AssignLocal("$v","3")>}                                                     # anID
		   ;
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+ ;
		 INC : '++' ;
		 DEC : '--' ;
		 WS : (' '|'\n') -> skip;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class MultipleAlternativesWithCommonLabel_1 extends MultipleAlternativesWithCommonLabel {
		public String input = "4";
		public String output = "4\n";
	}

	public static class MultipleAlternativesWithCommonLabel_2 extends MultipleAlternativesWithCommonLabel {
		public String input = "1+2";
		public String output = "3\n";
	}

	public static class MultipleAlternativesWithCommonLabel_3 extends MultipleAlternativesWithCommonLabel {
		public String input = "1+2*3";
		public String output = "7\n";
	}

	public static class MultipleAlternativesWithCommonLabel_4 extends MultipleAlternativesWithCommonLabel {
		public String input = "i++*3";
		public String output = "12\n";
	}

	/** Test for https://github.com/antlr/antlr4/issues/1295 in addition to #433. */
	public static class MultipleAlternativesWithCommonLabel_5 extends MultipleAlternativesWithCommonLabel {
		public String input = "(99)+3";
		public String output = "102\n";
	}


}
