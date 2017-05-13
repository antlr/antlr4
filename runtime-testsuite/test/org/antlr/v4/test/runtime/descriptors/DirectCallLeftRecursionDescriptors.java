package org.antlr.v4.test.runtime.descriptors;

import org.antlr.v4.test.runtime.BaseParserTestDescriptor;
import org.antlr.v4.test.runtime.CommentHasStringValue;

public class DirectCallLeftRecursionDescriptors {
	
	/*
	 * This is a regression test for "Support direct calls to left-recursive
	 * rules".
	 * https://github.com/antlr/antlr4/issues/161
	 */
	public static abstract class DirectCallToLeftRecursiveRule extends BaseParserTestDescriptor {
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a @after {<ToStringTree("$ctx"):writeln()>} : a ID
		   | ID
		   ;
		 ID : 'a'..'z'+ ;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class DirectCallToLeftRecursiveRule_1 extends DirectCallToLeftRecursiveRule {
		public String input = "x";
		public String output = "(a x)\n";
	}

	public static class DirectCallToLeftRecursiveRule_2 extends DirectCallToLeftRecursiveRule {
		public String input = "x y";
		public String output = "(a (a x) y)\n";
	}

	public static class DirectCallToLeftRecursiveRule_3 extends DirectCallToLeftRecursiveRule {
		public String input = "x y z";
		public String output = "(a (a (a x) y) z)\n";
	}

	
}
