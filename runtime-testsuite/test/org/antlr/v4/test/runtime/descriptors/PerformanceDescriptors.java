/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.descriptors;

import org.antlr.v4.test.runtime.BaseParserTestDescriptor;
import org.antlr.v4.test.runtime.CommentHasStringValue;

import java.util.Arrays;

public class PerformanceDescriptors {
    //
    // 	 This is a regression test for antlr/antlr4#192 "Poor performance of
    // 	 expression parsing".
    // 	 https://github.com/antlr/antlr4/issues/192
    //
	public static abstract class ExpressionGrammar extends BaseParserTestDescriptor {
		public String output = null;
		public String errors = null;
		public String startRule = "program";
		public String grammarName = "Expr";

		public String grammar = """
		 grammar Expr;

		 program: expr EOF;

		 expr
		 	: ID
		 	| 'not' expr
		 	| expr 'and' expr
		 	| expr 'or' expr
		 	;

		 ID: [a-zA-Z_][a-zA-Z_0-9]*;
		 WS: [ \t\n\r\f]+ -> skip;
		 ERROR: .;
""";
	}

	public static class ExpressionGrammar_1 extends ExpressionGrammar {
		public String input = """
		not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or
		    X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or
		not X1 and     X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or
		not X1 and not X2 and     X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or
		not X1 and not X2 and not X3 and     X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or
		not X1 and not X2 and not X3 and not X4 and     X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or
		not X1 and not X2 and not X3 and not X4 and not X5 and     X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or
		not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and     X7 and not X8 and not X9 and not X10 and not X11 and not X12 or
		not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and     X8 and not X9 and not X10 and not X11 and not X12 or
		not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and     X9 and not X10 and not X11 and not X12 or
		not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and     X10 and not X11 and not X12 or
		not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and     X11 and not X12 or
		not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and     X12
""";
	}

	public static class ExpressionGrammar_2 extends ExpressionGrammar {
		public String input = """
		not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or     X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and     X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and     X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and     X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and     X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and     X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and     X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and     X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and     X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and     X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and     X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and     X12  or
		not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or     X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and     X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and     X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and     X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and     X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and     X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and     X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and     X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and     X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and     X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and     X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and     X12  or
		not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or     X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and     X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and     X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and     X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and     X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and     X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and     X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and     X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and     X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and     X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and     X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and     X12  or
		not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or     X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and     X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and     X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and     X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and     X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and     X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and     X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and     X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and     X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and     X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and     X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and     X12  or
		not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or     X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and     X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and     X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and     X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and     X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and     X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and     X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and     X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and     X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and     X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and     X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and     X12  or
		not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or     X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and     X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and     X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and     X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and     X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and     X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and     X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and     X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and     X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and     X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and     X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and     X12  or
		not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or     X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and     X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and     X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and     X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and     X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and     X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and     X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and     X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and     X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and     X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and     X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and     X12  or
		not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or     X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and     X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and     X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and     X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and     X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and     X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and     X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and     X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and     X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and     X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and     X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and     X12  or
		not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or     X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and     X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and     X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and     X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and     X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and     X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and     X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and     X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and     X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and     X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and     X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and     X12  or
		not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or     X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and     X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and     X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and     X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and     X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and     X6 and not X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and     X7 and not X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and     X8 and not X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and     X9 and not X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and     X10 and not X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and     X11 and not X12 or not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 and not X8 and not X9 and not X10 and not X11 and     X12
""";
	}

    // 	Test for https://github.com/antlr/antlr4/issues/1398.
    // 	Seeing through a large expression takes 5 _minutes_ on
    // 	my fast box to complete.  After fix, it's instantaneous.
    //
	public static abstract class DropLoopEntryBranchInLRRule extends BaseParserTestDescriptor {
		public String grammarName = "Expr";
		public String startRule = "stat";

		public String grammar = """
		 grammar Expr;

		 stat : expr ';'
		      | expr '.'
		      ;

		 expr
		 	: ID
		 	| 'not' expr
		 	| expr 'and' expr
		 	| expr 'or' expr
		    | '(' ID ')' expr
		    | expr '?' expr ':' expr
		    | 'between' expr 'and' expr
		 	;

		 ID: [a-zA-Z_][a-zA-Z_0-9]*;
		 WS: [ \t\n\r\f]+ -> skip;
""";

