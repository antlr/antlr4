package org.antlr.v4.test.runtime.descriptors;

import org.antlr.v4.test.runtime.BaseParserTestDescriptor;
import org.antlr.v4.test.runtime.CommentHasStringValue;

public class LeftRecursionDescriptors {
	

	public static abstract class LabelsOnOpSubrule extends BaseParserTestDescriptor {
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s @after {<ToStringTree("$ctx"):writeln()>} : e;
		 e : a=e op=('*'|'/') b=e  {}
		   | INT {}
		   | '(' x=e ')' {}
		   ;
		 INT : '0'..'9'+ ;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class LabelsOnOpSubrule_1 extends LabelsOnOpSubrule {
		public String input = "4";
		public String output = "(s (e 4))\n";
	}

	public static class LabelsOnOpSubrule_2 extends LabelsOnOpSubrule {
		public String input = "1*2/3";
		public String output = "(s (e (e (e 1) * (e 2)) / (e 3)))\n";
	}

	public static class LabelsOnOpSubrule_3 extends LabelsOnOpSubrule {
		public String input = "(1/2)*3";
		public String output = "(s (e (e ( (e (e 1) / (e 2)) )) * (e 3)))\n";
	}


	/**
	 * This is a regression test for antlr/antlr4#509 "Incorrect rule chosen in
	 * unambiguous grammar".
	 * https://github.com/antlr/antlr4/issues/509
	 */
	public static class PrecedenceFilterConsidersContext extends BaseParserTestDescriptor {
		public String input = "aa";
		public String output = "(prog (statement (letterA a)) (statement (letterA a)) <EOF>)\n";
		public String errors = null;
		public String startRule = "prog";
		public String grammarName = "T";

		/**
		 grammar T;
		 prog
		 @after {<ToStringTree("$ctx"):writeln()>}
		 : statement* EOF {};
		 statement: letterA | statement letterA 'b' ;
		 letterA: 'a';
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static abstract class PrefixAndOtherAlt extends BaseParserTestDescriptor {
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s @after {<ToStringTree("$ctx"):writeln()>} : expr EOF ;
		 expr : literal
		      | op expr
		      | expr op expr
		      ;
		 literal : '-'? Integer ;
		 op : '+' | '-' ;
		 Integer : [0-9]+ ;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class PrefixAndOtherAlt_1 extends PrefixAndOtherAlt {
		public String input = "-1";
		public String output = "(s (expr (literal - 1)) <EOF>)\n";
	}

	public static class PrefixAndOtherAlt_2 extends PrefixAndOtherAlt {
		public String input = "-1 + -1";
		public String output = "(s (expr (expr (literal - 1)) (op +) (expr (literal - 1))) <EOF>)\n";
	}

	public static abstract class PrefixOpWithActionAndLabel extends BaseParserTestDescriptor {
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s : e {<writeln("$e.result")>} ;
		 e returns [<StringType()> result]
		     :   ID '=' e1=e    {$result = "(" + $ID.text + "=" + $e1.result + ")";}
		     |   ID             {$result = $ID.text;}
		     |   e1=e '+' e2=e  {$result = "(" + $e1.result + "+" + $e2.result + ")";}
		     ;
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+ ;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class PrefixOpWithActionAndLabel_1 extends PrefixOpWithActionAndLabel {
		public String input = "a";
		public String output = "a\n";
	}

	public static class PrefixOpWithActionAndLabel_2 extends PrefixOpWithActionAndLabel {
		public String input = "a+b";
		public String output = "(a+b)\n";
	}

	public static class PrefixOpWithActionAndLabel_3 extends PrefixOpWithActionAndLabel {
		public String input = "a=b+c";
		public String output = "((a=b)+c)\n";
	}



	public static class SemPred extends BaseParserTestDescriptor {
		public String input = "x y z";
		public String output = "(s (a (a (a x) y) z))\n";
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s @after {<ToStringTree("$ctx"):writeln()>} : a ;
		 a : a {<True()>}? ID
		   | ID
		   ;
		 ID : 'a'..'z'+ ;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class SemPredFailOption extends BaseParserTestDescriptor {
		public String input = "x y z";
		public String output = "(s (a (a x) y z))\n";
		public String errors = "line 1:4 rule a custom message\n";
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s @after {<ToStringTree("$ctx"):writeln()>} : a ;
		 a : a ID {<False()>}?\<fail='custom message'>
		   | ID
		   ;
		 ID : 'a'..'z'+ ;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static abstract class Simple extends BaseParserTestDescriptor {
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s @after {<ToStringTree("$ctx"):writeln()>} : a ;
		 a : a ID
		   | ID
		   ;
		 ID : 'a'..'z'+ ;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class Simple_1 extends Simple {
		public String input = "x";
		public String output = "(s (a x))\n";
	}

	public static class Simple_2 extends Simple {
		public String input = "x y";
		public String output = "(s (a (a x) y))\n";
	}

	public static class Simple_3 extends Simple {
		public String input = "x y z";
		public String output = "(s (a (a (a x) y) z))\n";
	}

	/*
	 * This is a regression test for #239 "recoursive parser using implicit tokens
	 * ignore white space lexer rule".
	 * https://github.com/antlr/antlr4/issues/239
	 */
	public static abstract class WhitespaceInfluence extends BaseParserTestDescriptor {
		public String input = "Test(1,3)";
		public String output = null;
		public String errors = null;
		public String startRule = "prog";
		public String grammarName = "Expr";

