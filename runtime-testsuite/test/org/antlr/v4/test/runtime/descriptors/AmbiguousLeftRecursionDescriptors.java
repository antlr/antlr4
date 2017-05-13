package org.antlr.v4.test.runtime.descriptors;

import org.antlr.v4.test.runtime.BaseParserTestDescriptor;
import org.antlr.v4.test.runtime.CommentHasStringValue;

public class AmbiguousLeftRecursionDescriptors {
	
	public static abstract class AmbigLR extends BaseParserTestDescriptor {
		public String errors = null;
		public String startRule = "prog";
		public String grammarName = "Expr";

		/**
		 grammar Expr;
		 prog:   stat ;
		 stat:   expr NEWLINE                # printExpr
		     |   ID '=' expr NEWLINE         # assign
		     |   NEWLINE                     # blank
		     ;
		 expr:   expr ('*'|'/') expr      # MulDiv
		     |   expr ('+'|'-') expr      # AddSub
		     |   INT                      # int
		     |   ID                       # id
		     |   '(' expr ')'             # parens
		     ;

		 MUL :   '*' ; // assigns token name to '*' used above in grammar
		 DIV :   '/' ;
		 ADD :   '+' ;
		 SUB :   '-' ;
		 ID  :   [a-zA-Z]+ ;      // match identifiers
		 INT :   [0-9]+ ;         // match integers
		 NEWLINE:'\r'? '\n' ;     // return newlines to parser (is end-statement signal)
		 WS  :   [ \t]+ -> skip ; // toss out whitespace
		 */
		@CommentHasStringValue
		public String grammar;
	}

	public static class AmbigLR_1 extends AmbigLR {
		public String input = "1\n";
		public String output = null;
	}

	public static class AmbigLR_2 extends AmbigLR {
		public String input = "a = 5\n";
		public String output = null;
	}

	public static class AmbigLR_3 extends AmbigLR {
		public String input = "b = 6\n";
		public String output = null;
	}

	public static class AmbigLR_4 extends AmbigLR {
		public String input = "a+b*2\n";
		public String output = null;
	}

	public static class AmbigLR_5 extends AmbigLR {
		public String input = "(1+2)*3\n";
		public String output = null;
	}


}
