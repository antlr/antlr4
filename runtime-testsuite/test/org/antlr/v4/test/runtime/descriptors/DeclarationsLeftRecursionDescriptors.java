package org.antlr.v4.test.runtime.descriptors;

import org.antlr.v4.test.runtime.BaseParserTestDescriptor;
import org.antlr.v4.test.runtime.CommentHasStringValue;

public class DeclarationsLeftRecursionDescriptors {
	
	public static abstract class Declarations extends BaseParserTestDescriptor {
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s @after {<ToStringTree("$ctx"):writeln()>} : declarator EOF ; // must indicate EOF can follow
		 declarator
		         : declarator '[' e ']'
		         | declarator '[' ']'
		         | declarator '(' ')'
		         | '*' declarator // binds less tight than suffixes
		         | '(' declarator ')'
		         | ID
		         ;
		 e : INT ;
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+ ;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class Declarations_1 extends Declarations {
		public String input = "a";
		public String output = "(s (declarator a) <EOF>)\n";
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s @after {<ToStringTree("$ctx"):writeln()>} : declarator EOF ; // must indicate EOF can follow
		 declarator
		         : declarator '[' e ']'
		         | declarator '[' ']'
		         | declarator '(' ')'
		         | '*' declarator // binds less tight than suffixes
		         | '(' declarator ')'
		         | ID
		         ;
		 e : INT ;
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+ ;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class Declarations_2 extends Declarations {
		public String input = "*a";
		public String output = "(s (declarator * (declarator a)) <EOF>)\n";
	}

	public static class Declarations_3 extends Declarations {
		public String input = "**a";
		public String output = "(s (declarator * (declarator * (declarator a))) <EOF>)\n";
	}

	public static class Declarations_4 extends Declarations {
		public String input = "a[3]";
		public String output = "(s (declarator (declarator a) [ (e 3) ]) <EOF>)\n";
	}

	public static class Declarations_5 extends Declarations {
		public String input = "b[]";
		public String output = "(s (declarator (declarator b) [ ]) <EOF>)\n";
	}

	public static class Declarations_6 extends Declarations {
		public String input = "(a)";
		public String output = "(s (declarator ( (declarator a) )) <EOF>)\n";
	}

	public static class Declarations_7 extends Declarations {
		public String input = "a[]()";
		public String output = "(s (declarator (declarator (declarator a) [ ]) ( )) <EOF>)\n";
	}

	public static class Declarations_8 extends Declarations {
		public String input = "a[][]";
		public String output = "(s (declarator (declarator (declarator a) [ ]) [ ]) <EOF>)\n";
	}

	public static class Declarations_9 extends Declarations {
		public String input = "*a[]";
		public String output = "(s (declarator * (declarator (declarator a) [ ])) <EOF>)\n";
	}

	public static class Declarations_10 extends Declarations {
		public String input = "(*a)[]";
		public String output = "(s (declarator (declarator ( (declarator * (declarator a)) )) [ ]) <EOF>)\n";
	}


}