		/**
		 grammar Expr;
		 prog : expression EOF;
		 expression
		     : ID '(' expression (',' expression)* ')'               # doFunction
		     | '(' expression ')'                                    # doParenthesis
		     | '!' expression                                        # doNot
		     | '-' expression                                        # doNegate
		     | '+' expression                                        # doPositiv
		     | expression '^' expression                             # doPower
		     | expression '*' expression                             # doMultipy
		     | expression '/' expression                             # doDivide
		     | expression '%' expression                             # doModulo
		     | expression '-' expression                             # doMinus
		     | expression '+' expression                             # doPlus
		     | expression '=' expression                             # doEqual
		     | expression '!=' expression                            # doNotEqual
		     | expression '>' expression                             # doGreather
		     | expression '>=' expression                            # doGreatherEqual
		     | expression '\<' expression                             # doLesser
		     | expression '\<=' expression                            # doLesserEqual
		     | expression K_IN '(' expression (',' expression)* ')'  # doIn
		     | expression ( '&' | K_AND) expression                  # doAnd
		     | expression ( '|' | K_OR) expression                   # doOr
		     | '[' expression (',' expression)* ']'                  # newArray
		     | K_TRUE                                                # newTrueBoolean
		     | K_FALSE                                               # newFalseBoolean
		     | NUMBER                                                # newNumber
		     | DATE                                                  # newDateTime
		     | ID                                                    # newIdentifier
		     | SQ_STRING                                             # newString
		     | K_NULL                                                # newNull
		     ;

		 // Fragments
		 fragment DIGIT    : '0' .. '9';
		 fragment UPPER    : 'A' .. 'Z';
		 fragment LOWER    : 'a' .. 'z';
		 fragment LETTER   : LOWER | UPPER;
		 fragment WORD     : LETTER | '_' | '$' | '#' | '.';
		 fragment ALPHANUM : WORD | DIGIT;

		 // Tokens
		 ID              : LETTER ALPHANUM*;
		 NUMBER          : DIGIT+ ('.' DIGIT+)? (('e'|'E')('+'|'-')? DIGIT+)?;
		 DATE            : '\'' DIGIT DIGIT DIGIT DIGIT '-' DIGIT DIGIT '-' DIGIT DIGIT (' ' DIGIT DIGIT ':' DIGIT DIGIT ':' DIGIT DIGIT ('.' DIGIT+)?)? '\'';
		 SQ_STRING       : '\'' ('\'\'' | ~'\'')* '\'';
		 DQ_STRING       : '"' ('\\\\"' | ~'"')* '"';
		 WS              : [ \t\n\r]+ -> skip ;
		 COMMENTS        : ('/*' .*? '*' '/' | '//' ~'\n'* '\n' ) -> skip;
		 */
		@CommentHasStringValue
		public String grammar;
	}

	public static class WhitespaceInfluence_1 extends WhitespaceInfluence {
		public String input = "Test(1,3)";
		public String output = null;
	}

	public static class WhitespaceInfluence_2 extends WhitespaceInfluence {
		public String input = "Test(1, 3)";
		public String output = null;
	}
}
