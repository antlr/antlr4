/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.descriptors;

import org.antlr.v4.test.runtime.BaseParserTestDescriptor;
import org.antlr.v4.test.runtime.CommentHasStringValue;

public class ParserExecDescriptors {
	public static class APlus extends BaseParserTestDescriptor {
		public String input = "a b c";
		public String output = "abc\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : ID+ {
		 <writeln("$text")>
		 };
		 ID : 'a'..'z'+;
		 WS : (' '|'\n') -> skip;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class AStar_1 extends BaseParserTestDescriptor {
		public String input = "";
		public String output = "\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : ID* {
		 <writeln("$text")>
		 };
		 ID : 'a'..'z'+;
		 WS : (' '|'\n') -> skip;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class AStar_2 extends BaseParserTestDescriptor {
		public String input = "a b c";
		public String output = "abc\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : ID* {
		 <writeln("$text")>
		 };
		 ID : 'a'..'z'+;
		 WS : (' '|'\n') -> skip;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class AorAPlus extends BaseParserTestDescriptor {
		public String input = "a b c";
		public String output = "abc\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : (ID|ID)+ {
		 <writeln("$text")>
		 };
		 ID : 'a'..'z'+;
		 WS : (' '|'\n') -> skip;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class AorAStar_1 extends BaseParserTestDescriptor {
		public String input = "";
		public String output = "\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : (ID|ID)* {
		 <writeln("$text")>
		 };
		 ID : 'a'..'z'+;
		 WS : (' '|'\n') -> skip;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class AorAStar_2 extends BaseParserTestDescriptor {
		public String input = "a b c";
		public String output = "abc\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : (ID|ID)* {
		 <writeln("$text")>
		 };
		 ID : 'a'..'z'+;
		 WS : (' '|'\n') -> skip;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class AorB extends BaseParserTestDescriptor {
		public String input = "34";
		public String output = "alt 2\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : ID {
		 <writeln("\"alt 1\"")>
		 } | INT {
		 <writeln("\"alt 2\"")>
		 };
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+;
		 WS : (' '|'\\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class AorBPlus extends BaseParserTestDescriptor {
		public String input = "a 34 c";
		public String output = "a34c\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : (ID|INT{
		 })+ {
		 <writeln("$text")>
		 };
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+;
		 WS : (' '|'\\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class AorBStar_1 extends BaseParserTestDescriptor {
		public String input = "";
		public String output = "\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : (ID|INT{
		 })* {
		 <writeln("$text")>
		 };
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+;
		 WS : (' '|'\\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class AorBStar_2 extends BaseParserTestDescriptor {
		public String input = "a 34 c";
		public String output = "a34c\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : (ID|INT{
		 })* {
		 <writeln("$text")>
		 };
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+;
		 WS : (' '|'\\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class Basic extends BaseParserTestDescriptor {
		public String input = "abc 34";
		public String output = "abc34\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : ID INT {
		 <writeln("$text")>
		 };
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+;
		 WS : (' '|'\n') -> skip;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	/** Match assignments, ignore other tokens with wildcard. */
	public static class Wildcard extends BaseParserTestDescriptor {
		public String input = "x=10; abc;;;; y=99;";
		public String output = "x=10;\ny=99;\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		grammar T;
		a : (assign|.)+ EOF ;
		assign : ID '=' INT ';' {
		<writeln("$text")>
		} ;
		ID : 'a'..'z'+ ;
		INT : '0'..'9'+;
		WS : (' '|'\n') -> skip;
		*/
		@CommentHasStringValue
		public String grammar;
	}

	/**
	 * This test ensures that {@link org.antlr.v4.runtime.atn.ParserATNSimulator} does not produce a
	 * {@link StackOverflowError} when it encounters an {@code EOF} transition
	 * inside a closure.
	 */
	public static class EOFInClosure extends BaseParserTestDescriptor {
		public String input = "x";
		public String output = null;
		public String errors = null;
		public String startRule = "prog";
		public String grammarName = "T";

		/**
		 grammar T;
		 prog : stat EOF;
		 stat : 'x' ('y' | EOF)*?;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class IfIfElseGreedyBinding1 extends BaseParserTestDescriptor {
		public String input = "if y if y x else x";
		/**
		if y x else x
		if y if y x else x
		 */
		@CommentHasStringValue
		public String output;

		public String errors = null;
		public String startRule = "start";
		public String grammarName = "T";

		/**
		 grammar T;
		 start : statement+ ;
		 statement : 'x' | ifStatement;
		 ifStatement : 'if' 'y' statement ('else' statement)? {
		 <writeln("$text")>
		 };
		 ID : 'a'..'z'+ ;
		 WS : (' '|'\n') -> channel(HIDDEN);
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class IfIfElseGreedyBinding2 extends BaseParserTestDescriptor {
		public String input = "if y if y x else x";
		/**
		if y x else x
		if y if y x else x
		 */
		@CommentHasStringValue
		public String output;

		public String errors = null;
		public String startRule = "start";
		public String grammarName = "T";

		/**
		 grammar T;
		 start : statement+ ;
		 statement : 'x' | ifStatement;
		 ifStatement : 'if' 'y' statement ('else' statement|) {
		 <writeln("$text")>
		 };
		 ID : 'a'..'z'+ ;
		 WS : (' '|'\n') -> channel(HIDDEN);
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class IfIfElseNonGreedyBinding1 extends BaseParserTestDescriptor {
		public String input = "if y if y x else x";
		/**
		if y x
		if y if y x else x
		 */
		@CommentHasStringValue
		public String output;

		public String errors = null;
		public String startRule = "start";
		public String grammarName = "T";

		/**
		 grammar T;
		 start : statement+ ;
		 statement : 'x' | ifStatement;
		 ifStatement : 'if' 'y' statement ('else' statement)?? {
		 <writeln("$text")>
		 };
		 ID : 'a'..'z'+ ;
		 WS : (' '|'\n') -> channel(HIDDEN);
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class IfIfElseNonGreedyBinding2 extends BaseParserTestDescriptor {
		public String input = "if y if y x else x";
		/**
		if y x
		if y if y x else x
		 */
		@CommentHasStringValue
		public String output;

		public String errors = null;
		public String startRule = "start";
		public String grammarName = "T";

		/**
		 grammar T;
		 start : statement+ ;
		 statement : 'x' | ifStatement;
		 ifStatement : 'if' 'y' statement (|'else' statement) {
		 <writeln("$text")>
		 };
		 ID : 'a'..'z'+ ;
		 WS : (' '|'\n') -> channel(HIDDEN);
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class LL1OptionalBlock_1 extends BaseParserTestDescriptor {
		public String input = "";
		public String output = "\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : (ID|{}INT)? {
		 <writeln("$text")>
		 };
		 ID : 'a'..'z'+;
		 INT : '0'..'9'+ ;
		 WS : (' '|'\n') -> skip;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class LL1OptionalBlock_2 extends BaseParserTestDescriptor {
		public String input = "a";
		public String output = "a\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : (ID|{}INT)? {
		 <writeln("$text")>
		 };
		 ID : 'a'..'z'+;
		 INT : '0'..'9'+ ;
		 WS : (' '|'\n') -> skip;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	/**
	 * This is a regression test for antlr/antlr4#195 "label 'label' type
	 * mismatch with previous definition: TOKEN_LABEL!=RULE_LABEL"
	 * https://github.com/antlr/antlr4/issues/195
	 */
	public static class LabelAliasingAcrossLabeledAlternatives extends BaseParserTestDescriptor {
		public String input = "xy";
		/**
		x
		y
		 */
		@CommentHasStringValue
		public String output;

		public String errors = null;
		public String startRule = "start";
		public String grammarName = "T";

		/**
		 grammar T;
		 start : a* EOF;
		 a
		   : label=subrule {<writeln("$label.text")>} #One
		   | label='y' {<writeln("$label.text")>} #Two
		   ;
		 subrule : 'x';
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class Labels extends BaseParserTestDescriptor {
		public String input = "abc 34;";
		public String output = null;
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : b1=b b2+=b* b3+=';' ;
		 b : id_=ID val+=INT*;
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	/**
	 * This is a regression test for antlr/antlr4#299 "Repeating subtree not
	 * accessible in visitor".
	 * https://github.com/antlr/antlr4/issues/299
	 */
	public static class ListLabelForClosureContext extends BaseParserTestDescriptor {
		public String input = "a";
		public String output = null;
		public String errors = null;
		public String startRule = "expression";
		public String grammarName = "T";

		/**
		 grammar T;
		 ifStatement
		 @after {
		 <AssertIsList({<ContextRuleFunction("$ctx", "elseIfStatement()")>})>
		 }
		     : 'if' expression
		       ( ( 'then'
		           executableStatement*
		           elseIfStatement*  // \<--- problem is here; should yield a list not node
		           elseStatement?
		           'end' 'if'
		         ) | executableStatement )
		     ;

		 elseIfStatement
		     : 'else' 'if' expression 'then' executableStatement*
		     ;
		 expression : 'a' ;
		 executableStatement : 'a' ;
		 elseStatement : 'a' ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	/**
	 * This is a regression test for #270 "Fix operator += applied to a set of
	 * tokens".
	 * https://github.com/antlr/antlr4/issues/270
	 */
	public static class ListLabelsOnSet extends BaseParserTestDescriptor {
		public String input = "abc 34;";
		public String output = null;
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : b b* ';' ;
		 b : ID val+=(INT | FLOAT)*;
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+;
		 FLOAT : [0-9]+ '.' [0-9]+;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	/**
	 * This test ensures that {@link ParserATNSimulator} produces a correct
	 * result when the grammar contains multiple explicit references to
	 * {@code EOF} inside of parser rules.
	 */
	public static class MultipleEOFHandling extends BaseParserTestDescriptor {
		public String input = "x";
		public String output = null;
		public String errors = null;
		public String startRule = "prog";
		public String grammarName = "T";

		/**
		 grammar T;
		 prog : ('x' | 'x' 'y') EOF EOF;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	/**
	 * This test is meant to detect regressions of bug antlr/antlr4#41.
	 * https://github.com/antlr/antlr4/issues/41
	 */
	public static class Optional_1 extends BaseParserTestDescriptor {
		public String input = "x";
		public String output = null;
		public String errors = null;
		public String startRule = "stat";
		public String grammarName = "T";

		/**
		 grammar T;
		 stat : ifstat | 'x';
		 ifstat : 'if' stat ('else' stat)?;
		 WS : [ \n\t]+ -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class Optional_2 extends BaseParserTestDescriptor {
		public String input = "if x";
		public String output = null;
		public String errors = null;
		public String startRule = "stat";
		public String grammarName = "T";

		/**
		 grammar T;
		 stat : ifstat | 'x';
		 ifstat : 'if' stat ('else' stat)?;
		 WS : [ \n\t]+ -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class Optional_3 extends BaseParserTestDescriptor {
		public String input = "if x else x";
		public String output = null;
		public String errors = null;
		public String startRule = "stat";
		public String grammarName = "T";

		/**
		 grammar T;
		 stat : ifstat | 'x';
		 ifstat : 'if' stat ('else' stat)?;
		 WS : [ \n\t]+ -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class Optional_4 extends BaseParserTestDescriptor {
		public String input = "if if x else x";
		public String output = null;
		public String errors = null;
		public String startRule = "stat";
		public String grammarName = "T";

		/**
		 grammar T;
		 stat : ifstat | 'x';
		 ifstat : 'if' stat ('else' stat)?;
		 WS : [ \n\t]+ -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	/*
	 * This is a regression test for antlr/antlr4#561 "Issue with parser
	 * generation in 4.2.2"
	 * https://github.com/antlr/antlr4/issues/561
	 */
	public static class ParserProperty extends BaseParserTestDescriptor {
		public String input = "abc";
		public String output = "valid\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 <ParserPropertyMember()>
		 a : {<ParserPropertyCall({$parser}, "Property()")>}? ID {<writeln("\"valid\"")>}
		   ;
		 ID : 'a'..'z'+ ;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	/**
	 * This test is meant to test the expected solution to antlr/antlr4#42.
	 * https://github.com/antlr/antlr4/issues/42
	 */
	public static class PredicatedIfIfElse extends BaseParserTestDescriptor {
		public String input = "if x if x a else b";
		public String output = null;
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		/**
		 grammar T;
		 s : stmt EOF ;
		 stmt : ifStmt | ID;
		 ifStmt : 'if' ID stmt ('else' stmt | { <LANotEquals("1", {T<ParserToken("Parser", "ELSE")>})> }?);
		 ELSE : 'else';
		 ID : [a-zA-Z]+;
		 WS : [ \\n\\t]+ -> skip;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	/**
	 * This is a regression test for antlr/antlr4#334 "BailErrorStrategy: bails
	 * out on proper input".
	 * https://github.com/antlr/antlr4/issues/334
	 */
	public static class PredictionIssue334 extends BaseParserTestDescriptor {
		public String input = "a";
		public String output = "(file_ (item a) <EOF>)\n";
		public String errors = null;
		public String startRule = "file_";
		public String grammarName = "T";

		/**
		 grammar T;
		 file_ @init{
		 <BailErrorStrategy()>
		 }
		 @after {
		 <ToStringTree("$ctx"):writeln()>
		 }
		   :   item (SEMICOLON item)* SEMICOLON? EOF ;
		 item : A B?;
		 SEMICOLON: ';';
		 A : 'a'|'A';
		 B : 'b'|'B';
		 WS      : [ \r\t\n]+ -> skip;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	/**
	 * This is a regression test for antlr/antlr4#561 "Issue with parser
	 * generation in 4.2.2"
	 * https://github.com/antlr/antlr4/issues/561
	 */
	public static class ReferenceToATN_1 extends BaseParserTestDescriptor {
		public String input = "";
		public String output = "\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : (ID|ATN)* ATN? {<writeln("$text")>} ;
		 ID : 'a'..'z'+ ;
		 ATN : '0'..'9'+;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;

	}

	public static class ReferenceToATN_2 extends BaseParserTestDescriptor {
		public String input = "a 34 c";
		public String output = "a34c\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		/**
		 grammar T;
		 a : (ID|ATN)* ATN? {<writeln("$text")>} ;
		 ID : 'a'..'z'+ ;
		 ATN : '0'..'9'+;
		 WS : (' '|'\n') -> skip ;
		 */
		@CommentHasStringValue
		public String grammar;
	}

	/**
	 * This is a regression test for antlr/antlr4#1545, case 1.
	 */
	public static class OpenDeviceStatement_Case1 extends BaseParserTestDescriptor {
		public String input = "OPEN DEVICE DEVICE";
		public String output = "OPEN DEVICE DEVICE\n";
		public String errors = null;
		public String startRule = "statement";
		public String grammarName = "OpenDeviceStatement";

		/**
		 grammar OpenDeviceStatement;
		 program : statement+ '.' ;

		 statement : 'OPEN' ( 'DEVICE' (  OPT1  |  OPT2  |  OPT3  )? )+ {<writeln("$text")>} ;

		 OPT1 : 'OPT-1';
		 OPT2 : 'OPT-2';
		 OPT3 : 'OPT-3';

		 WS : (' '|'\n')+ -> channel(HIDDEN);
		 */
		@CommentHasStringValue
		public String grammar;
	}

	/**
	 * This is a regression test for antlr/antlr4#1545, case 2.
	 */
	public static class OpenDeviceStatement_Case2 extends BaseParserTestDescriptor {
		public String input = "OPEN DEVICE DEVICE";
		public String output = "OPEN DEVICE DEVICE\n";
		public String errors = null;
		public String startRule = "statement";
		public String grammarName = "OpenDeviceStatement";

		/**
		 grammar OpenDeviceStatement;
		 program : statement+ '.' ;

		 statement : 'OPEN' ( 'DEVICE' (  (OPT1)  |  OPT2  |  OPT3  )? )+ {<writeln("$text")>} ;

		 OPT1 : 'OPT-1';
		 OPT2 : 'OPT-2';
		 OPT3 : 'OPT-3';

		 WS : (' '|'\n')+ -> channel(HIDDEN);
		 */
		@CommentHasStringValue
		public String grammar;
	}

	/**
	 * This is a regression test for antlr/antlr4#1545, case 3.
	 */
	public static class OpenDeviceStatement_Case3 extends BaseParserTestDescriptor {
		public String input = "OPEN DEVICE DEVICE.";
		public String output = "OPEN DEVICE DEVICE\n";
		public String errors = null;
		public String startRule = "statement";
		public String grammarName = "OpenDeviceStatement";

		/**
		 grammar OpenDeviceStatement;
		 program : statement+ '.' ;

		 statement : 'OPEN' ( 'DEVICE' (  (OPT1)  |  OPT2  |  OPT3  )? )+ {<writeln("$text")>} ;

		 OPT1 : 'OPT-1';
		 OPT2 : 'OPT-2';
		 OPT3 : 'OPT-3';

		 WS : (' '|'\n')+ -> channel(HIDDEN);
		 */
		@CommentHasStringValue
		public String grammar;
	}

	/**
	 * This is a regression test for antlr/antlr4#2301.
	 */
	public static class OrderingPredicates extends BaseParserTestDescriptor {
		public String input = "POINT AT X";
		public String output = null;
		public String errors = null;
		public String startRule = "expr";
		public String grammarName = "Issue2301";

		/**
		 grammar Issue2301;

		 SPACES: [ \t\r\n]+ -> skip;

		 AT: 'AT';
		 X : 'X';
		 Y : 'Y';

		 ID: [A-Z]+;

		 constant
		 : 'DUMMY'
		 ;

		 expr
		 : ID constant?
		 | expr AT X
		 | expr AT Y
		 ;
		 */
		@CommentHasStringValue
		public String grammar;
	}
}
