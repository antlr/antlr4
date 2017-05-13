package org.antlr.v4.test.runtime.descriptors;

import org.antlr.v4.test.runtime.BaseParserTestDescriptor;
import org.antlr.v4.test.runtime.CommentHasStringValue;

public class TernaryLeftRecursionDescriptors {
	


	/**
	 * This is a regression test for antlr/antlr4#542 "First alternative cannot
	 * be right-associative".
	 * https://github.com/antlr/antlr4/issues/542
	 */
	public static abstract class TernaryExprExplicitAssociativity extends BaseParserTestDescriptor {
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s @after {<ToStringTree("$ctx"):writeln()>} : e EOF; // must indicate EOF can follow or 'a\<EOF>' won't match
		 e :\<assoc=right> e '*' e
		   |\<assoc=right> e '+' e
		   |\<assoc=right> e '?' e ':' e
		   |\<assoc=right> e '=' e
		   | ID
		   ;
		 ID : 'a'..'z'+ ;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class TernaryExprExplicitAssociativity_1 extends TernaryExprExplicitAssociativity {
		public String input = "a";
		public String output = "(s (e a) <EOF>)\n";
	}

	public static class TernaryExprExplicitAssociativity_2 extends TernaryExprExplicitAssociativity {
		public String input = "a+b";
		public String output = "(s (e (e a) + (e b)) <EOF>)\n";
	}

	public static class TernaryExprExplicitAssociativity_3 extends TernaryExprExplicitAssociativity {
		public String input = "a*b";
		public String output = "(s (e (e a) * (e b)) <EOF>)\n";
	}

	public static class TernaryExprExplicitAssociativity_4 extends TernaryExprExplicitAssociativity {
		public String input = "a?b:c";
		public String output = "(s (e (e a) ? (e b) : (e c)) <EOF>)\n";
	}

	public static class TernaryExprExplicitAssociativity_5 extends TernaryExprExplicitAssociativity {
		public String input = "a=b=c";
		public String output = "(s (e (e a) = (e (e b) = (e c))) <EOF>)\n";
	}

	public static class TernaryExprExplicitAssociativity_6 extends TernaryExprExplicitAssociativity {
		public String input = "a?b+c:d";
		public String output = "(s (e (e a) ? (e (e b) + (e c)) : (e d)) <EOF>)\n";
	}

	public static class TernaryExprExplicitAssociativity_7 extends TernaryExprExplicitAssociativity {
		public String input = "a?b=c:d";
		public String output = "(s (e (e a) ? (e (e b) = (e c)) : (e d)) <EOF>)\n";
	}

	public static class TernaryExprExplicitAssociativity_8 extends TernaryExprExplicitAssociativity {
		public String input = "a? b?c:d : e";
		public String output = "(s (e (e a) ? (e (e b) ? (e c) : (e d)) : (e e)) <EOF>)\n";
	}

	public static class TernaryExprExplicitAssociativity_9 extends TernaryExprExplicitAssociativity {
		public String input = "a?b: c?d:e";
		public String output = "(s (e (e a) ? (e b) : (e (e c) ? (e d) : (e e))) <EOF>)\n";
	}

	public static abstract class TernaryExpr extends BaseParserTestDescriptor {
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s @after {<ToStringTree("$ctx"):writeln()>} : e EOF ; // must indicate EOF can follow or 'a\<EOF>' won't match
		 e : e '*' e
		   | e '+' e
		   |\<assoc=right> e '?' e ':' e
		   |\<assoc=right> e '=' e
		   | ID
		   ;
		 ID : 'a'..'z'+ ;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class TernaryExpr_1 extends TernaryExpr {
		public String input = "a";
		public String output = "(s (e a) <EOF>)\n";
	}

	public static class TernaryExpr_2 extends TernaryExpr {
		public String input = "a+b";
		public String output = "(s (e (e a) + (e b)) <EOF>)\n";
	}

	public static class TernaryExpr_3 extends TernaryExpr {
		public String input = "a*b";
		public String output = "(s (e (e a) * (e b)) <EOF>)\n";
	}

	public static class TernaryExpr_4 extends TernaryExpr {
		public String input = "a?b:c";
		public String output = "(s (e (e a) ? (e b) : (e c)) <EOF>)\n";
	}

	public static class TernaryExpr_5 extends TernaryExpr {
		public String input = "a=b=c";
		public String output = "(s (e (e a) = (e (e b) = (e c))) <EOF>)\n";
	}

	public static class TernaryExpr_6 extends TernaryExpr {
		public String input = "a?b+c:d";
		public String output = "(s (e (e a) ? (e (e b) + (e c)) : (e d)) <EOF>)\n";
	}

	public static class TernaryExpr_7 extends TernaryExpr {
		public String input = "a?b=c:d";
		public String output = "(s (e (e a) ? (e (e b) = (e c)) : (e d)) <EOF>)\n";
	}

	public static class TernaryExpr_8 extends TernaryExpr {
		public String input = "a? b?c:d : e";
		public String output = "(s (e (e a) ? (e (e b) ? (e c) : (e d)) : (e e)) <EOF>)\n";
	}

	public static class TernaryExpr_9 extends TernaryExpr {
		public String input = "a?b: c?d:e";
		public String output = "(s (e (e a) ? (e b) : (e (e c) ? (e d) : (e e))) <EOF>)\n";
	}


}
