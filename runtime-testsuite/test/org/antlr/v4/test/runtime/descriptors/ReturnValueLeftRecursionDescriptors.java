package org.antlr.v4.test.runtime.descriptors;

import org.antlr.v4.test.runtime.BaseParserTestDescriptor;
import org.antlr.v4.test.runtime.CommentHasStringValue;

public class ReturnValueLeftRecursionDescriptors {
	

	public static abstract class ReturnValueAndActionsAndLabels extends BaseParserTestDescriptor {
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s : q=e {<writeln("$e.v")>};
		 e returns [int v]
		   : a=e op='*' b=e {$v = $a.v * $b.v;}  # mult
		   | a=e '+' b=e {$v = $a.v + $b.v;}     # add
		   | INT         {$v = $INT.int;}        # anInt
		   | '(' x=e ')' {$v = $x.v;}            # parens
		   | x=e '++'    {$v = $x.v+1;}          # inc
		   | e '--'                              # dec
		   | ID          {$v = 3;}               # anID
		   ;
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+ ;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class ReturnValueAndActionsAndLabels_1 extends ReturnValueAndActionsAndLabels {
		public String input = "4";
		public String output = "4\n";
	}

	public static class ReturnValueAndActionsAndLabels_2 extends ReturnValueAndActionsAndLabels {
		public String input = "1+2";
		public String output = "3\n";
	}

	public static class ReturnValueAndActionsAndLabels_3 extends ReturnValueAndActionsAndLabels {
		public String input = "1+2*3";
		public String output = "7\n";
	}

	public static class ReturnValueAndActionsAndLabels_4 extends ReturnValueAndActionsAndLabels {
		public String input = "i++*3";
		public String output = "12\n";
	}

	/*
	 * This is a regression test for antlr/antlr4#677 "labels not working in grammar
	 * file".
	 * https://github.com/antlr/antlr4/issues/677
	 *
	 * This test treats `,` and `>>` as part of a single compound operator (similar
	 * to a ternary operator).
	 */
	public static abstract class ReturnValueAndActionsList extends BaseParserTestDescriptor {
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s @after {<ToStringTree("$ctx"):writeln()>} : expr EOF;
		 expr:
		     a=expr '*' a=expr #Factor
		     | b+=expr (',' b+=expr)* '>>' c=expr #Send
		     | ID #JustId //semantic check on modifiers
		 ;

		 ID  : ('a'..'z'|'A'..'Z'|'_')
		       ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
		 ;

		 WS : [ \t\n]+ -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class ReturnValueAndActionsList1_1 extends ReturnValueAndActionsList {
		public String input = "a*b";
		public String output = "(s (expr (expr a) * (expr b)) <EOF>)\n";
	}

	public static class ReturnValueAndActionsList1_2 extends ReturnValueAndActionsList {
		public String input = "a,c>>x";
		public String output = "(s (expr (expr a) , (expr c) >> (expr x)) <EOF>)\n";
	}

	public static class ReturnValueAndActionsList1_3 extends ReturnValueAndActionsList {
		public String input = "x";
		public String output = "(s (expr x) <EOF>)\n";
	}

	public static class ReturnValueAndActionsList1_4 extends ReturnValueAndActionsList {
		public String input = "a*b,c,x*y>>r";
		public String output = "(s (expr (expr (expr a) * (expr b)) , (expr c) , (expr (expr x) * (expr y)) >> (expr r)) <EOF>)\n";
	}

	/*
	 * This is a regression test for antlr/antlr4#677 "labels not working in grammar
	 * file".
	 * https://github.com/antlr/antlr4/issues/677
	 *
	 * This test treats the `,` and `>>` operators separately.
	 */
	public static abstract class ReturnValueAndActionsList2 extends BaseParserTestDescriptor {
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s @after {<ToStringTree("$ctx"):writeln()>} : expr EOF;
		 expr:
		     a=expr '*' a=expr #Factor
		     | b+=expr ',' b+=expr #Comma
		     | b+=expr '>>' c=expr #Send
		     | ID #JustId //semantic check on modifiers
		 	;
		 ID  : ('a'..'z'|'A'..'Z'|'_')
		       ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
		 ;
		 WS : [ \t\n]+ -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class ReturnValueAndActionsList2_1 extends ReturnValueAndActionsList2 {
		public String input = "a*b";
		public String output = "(s (expr (expr a) * (expr b)) <EOF>)\n";
	}

	public static class ReturnValueAndActionsList2_2 extends ReturnValueAndActionsList2 {
		public String input = "a,c>>x";
		public String output = "(s (expr (expr (expr a) , (expr c)) >> (expr x)) <EOF>)\n";
	}

	public static class ReturnValueAndActionsList2_3 extends ReturnValueAndActionsList2 {
		public String input = "x";
		public String output = "(s (expr x) <EOF>)\n";
	}

	public static class ReturnValueAndActionsList2_4 extends ReturnValueAndActionsList2 {
		public String input = "a*b,c,x*y>>r";
		public String output = "(s (expr (expr (expr (expr (expr a) * (expr b)) , (expr c)) , (expr (expr x) * (expr y))) >> (expr r)) <EOF>)\n";
	}

	public static abstract class ReturnValueAndActions extends BaseParserTestDescriptor {
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s : e {<writeln("$e.v")>};
		 e returns [int v, <StringList()> ignored]
		   : a=e '*' b=e {$v = $a.v * $b.v;}
		   | a=e '+' b=e {$v = $a.v + $b.v;}
		   | INT {$v = $INT.int;}
		   | '(' x=e ')' {$v = $x.v;}
		   ;
		 INT : '0'..'9'+ ;
		 WS : (' '|'\n') -> skip ;

		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class ReturnValueAndActions_1 extends ReturnValueAndActions {
		public String input = "4";
		public String output = "4\n";
	}

	public static class ReturnValueAndActions_2 extends ReturnValueAndActions {
		public String input = "1+2";
		public String output = "3\n";
	}

	public static class ReturnValueAndActions_3 extends ReturnValueAndActions {
		public String input = "1+2*3";
		public String output = "7\n";
	}

	public static class ReturnValueAndActions_4 extends ReturnValueAndActions {
		public String input = "(1+2)*3";
		public String output = "9\n";
	}

}
