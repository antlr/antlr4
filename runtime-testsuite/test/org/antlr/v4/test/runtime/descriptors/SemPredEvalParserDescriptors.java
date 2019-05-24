/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.descriptors;

import org.antlr.v4.test.runtime.BaseParserTestDescriptor;
import org.antlr.v4.test.runtime.CommentHasStringValue;

public class SemPredEvalParserDescriptors {
	public static class ActionHidesPreds extends BaseParserTestDescriptor {
		public String input = "x x y";
		/**
		alt 1
		alt 1
		alt 1
		 */
		@CommentHasStringValue
		public String output;

		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 @parser::members {<InitIntMember("i","0")>}
		 s : a+ ;
		 a : {<SetMember("i","1")>} ID {<MemberEquals("i","1")>}? {<writeln("\"alt 1\"")>}
		   | {<SetMember("i","2")>} ID {<MemberEquals("i","2")>}? {<writeln("\"alt 2\"")>}
		   ;
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	/** Regular non-forced actions can create side effects used by semantic
	 *  predicates and so we cannot evaluate any semantic predicate
	 *  encountered after having seen a regular action. This includes
	 *  during global follow operations.
	 */
	public static class ActionsHidePredsInGlobalFOLLOW extends BaseParserTestDescriptor {
		public String input = "a!";
		/**
		eval=true
		parse
		 */
		@CommentHasStringValue
		public String output;

		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 @parser::members {
		 <Declare_pred()>
		 }
		 s : e {} {<True():Invoke_pred()>}? {<writeln("\"parse\"")>} '!' ;
		 t : e {} {<False():Invoke_pred()>}? ID ;
		 e : ID | ; // non-LL(1) so we use ATN
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	/**
	 * This is a regression test for antlr/antlr4#196
	 * "element+ in expression grammar doesn't parse properly"
	 * https://github.com/antlr/antlr4/issues/196
	 */
	public static class AtomWithClosureInTranslatedLRRule extends BaseParserTestDescriptor {
		public String input = "a+b+a";
		public String output = null;
		public String errors = null;
		public String startRule = "start";
		public String grammarName = "T";

		/**
		 grammar T;
		 start : e[0] EOF;
		 e[int _p]
		     :   ( 'a' | 'b'+ ) ( {3 >= $_p}? '+' e[4] )*
		     ;

		 */
		@CommentHasStringValue
		public String grammar;

	}

	/** We cannot collect predicates that are dependent on local context if
	 *  we are doing a global follow. They appear as if they were not there at all.
	 */
	public static class DepedentPredsInGlobalFOLLOW extends BaseParserTestDescriptor {
		public String input = "a!";
		/**
		eval=true
		parse
		 */
		@CommentHasStringValue
		public String output;

		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 @parser::members {
		 <Declare_pred()>
		 }
		 s : a[99] ;
		 a[int i] : e {<ValEquals("$i","99"):Invoke_pred()>}? {<writeln("\"parse\"")>} '!' ;
		 b[int i] : e {<ValEquals("$i","99"):Invoke_pred()>}? ID ;
		 e : ID | ; // non-LL(1) so we use ATN
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class DependentPredNotInOuterCtxShouldBeIgnored extends BaseParserTestDescriptor {
		public String input = "a;";
		public String output = "alt 2\n";
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s : b[2] ';' |  b[2] '.' ; // decision in s drills down to ctx-dependent pred in a;
		 b[int i] : a[i] ;
		 a[int i]
		   : {<ValEquals("$i","1")>}? ID {<writeln("\"alt 1\"")>}
		     | {<ValEquals("$i","2")>}? ID {<writeln("\"alt 2\"")>}
		     ;
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+;
		 WS : (' '|'\n') -> skip ;

		 */
		@CommentHasStringValue
		public String grammar;

	}

	/**
	 * This is a regression test for antlr/antlr4#218 "ANTLR4 EOF Related Bug".
	 * https://github.com/antlr/antlr4/issues/218
	 */
	public static class DisabledAlternative extends BaseParserTestDescriptor {
		public String input = "hello";
		public String output = null;
		public String errors = null;
		public String startRule = "cppCompilationUnit";
		public String grammarName = "T";

