/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.descriptors;

import org.antlr.v4.test.runtime.BaseParserTestDescriptor;
import org.antlr.v4.test.runtime.CommentHasStringValue;

public class LeftRecursionDescriptors {
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

	public static abstract class JavaExpressions extends BaseParserTestDescriptor {
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s @after {<ToStringTree("$ctx"):writeln()>} : e EOF ; // must indicate EOF can follow
		 expressionList
		     :   e (',' e)*
		     ;
		 e   :   '(' e ')'
		     |   'this'
		     |   'super'
		     |   INT
		     |   ID
		     |   typespec '.' 'class'
		     |   e '.' ID
		     |   e '.' 'this'
		     |   e '.' 'super' '(' expressionList? ')'
		     |   e '.' 'new' ID '(' expressionList? ')'
		 	 |   'new' typespec ( '(' expressionList? ')' | ('[' e ']')+)
		     |   e '[' e ']'
		     |   '(' typespec ')' e
		     |   e ('++' | '--')
		     |   e '(' expressionList? ')'
		     |   ('+'|'-'|'++'|'--') e
		     |   ('~'|'!') e
		     |   e ('*'|'/'|'%') e
		     |   e ('+'|'-') e
		     |   e ('\<\<' | '>>>' | '>>') e
		     |   e ('\<=' | '>=' | '>' | '\<') e
		     |   e 'instanceof' e
		     |   e ('==' | '!=') e
		     |   e '&' e
		     |\<assoc=right> e '^' e
		     |   e '|' e
		     |   e '&&' e
		     |   e '||' e
		     |   e '?' e ':' e
		     |\<assoc=right>
		         e ('='
		           |'+='
		           |'-='
		           |'*='
		           |'/='
		           |'&='
		           |'|='
		           |'^='
		           |'>>='
		           |'>>>='
		           |'\<\<='
		           |'%=') e
		     ;
		 typespec
		     : ID
		     | ID '[' ']'
		     | 'int'
		 	 | 'int' '[' ']'
		     ;
		 ID  : ('a'..'z'|'A'..'Z'|'_'|'$')+;
		 INT : '0'..'9'+ ;
		 WS  : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class JavaExpressions_1 extends JavaExpressions {
		public String input = "a|b&c";
		public String output = "(s (e (e a) | (e (e b) & (e c))) <EOF>)\n";
	}

	public static class JavaExpressions_2 extends JavaExpressions {
		public String input = "(a|b)&c";
		public String output = "(s (e (e ( (e (e a) | (e b)) )) & (e c)) <EOF>)\n";
	}

	public static class JavaExpressions_3 extends JavaExpressions {
		public String input = "a > b";
		public String output = "(s (e (e a) > (e b)) <EOF>)\n";
	}

	public static class JavaExpressions_4 extends JavaExpressions {
		public String input = "a >> b";
		public String output = "(s (e (e a) >> (e b)) <EOF>)\n";
	}

	public static class JavaExpressions_5 extends JavaExpressions {
		public String input = "a=b=c";
		public String output = "(s (e (e a) = (e (e b) = (e c))) <EOF>)\n";
	}

	public static class JavaExpressions_6 extends JavaExpressions {
		public String input = "a^b^c";
		public String output = "(s (e (e a) ^ (e (e b) ^ (e c))) <EOF>)\n";
	}

	public static class JavaExpressions_7 extends JavaExpressions {
		public String input = "(T)x";
		public String output = "(s (e ( (typespec T) ) (e x)) <EOF>)\n";
	}

	public static class JavaExpressions_8 extends JavaExpressions {
		public String input = "new A().b";
		public String output = "(s (e (e new (typespec A) ( )) . b) <EOF>)\n";
	}

	public static class JavaExpressions_9 extends JavaExpressions {
		public String input = "(T)t.f()";
		public String output = "(s (e (e ( (typespec T) ) (e (e t) . f)) ( )) <EOF>)\n";
	}

	public static class JavaExpressions_10 extends JavaExpressions {
		public String input = "a.f(x)==T.c";
		public String output = "(s (e (e (e (e a) . f) ( (expressionList (e x)) )) == (e (e T) . c)) <EOF>)\n";
	}

	public static class JavaExpressions_11 extends JavaExpressions {
		public String input = "a.f().g(x,1)";
		public String output = "(s (e (e (e (e (e a) . f) ( )) . g) ( (expressionList (e x) , (e 1)) )) <EOF>)\n";
	}

	public static class JavaExpressions_12 extends JavaExpressions {
		public String input = "new T[((n-1) * x) + 1]";
		public String output = "(s (e new (typespec T) [ (e (e ( (e (e ( (e (e n) - (e 1)) )) * (e x)) )) + (e 1)) ]) <EOF>)\n";
	}

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