		@Override
		public boolean ignore(String targetName) {
			return !Arrays.asList("Java", "CSharp", "Python2", "Python3", "Node", "Cpp", "Swift", "Dart").contains(targetName);
		}
	}

	public static class DropLoopEntryBranchInLRRule_1 extends DropLoopEntryBranchInLRRule {
		public String input = """
		 X1 and X2 and X3 and X4 and X5 and X6 and X7 or
		 X1 and X2 and X3 and X4 and X5 and X6 and X7 or
		 X1 and X2 and X3 and X4 and X5 and X6 and X7 or
		 X1 and X2 and X3 and X4 and X5 and X6 and X7 or
		 X1 and X2 and X3 and X4 and X5 and X6 and X7 or
		 X1 and X2 and X3 and X4 and X5 and X6 and X7 or
		 X1 and X2 and X3 and X4 and X5 and X6 and X7 or
		 X1 and X2 and X3 and X4 and X5 and X6 and X7
		 ;
""";
	}

	public static class DropLoopEntryBranchInLRRule_2 extends DropLoopEntryBranchInLRRule {
		public String input = """
		 X1 and X2 and X3 and X4 and X5 and X6 and X7 or
		 X1 and X2 and X3 and X4 and X5 and X6 and X7 or
		 X1 and X2 and X3 and X4 and X5 and X6 and X7 or
		 X1 and X2 and X3 and X4 and X5 and X6 and X7 or
		 X1 and X2 and X3 and X4 and X5 and X6 and X7 or
		 X1 and X2 and X3 and X4 and X5 and X6 and X7 or
		 X1 and X2 and X3 and X4 and X5 and X6 and X7
		 .
""";
	}

	public static class DropLoopEntryBranchInLRRule_3 extends DropLoopEntryBranchInLRRule {
		public String input = """
		 not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 or
		 not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 or
		 not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 or
		 not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 or
		 not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 or
		 not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 or
		 not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 or
		 not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 or
		 not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 or
		 not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 or
		 not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 or
		 not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 or
		 not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 or
		 not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 or
		 not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 or
		 not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 or
		 not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7 or
		 not X1 and not X2 and not X3 and not X4 and not X5 and not X6 and not X7
		 ;
""";
	}

	public static class DropLoopEntryBranchInLRRule_4 extends DropLoopEntryBranchInLRRule {
		public String input = """
		 between X1 and X2 or between X3 and X4 and
		 between X1 and X2 or between X3 and X4 and
		 between X1 and X2 or between X3 and X4 and
		 between X1 and X2 or between X3 and X4 and
		 between X1 and X2 or between X3 and X4 and
		 between X1 and X2 or between X3 and X4 and
		 between X1 and X2 or between X3 and X4 and
		 between X1 and X2 or between X3 and X4 and
		 between X1 and X2 or between X3 and X4 and
		 between X1 and X2 or between X3 and X4 and
		 between X1 and X2 or between X3 and X4 and
		 between X1 and X2 or between X3 and X4 and
		 between X1 and X2 or between X3 and X4 and
		 between X1 and X2 or between X3 and X4 and
		 between X1 and X2 or between X3 and X4
		 ;
""";

		@Override
		public boolean ignore(String targetName) {
			// passes, but still too slow in Python and JavaScript
			return !Arrays.asList("Java", "CSharp", "Cpp", "Swift", "Dart").contains(targetName);
		}
	}

	public static class DropLoopEntryBranchInLRRule_5 extends DropLoopEntryBranchInLRRule {
		public String input = """
		 X ? Y : Z or
		 X ? Y : Z or
		 X ? Y : Z or
		 X ? Y : Z or
		 X ? Y : Z or
		 X ? Y : Z or
		 X ? Y : Z or
		 X ? Y : Z or
		 X ? Y : Z or
		 X ? Y : Z or
		 X ? Y : Z or
		 X ? Y : Z or
		 X ? Y : Z or
		 X ? Y : Z or
		 X ? Y : Z or
		 X ? Y : Z or
		 X ? Y : Z or
		 X ? Y : Z or
		 X ? Y : Z or
		 X ? Y : Z or
		 X ? Y : Z or
		 X ? Y : Z or
		 X ? Y : Z or
		 X ? Y : Z or
		 X ? Y : Z or
		 X ? Y : Z or
		 X ? Y : Z or
		 X ? Y : Z or
		 X ? Y : Z
		 ;
""";
	}
}