		/**
		 grammar T;
		 cppCompilationUnit : content+ EOF;
		 content: anything | {<False()>}? .;
		 anything: ANY_CHAR;
		 ANY_CHAR: [_a-zA-Z0-9];
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class IndependentPredNotPassedOuterCtxToAvoidCastException extends BaseParserTestDescriptor {
		public String input = "a;";
		public String output = "alt 2\n";
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s : b ';' |  b '.' ;
		 b : a ;
		 a
		   : {<False()>}? ID {<writeln("\"alt 1\"")>}
		   | {<True()>}? ID {<writeln("\"alt 2\"")>}
		  ;
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class NoTruePredsThrowsNoViableAlt extends BaseParserTestDescriptor {
		public String input = "y 3 x 4";
		public String output = null;
		public String errors = "line 1:0 no viable alternative at input 'y'\n";
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s : a a;
		 a : {<False()>}? ID INT {<writeln("\"alt 1\"")>}
		   | {<False()>}? ID INT {<writeln("\"alt 2\"")>}
		   ;
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class Order extends BaseParserTestDescriptor {
		public String input = "x y";
		/**
		alt 1
		alt 1
		 */
		@CommentHasStringValue
		public String output;

		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s : a {} a; // do 2x: once in ATN, next in DFA;
		 // action blocks lookahead from falling off of 'a'
		 // and looking into 2nd 'a' ref. !ctx dependent pred
		 a : ID {<writeln("\"alt 1\"")>}
		   | {<True()>}?  ID {<writeln("\"alt 2\"")>}
		   ;
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	/** Loopback doesn't eval predicate at start of alt */
	public static abstract class PredFromAltTestedInLoopBack extends BaseParserTestDescriptor {
		public String startRule = "file_";
		public String grammarName = "T";

		/**
		 grammar T;
		 file_
		 @after {<ToStringTree("$ctx"):writeln()>}
		   : para para EOF ;
		 para: paraContent NL NL ;
		 paraContent : ('s'|'x'|{<LANotEquals("2",{T<ParserToken("Parser", "NL")>})>}? NL)+ ;
		 NL : '\n' ;
		 s : 's' ;
		 X : 'x' ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class PredFromAltTestedInLoopBack_1 extends PredFromAltTestedInLoopBack {
		public String input = "s\n\n\nx\n";
		public String output = "(file_ (para (paraContent s) \\n \\n) (para (paraContent \\n x \\n)) <EOF>)\n";
		/**
		line 5:0 mismatched input '<EOF>' expecting {'s', '
		', 'x'}
		 */
		@CommentHasStringValue
		public String errors;

		@Override
		public boolean ignore(String targetName) {
			return !"Java".equals(targetName) && !"Swift".equals(targetName);
		}
	}

	public static class PredFromAltTestedInLoopBack_2 extends PredFromAltTestedInLoopBack {
		public String input = "s\n\n\nx\n\n";
		public String output = "(file_ (para (paraContent s) \\n \\n) (para (paraContent \\n x) \\n \\n) <EOF>)\n";
		public String errors = null;
	}

	public static abstract class PredTestedEvenWhenUnAmbig extends BaseParserTestDescriptor {
		public String errors = null;
		public String startRule = "primary";
		public String grammarName = "T";

