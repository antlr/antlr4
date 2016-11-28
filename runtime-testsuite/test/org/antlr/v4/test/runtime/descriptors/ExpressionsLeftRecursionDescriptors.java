package org.antlr.v4.test.runtime.descriptors;

import org.antlr.v4.test.runtime.BaseParserTestDescriptor;
import org.antlr.v4.test.runtime.CommentHasStringValue;

public class ExpressionsLeftRecursionDescriptors {
	

	public static abstract class Expressions extends BaseParserTestDescriptor {
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s @after {<ToStringTree("$ctx"):writeln()>} : e EOF ; // must indicate EOF can follow
		 e : e '.' ID
		   | e '.' 'this'
		   | '-' e
		   | e '*' e
		   | e ('+'|'-') e
		   | INT
		   | ID
		   ;
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+ ;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class Expressions_1 extends Expressions {
		public String input = "a";
		public String output = "(s (e a) <EOF>)\n";
	}

	public static class Expressions_2 extends Expressions {
		public String input = "1";
		public String output = "(s (e 1) <EOF>)\n";
	}

	public static class Expressions_3 extends Expressions {
		public String input = "a-1";
		public String output = "(s (e (e a) - (e 1)) <EOF>)\n";
	}

	public static class Expressions_4 extends Expressions {
		public String input = "a.b";
		public String output = "(s (e (e a) . b) <EOF>)\n";
	}

	public static class Expressions_5 extends Expressions {
		public String input = "a.this";
		public String output = "(s (e (e a) . this) <EOF>)\n";
	}

	public static class Expressions_6 extends Expressions {
		public String input = "-a";
		public String output = "(s (e - (e a)) <EOF>)\n";
	}

	public static class Expressions_7 extends Expressions {
		public String input = "-a+b";
		public String output = "(s (e (e - (e a)) + (e b)) <EOF>)\n";
	}

}
