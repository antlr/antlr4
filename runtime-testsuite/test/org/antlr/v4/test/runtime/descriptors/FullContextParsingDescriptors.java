/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.descriptors;

import org.antlr.v4.test.runtime.BaseDiagnosticParserTestDescriptor;
import org.antlr.v4.test.runtime.CommentHasStringValue;

public class FullContextParsingDescriptors {
	public static class AmbigYieldsCtxSensitiveDFA extends BaseDiagnosticParserTestDescriptor {
		public String input = "abc";
		/**
		Decision 0:
		s0-ID->:s1^=>1
		 */
		@CommentHasStringValue
		public String output;

		public String errors = "line 1:0 reportAttemptingFullContext d=0 (s), input='abc'\n";
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s @after {<DumpDFA()>}
		 	: ID | ID {} ;
		 ID : 'a'..'z'+;
		 WS : (' '|'\t'|'\n')+ -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;
	}

	public static class AmbiguityNoLoop extends BaseDiagnosticParserTestDescriptor {
		public String input = "a@";
		public String output = "alt 1\n";
		/**
		line 1:2 reportAttemptingFullContext d=0 (prog), input='a@'
		line 1:2 reportAmbiguity d=0 (prog): ambigAlts={1, 2}, input='a@'
		line 1:2 reportAttemptingFullContext d=1 (expr), input='a@'
		line 1:2 reportContextSensitivity d=1 (expr), input='a@'
		 */
		@CommentHasStringValue
		public String errors;

		public String startRule = "prog";
		public String grammarName = "T";

		/**
		 grammar T;
		 prog
		 @init {<LL_EXACT_AMBIG_DETECTION()>}
		 	: expr expr {<writeln("\"alt 1\"")>}
		 	| expr
		 	;
		 expr: '@'
		 	| ID '@'
		 	| ID
		 	;
		 ID  : [a-z]+ ;
		 WS  : [ \r\n\t]+ -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;
	}

	public static class CtxSensitiveDFATwoDiffInput extends BaseDiagnosticParserTestDescriptor {
		public String input = "$ 34 abc @ 34 abc";
		/**
		Decision 2:
		s0-INT->s1
		s1-ID->:s2^=>1
		 */
		@CommentHasStringValue
		public String output;