		/**
		 grammar T;
		 @parser::members {<InitBooleanMember("enumKeyword",True())>}
		 primary
		     :   ID {<writeln("\"ID \"+$ID.text")>}
		     |   {<GetMember("enumKeyword"):Not()>}? 'enum' {<writeln("\"enum\"")>}
		     ;
		 ID : [a-z]+ ;
		 WS : [ \t\n\r]+ -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class PredTestedEvenWhenUnAmbig_1 extends PredTestedEvenWhenUnAmbig {
		public String input = "abc";
		public String output = "ID abc\n";
	}

	public static class PredTestedEvenWhenUnAmbig_2 extends PredTestedEvenWhenUnAmbig {
		public String input = "enum";
		public String output = null;
		public String errors = "line 1:0 no viable alternative at input 'enum'\n";
	}

	/**
	 * In this case, we're passing a parameter into a rule that uses that
	 * information to predict the alternatives. This is the special case
	 * where we know exactly which context we are in. The context stack
	 * is empty and we have not dipped into the outer context to make a decision.
	 */
	public static class PredicateDependentOnArg extends BaseParserTestDescriptor {
		public String input = "a b";
		/**
		alt 2
		alt 1
		 */
		@CommentHasStringValue
		public String output;

		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 @parser::members {<InitIntMember("i","0")>}
		 s : a[2] a[1];
		 a[int i]
		   : {<ValEquals("$i","1")>}? ID {<writeln("\"alt 1\"")>}
		   | {<ValEquals("$i","2")>}? ID {<writeln("\"alt 2\"")>}
		   ;
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	/**
	 * In this case, we have to ensure that the predicates are not tested
	 * during the closure after recognizing the 1st ID. The closure will
	 * fall off the end of 'a' 1st time and reach into the a[1] rule
	 * invocation. It should not execute predicates because it does not know
	 * what the parameter is. The context stack will not be empty and so
	 * they should be ignored. It will not affect recognition, however. We
	 * are really making sure the ATN simulation doesn't crash with context
	 * object issues when it encounters preds during FOLLOW.
	 */
	public static class PredicateDependentOnArg2 extends BaseParserTestDescriptor {
		public String input = "a b";
		public String output = null;
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 @parser::members {<InitIntMember("i","0")>}
		 s : a[2] a[1];
		 a[int i]
		   : {<ValEquals("$i","1")>}? ID
		   | {<ValEquals("$i","2")>}? ID
		   ;
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	/** During a global follow operation, we still collect semantic
	 *  predicates as long as they are not dependent on local context
	 */
	public static class PredsInGlobalFOLLOW extends BaseParserTestDescriptor {
		public String input = "a!";
		/**
		eval=true
		parse
		 */
		@CommentHasStringValue
		public String output;

		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 @parser::members {
		 <Declare_pred()>
		 }
		 s : e {<True():Invoke_pred()>}? {<writeln("\"parse\"")>} '!' ;
		 t : e {<False():Invoke_pred()>}? ID ;
		 e : ID | ; // non-LL(1) so we use ATN
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class RewindBeforePredEval extends BaseParserTestDescriptor {
		public String input = "y 3 x 4";
		/**
		alt 2
		alt 1
		 */
		@CommentHasStringValue
		public String output;

		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s : a a;
		 a : {<LTEquals("1", "\"x\"")>}? ID INT {<writeln("\"alt 1\"")>}
		   | {<LTEquals("1", "\"y\"")>}? ID INT {<writeln("\"alt 2\"")>}
		   ;
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class Simple extends BaseParserTestDescriptor {
		public String input = "x y 3";
		/**
		alt 2
		alt 2
		alt 3
		 */
		@CommentHasStringValue
		public String output;

		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s : a a a; // do 3x: once in ATN, next in DFA then INT in ATN
		 a : {<False()>}? ID {<writeln("\"alt 1\"")>}
		   | {<True()>}?  ID {<writeln("\"alt 2\"")>}
		   | INT         {<writeln("\"alt 3\"")>}
		   ;
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class SimpleValidate extends BaseParserTestDescriptor {
		public String input = "x";
		public String output = null;
		public String errors = "line 1:0 no viable alternative at input 'x'\n";
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s : a ;
		 a : {<False()>}? ID  {<writeln("\"alt 1\"")>}
		   | {<True()>}?  INT {<writeln("\"alt 2\"")>}
		   ;
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class SimpleValidate2 extends BaseParserTestDescriptor {
		public String input = "3 4 x";
		/**
		alt 2
		alt 2
		 */
		@CommentHasStringValue
		public String output;

		public String errors = "line 1:4 no viable alternative at input 'x'\n";
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s : a a a;
		 a : {<False()>}? ID  {<writeln("\"alt 1\"")>}
		   | {<True()>}?  INT {<writeln("\"alt 2\"")>}
		   ;
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class ToLeft extends BaseParserTestDescriptor {
		public String input = "x x y";
		/**
		alt 2
		alt 2
		alt 2
		 */
		@CommentHasStringValue
		public String output;

		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 	s : a+ ;
		 a : {<False()>}? ID {<writeln("\"alt 1\"")>}
		   | {<True()>}?  ID {<writeln("\"alt 2\"")>}
		   ;
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	/** In this case, we use predicates that depend on global information
	 *  like we would do for a symbol table. We simply execute
	 *  the predicates assuming that all necessary information is available.
	 *  The i++ action is done outside of the prediction and so it is executed.
	 */
	public static class ToLeftWithVaryingPredicate extends BaseParserTestDescriptor {
		public String input = "x x y";
		/**
		i=1
		alt 2
		i=2
		alt 1
		i=3
		alt 2
		 */
		@CommentHasStringValue
		public String output;

		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 @parser::members {<InitIntMember("i","0")>}
		 s : ({<AddMember("i","1")>
		 <write("\"i=\"")>
		 <writeln(GetMember("i"))>} a)+ ;
		 a : {<ModMemberEquals("i","2","0")>}? ID {<writeln("\"alt 1\"")>}
		   | {<ModMemberNotEquals("i","2","0")>}? ID {<writeln("\"alt 2\"")>}
		   ;
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class TwoUnpredicatedAlts extends BaseParserTestDescriptor {
		public String input = "x; y";
		/**
		alt 1
		alt 1
		 */
		@CommentHasStringValue
		public String output;

		/**
		line 1:0 reportAttemptingFullContext d=0 (a), input='x'
		line 1:0 reportAmbiguity d=0 (a): ambigAlts={1, 2}, input='x'
		line 1:3 reportAttemptingFullContext d=0 (a), input='y'
		line 1:3 reportAmbiguity d=0 (a): ambigAlts={1, 2}, input='y'
		 */
		@CommentHasStringValue
		public String errors;

		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s : {<LL_EXACT_AMBIG_DETECTION()>} a ';' a; // do 2x: once in ATN, next in DFA
		 a : ID {<writeln("\"alt 1\"")>}
		   | ID {<writeln("\"alt 2\"")>}
		   | {<False()>}? ID {<writeln("\"alt 3\"")>}
		   ;
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

		@Override
		public boolean showDiagnosticErrors() { return true; }
	}

	public static class TwoUnpredicatedAltsAndOneOrthogonalAlt extends BaseParserTestDescriptor {
		public String input = "34; x; y";
		/**
		alt 1
		alt 2
		alt 2
		 */
		@CommentHasStringValue
		public String output;

		/**
		line 1:4 reportAttemptingFullContext d=0 (a), input='x'
		line 1:4 reportAmbiguity d=0 (a): ambigAlts={2, 3}, input='x'
		line 1:7 reportAttemptingFullContext d=0 (a), input='y'
		line 1:7 reportAmbiguity d=0 (a): ambigAlts={2, 3}, input='y'
		 */
		@CommentHasStringValue
		public String errors;

		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s : {<LL_EXACT_AMBIG_DETECTION()>} a ';' a ';' a;
		 a : INT {<writeln("\"alt 1\"")>}
		   | ID {<writeln("\"alt 2\"")>} // must pick this one for ID since pred is false
		   | ID {<writeln("\"alt 3\"")>}
		   | {<False()>}? ID {<writeln("\"alt 4\"")>}
		   ;
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

		@Override
		public boolean showDiagnosticErrors() { return true; }
	}

	public static class UnpredicatedPathsInAlt extends BaseParserTestDescriptor {
		public String input = "x 4";
		public String output = "alt 1\n";
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s : a {<writeln("\"alt 1\"")>}
		   | b {<writeln("\"alt 2\"")>}
		   ;
		 a : {<False()>}? ID INT
		   | ID INT
		   ;
		 b : ID ID
		   ;
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class ValidateInDFA extends BaseParserTestDescriptor {
		public String input = "x ; y";
		public String output = null;
		/**
		line 1:0 no viable alternative at input 'x'
		line 1:4 no viable alternative at input 'y'
		 */
		@CommentHasStringValue
		public String errors;

		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s : a ';' a;
		 // ';' helps us to resynchronize without consuming
		 // 2nd 'a' reference. We our testing that the DFA also
		 // throws an exception if the validating predicate fails
		 a : {<False()>}? ID  {<writeln("\"alt 1\"")>}
		   | {<True()>}?  INT {<writeln("\"alt 2\"")>}
		   ;
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}
}
