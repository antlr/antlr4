/*
 * Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.descriptors;

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.test.runtime.BaseParserTestDescriptor;
import org.antlr.v4.test.runtime.CommentHasStringValue;

public class VisitorsDescriptors {
	/**
	 * This test verifies the basic behavior of visitors, with an emphasis on
	 * {@link AbstractParseTreeVisitor#visitTerminal}.
	 */
	public static class TestVisitTerminalNode extends BaseParserTestDescriptor {
		public String input = "A";
		/**
		(s A <EOF>)
		[@0,0:0='A',<1>,1:0]
		[@1,1:0='<EOF>',<-1>,1:1]

		 */
		@CommentHasStringValue
		public String output;

		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		grammar T;

		<ImportVisitor("T")>
		<TerminalVisitor("T")>

		s
		@after {
		<ToStringTree("$ctx"):writeln()>
		<WalkVisitor("$ctx")>
		}
			: 'A' EOF
			;
		*/
		@CommentHasStringValue
		public String grammar;

		@Override
		public boolean ignore(String targetName) {
			return !"Java".equals(targetName);
		}
	}

	/**
	 * This test verifies the basic behavior of visitors, with an emphasis on
	 * {@link AbstractParseTreeVisitor#visitTerminal}.
	 */
	public static class TestVisitErrorNode extends BaseParserTestDescriptor {
		public String input = "";
		/**
		(s <missing 'A'> <EOF>)
		Error encountered: [@-1,-1:-1='<missing 'A'>',<1>,1:0]
		 */
		@CommentHasStringValue
		public String output;

		public String errors = "line 1:0 missing 'A' at '<EOF>'\n";
		public String startRule = "s";
		public String grammarName = "T";

		/**
		grammar T;

		<ImportVisitor("T")>
		<ErrorVisitor("T")>

		s
		@after {
		<ToStringTree("$ctx"):writeln()>
		<WalkVisitor("$ctx")>
		}
			: 'A' EOF
			;
		*/
		@CommentHasStringValue
		public String grammar;

		@Override
		public boolean ignore(String targetName) {
			return !"Java".equals(targetName);
		}
	}

	/**
	 * This test verifies that {@link AbstractParseTreeVisitor#visitChildren} does not call
	 * {@link ParseTreeVisitor#visit} after {@link AbstractParseTreeVisitor#shouldVisitNextChild} returns
	 * {@code false}.
	 */
	public static class TestShouldNotVisitEOF extends BaseParserTestDescriptor {
		public String input = "A";
		/**
		(s A <EOF>)
		[@0,0:0='A',<1>,1:0]

		 */
		@CommentHasStringValue
		public String output;

		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		grammar T;

		<ImportVisitor("T")>
		<ShouldNotVisitEOFVisitor("T")>

		s
		@after {
		<ToStringTree("$ctx"):writeln()>
		<WalkVisitor("$ctx")>
		}
			: 'A' EOF
			;
		*/
		@CommentHasStringValue
		public String grammar;

		@Override
		public boolean ignore(String targetName) {
			return !"Java".equals(targetName);
		}
	}

	/**
	 * This test verifies that {@link AbstractParseTreeVisitor#shouldVisitNextChild} is called before visiting the first
	 * child. It also verifies that {@link AbstractParseTreeVisitor#defaultResult} provides the default return value for
	 * visiting a tree.
	 */
	public static class TestShouldNotVisitTerminal extends BaseParserTestDescriptor {
		public String input = "A";
		/**
		(s A <EOF>)
		default result
		 */
		@CommentHasStringValue
		public String output;

		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		grammar T;

		<ImportVisitor("T")>
		<ShouldNotVisitTerminalVisitor("T")>

		s
		@after {
		<ToStringTree("$ctx"):writeln()>
		<WalkVisitor("$ctx")>
		}
			: 'A' EOF
			;
		*/
		@CommentHasStringValue
		public String grammar;

		@Override
		public boolean ignore(String targetName) {
			return !"Java".equals(targetName);
		}
	}

	/**
	 * This test verifies that the visitor correctly dispatches calls for labeled outer alternatives.
	 */
	public static class TestCalculatorVisitor extends BaseParserTestDescriptor {
		public String input = "2 + 8 / 2";
		/**
		(s (expr (expr 2) + (expr (expr 8) / (expr 2))) <EOF>)
		6
		 */
		@CommentHasStringValue
		public String output;

		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		grammar T;

		<ImportVisitor("T")>
		<CalculatorVisitor("T")>

		s
		@after {
		<ToStringTree("$ctx"):writeln()>
		<WalkVisitor("$ctx")>
		}
			: expr EOF
			;
		expr
			:	INT						# number
			|	expr (MUL | DIV) expr	# multiply
			|	expr (ADD | SUB) expr	# add
			;

		INT	: [0-9]+;
		MUL : '*';
		DIV : '/';
		ADD : '+';
		SUB : '-';
		WS : [ \t]+ -> channel(HIDDEN);
		*/
		@CommentHasStringValue
		public String grammar;

		@Override
		public boolean ignore(String targetName) {
			return !"Java".equals(targetName);
		}
	}
}