		/**
		line 1:5 reportAttemptingFullContext d=2 (e), input='34abc'
		line 1:2 reportContextSensitivity d=2 (e), input='34'
		line 1:14 reportAttemptingFullContext d=2 (e), input='34abc'
		line 1:14 reportContextSensitivity d=2 (e), input='34abc'
		 */
		@CommentHasStringValue
		public String errors;

		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s @after {<DumpDFA()>}
		   : ('$' a | '@' b)+ ;
		 a : e ID ;
		 b : e INT ID ;
		 e : INT | ;
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+ ;
		 WS : (' '|'\t'|'\n')+ -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static abstract class CtxSensitiveDFA extends BaseDiagnosticParserTestDescriptor {
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s @after {<DumpDFA()>}
		   : '$' a | '@' b ;
		 a : e ID ;
		 b : e INT ID ;
		 e : INT | ;
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+ ;
		 WS : (' '|'\t'|'\n')+ -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class CtxSensitiveDFA_1 extends CtxSensitiveDFA {
		public String input = "$ 34 abc";
		/**
		Decision 1:
		s0-INT->s1
		s1-ID->:s2^=>1
		 */
		@CommentHasStringValue
		public String output;

		/**
		line 1:5 reportAttemptingFullContext d=1 (e), input='34abc'
		line 1:2 reportContextSensitivity d=1 (e), input='34'
		 */
		@CommentHasStringValue
		public String errors;

	}

	public static class CtxSensitiveDFA_2 extends CtxSensitiveDFA {
		public String input = "@ 34 abc";
		/**
		Decision 1:
		s0-INT->s1
		s1-ID->:s2^=>1
		 */
		@CommentHasStringValue
		public String output;

		/**
		line 1:5 reportAttemptingFullContext d=1 (e), input='34abc'
		line 1:5 reportContextSensitivity d=1 (e), input='34abc'
		 */
		@CommentHasStringValue
		public String errors;
	}

	public static abstract class ExprAmbiguity extends BaseDiagnosticParserTestDescriptor {
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s
		 @init {<LL_EXACT_AMBIG_DETECTION()>}
		 :   expr[0] {<ToStringTree("$expr.ctx"):writeln()>};
		 	expr[int _p]
		 		: ID
		 		(
		 			{5 >= $_p}? '*' expr[6]
		 			| {4 >= $_p}? '+' expr[5]
		 		)*
		 		;
		 ID  : [a-zA-Z]+ ;
		 WS  : [ \r\n\t]+ -> skip ;

		 */
		@CommentHasStringValue
		public String grammar;
	}

	public static class ExprAmbiguity_1 extends ExprAmbiguity {
		public String input = "a+b";
		public String output = "(expr a + (expr b))\n";
		/**
		line 1:1 reportAttemptingFullContext d=1 (expr), input='+'
		line 1:2 reportContextSensitivity d=1 (expr), input='+b'
		 */
		@CommentHasStringValue
		public String errors;
	}

	public static class ExprAmbiguity_2 extends ExprAmbiguity {
		public String input = "a+b*c";
		public String output = "(expr a + (expr b * (expr c)))\n";
		/**
		line 1:1 reportAttemptingFullContext d=1 (expr), input='+'
		line 1:2 reportContextSensitivity d=1 (expr), input='+b'
		line 1:3 reportAttemptingFullContext d=1 (expr), input='*'
		line 1:5 reportAmbiguity d=1 (expr): ambigAlts={1, 2}, input='*c'
		 */
		@CommentHasStringValue
		public String errors;
	}

	public static abstract class FullContextIF_THEN_ELSEParse extends BaseDiagnosticParserTestDescriptor {
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s
		 @init {<LL_EXACT_AMBIG_DETECTION()>}
		 @after {<DumpDFA()>}
		 	: '{' stat* '}' ;
		 stat: 'if' ID 'then' stat ('else' ID)?
		 		| 'return'
		 		;
		 ID : 'a'..'z'+ ;
		 WS : (' '|'\t'|'\n')+ -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class FullContextIF_THEN_ELSEParse_1 extends FullContextIF_THEN_ELSEParse {
		public String input = "{ if x then return }";
		/**
		Decision 1:
		s0-'}'->:s1=>2
		 */
		@CommentHasStringValue
		public String output;
	}

	public static class FullContextIF_THEN_ELSEParse_2 extends FullContextIF_THEN_ELSEParse {
		public String input = "{ if x then return else foo }";
		/**
		Decision 1:
		s0-'else'->:s1^=>1
		 */
		@CommentHasStringValue
		public String output;

		/**
		line 1:19 reportAttemptingFullContext d=1 (stat), input='else'
		line 1:19 reportContextSensitivity d=1 (stat), input='else'
		 */
		@CommentHasStringValue
		public String errors;
	}

	public static class FullContextIF_THEN_ELSEParse_3 extends FullContextIF_THEN_ELSEParse {
		public String input = "{ if x then if y then return else foo }";
		/**
		Decision 1:
		s0-'}'->:s2=>2
		s0-'else'->:s1^=>1
		 */
		@CommentHasStringValue
		public String output;

		/**
		line 1:29 reportAttemptingFullContext d=1 (stat), input='else'
		line 1:38 reportAmbiguity d=1 (stat): ambigAlts={1, 2}, input='elsefoo}'
		 */
		@CommentHasStringValue
		public String errors;
	}

	public static class FullContextIF_THEN_ELSEParse_4 extends FullContextIF_THEN_ELSEParse {
		public String input = "{ if x then if y then return else foo else bar }";
		/**
		Decision 1:
		s0-'else'->:s1^=>1
		 */
		@CommentHasStringValue
		public String output;

		/**
		line 1:29 reportAttemptingFullContext d=1 (stat), input='else'
		line 1:38 reportContextSensitivity d=1 (stat), input='elsefooelse'
		line 1:38 reportAttemptingFullContext d=1 (stat), input='else'
		line 1:38 reportContextSensitivity d=1 (stat), input='else'
		 */
		@CommentHasStringValue
		public String errors;
	}

	public static class FullContextIF_THEN_ELSEParse_5 extends FullContextIF_THEN_ELSEParse {
		/**
		{ if x then return else foo
		if x then if y then return else foo }
		 */
		@CommentHasStringValue
		public String input;

		/**
		Decision 1:
		s0-'}'->:s2=>2
		s0-'else'->:s1^=>1
		 */
		@CommentHasStringValue
		public String output;

		/**
		line 1:19 reportAttemptingFullContext d=1 (stat), input='else'
		line 1:19 reportContextSensitivity d=1 (stat), input='else'
		line 2:27 reportAttemptingFullContext d=1 (stat), input='else'
		line 2:36 reportAmbiguity d=1 (stat): ambigAlts={1, 2}, input='elsefoo}'
		 */
		@CommentHasStringValue
		public String errors;
	}

	public static class FullContextIF_THEN_ELSEParse_6 extends FullContextIF_THEN_ELSEParse {
		/**
		{ if x then return else foo
		if x then if y then return else foo }
		 */
		@CommentHasStringValue
		public String input;

		/**
		Decision 1:
		s0-'}'->:s2=>2
		s0-'else'->:s1^=>1
		 */
		@CommentHasStringValue
		public String output;

		/**
		line 1:19 reportAttemptingFullContext d=1 (stat), input='else'
		line 1:19 reportContextSensitivity d=1 (stat), input='else'
		line 2:27 reportAttemptingFullContext d=1 (stat), input='else'
		line 2:36 reportAmbiguity d=1 (stat): ambigAlts={1, 2}, input='elsefoo}'
		 */
		@CommentHasStringValue
		public String errors;
	}

	/*
	 * Tests predictions for the following case involving closures.
	 * http://www.antlr.org/wiki/display/~admin/2011/12/29/Flaw+in+ANTLR+v3+LL(*)+analysis+algorithm
	 */
	public static class LoopsSimulateTailRecursion extends BaseDiagnosticParserTestDescriptor {
		public String input = "a(i)<-x";
		public String output = "pass: a(i)<-x\n";
		/**
		line 1:3 reportAttemptingFullContext d=3 (expr_primary), input='a(i)'
		line 1:7 reportAmbiguity d=3 (expr_primary): ambigAlts={2, 3}, input='a(i)<-x'
		 */
		@CommentHasStringValue
		public String errors;

		public String startRule = "prog";
		public String grammarName = "T";

		/**
		 grammar T;
		 prog
		 @init {<LL_EXACT_AMBIG_DETECTION()>}
		 	: expr_or_assign*;
		 expr_or_assign
		 	: expr '++' {<writeln("\"fail.\"")>}
		 	|  expr {<writeln("\"pass: \"+$expr.text")>}
		 	;
		 expr: expr_primary ('\<-' ID)?;
		 expr_primary
		 	: '(' ID ')'
		 	| ID '(' ID ')'
		 	| ID
		 	;
		 ID  : [a-z]+ ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class SLLSeesEOFInLLGrammar extends BaseDiagnosticParserTestDescriptor {
		public String input = "34 abc";
		/**
		Decision 0:
		s0-INT->s1
		s1-ID->:s2^=>1
		 */
		@CommentHasStringValue
		public String output;

		/**
		line 1:3 reportAttemptingFullContext d=0 (e), input='34abc'
		line 1:0 reportContextSensitivity d=0 (e), input='34'
		 */
		@CommentHasStringValue
		public String errors;

		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s @after {<DumpDFA()>}
		   : a;
		 a : e ID ;
		 b : e INT ID ;
		 e : INT | ;
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+ ;
		 WS : (' '|'\t'|'\n')+ -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}
}
