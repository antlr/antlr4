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

		public String grammar = """
		 grammar T;
		 a : ID+ {
		 <writeln("$text")>
		 };
		 ID : 'a'..'z'+;
		 WS : (' '|'\n') -> skip;
""";

	}

	public static class AStar_1 extends BaseParserTestDescriptor {
		public String input = "";
		public String output = "\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 a : ID* {
		 <writeln("$text")>
		 };
		 ID : 'a'..'z'+;
		 WS : (' '|'\n') -> skip;
""";

	}

	public static class AStar_2 extends BaseParserTestDescriptor {
		public String input = "a b c";
		public String output = "abc\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 a : ID* {
		 <writeln("$text")>
		 };
		 ID : 'a'..'z'+;
		 WS : (' '|'\n') -> skip;
""";

	}

	public static class AorAPlus extends BaseParserTestDescriptor {
		public String input = "a b c";
		public String output = "abc\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 a : (ID|ID)+ {
		 <writeln("$text")>
		 };
		 ID : 'a'..'z'+;
		 WS : (' '|'\n') -> skip;
""";

	}

	public static class AorAStar_1 extends BaseParserTestDescriptor {
		public String input = "";
		public String output = "\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 a : (ID|ID)* {
		 <writeln("$text")>
		 };
		 ID : 'a'..'z'+;
		 WS : (' '|'\n') -> skip;
""";

	}

	public static class AorAStar_2 extends BaseParserTestDescriptor {
		public String input = "a b c";
		public String output = "abc\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 a : (ID|ID)* {
		 <writeln("$text")>
		 };
		 ID : 'a'..'z'+;
		 WS : (' '|'\n') -> skip;
""";

	}

	public static class AorB extends BaseParserTestDescriptor {
		public String input = "34";
		public String output = "alt 2\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 a : ID {
		 <writeln("\"alt 1\"")>
		 } | INT {
		 <writeln("\"alt 2\"")>
		 };
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+;
		 WS : (' '|'\\n') -> skip ;
""";

	}

	public static class AorBPlus extends BaseParserTestDescriptor {
		public String input = "a 34 c";
		public String output = "a34c\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 a : (ID|INT{
		 })+ {
		 <writeln("$text")>
		 };
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+;
		 WS : (' '|'\\n') -> skip ;
""";

	}

	public static class AorBStar_1 extends BaseParserTestDescriptor {
		public String input = "";
		public String output = "\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 a : (ID|INT{
		 })* {
		 <writeln("$text")>
		 };
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+;
		 WS : (' '|'\\n') -> skip ;
""";

	}

	public static class AorBStar_2 extends BaseParserTestDescriptor {
		public String input = "a 34 c";
		public String output = "a34c\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 a : (ID|INT{
		 })* {
		 <writeln("$text")>
		 };
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+;
		 WS : (' '|'\\n') -> skip ;
""";

	}

	public static class Basic extends BaseParserTestDescriptor {
		public String input = "abc 34";
		public String output = "abc34\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 a : ID INT {
		 <writeln("$text")>
		 };
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+;
		 WS : (' '|'\n') -> skip;
""";

	}

	public String grammar = """
 Match assignments, ignore other tokens with wildcard.
""";
	}

	public String grammar = """
	 * This test ensures that {@link org.antlr.v4.runtime.atn.ParserATNSimulator} does not produce a
	 * {@link StackOverflowError} when it encounters an {@code EOF} transition
	 * inside a closure.
""";

	}

	public static class IfIfElseGreedyBinding1 extends BaseParserTestDescriptor {
		public String input = "if y if y x else x";
		public String output = """
		if y x else x
		if y if y x else x
""";

		public String errors = null;
		public String startRule = "start";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 start : statement+ ;
		 statement : 'x' | ifStatement;
		 ifStatement : 'if' 'y' statement ('else' statement)? {
		 <writeln("$text")>
		 };
		 ID : 'a'..'z'+ ;
		 WS : (' '|'\n') -> channel(HIDDEN);
""";

	}

	public static class IfIfElseGreedyBinding2 extends BaseParserTestDescriptor {
		public String input = "if y if y x else x";
		public String output = """
		if y x else x
		if y if y x else x
""";

		public String errors = null;
		public String startRule = "start";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 start : statement+ ;
		 statement : 'x' | ifStatement;
		 ifStatement : 'if' 'y' statement ('else' statement|) {
		 <writeln("$text")>
		 };
		 ID : 'a'..'z'+ ;
		 WS : (' '|'\n') -> channel(HIDDEN);
""";

	}

	public static class IfIfElseNonGreedyBinding1 extends BaseParserTestDescriptor {
		public String input = "if y if y x else x";
		public String output = """
		if y x
		if y if y x else x
""";

		public String errors = null;
		public String startRule = "start";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 start : statement+ ;
		 statement : 'x' | ifStatement;
		 ifStatement : 'if' 'y' statement ('else' statement)?? {
		 <writeln("$text")>
		 };
		 ID : 'a'..'z'+ ;
		 WS : (' '|'\n') -> channel(HIDDEN);
""";

	}

	public static class IfIfElseNonGreedyBinding2 extends BaseParserTestDescriptor {
		public String input = "if y if y x else x";
		public String output = """
		if y x
		if y if y x else x
""";

		public String errors = null;
		public String startRule = "start";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 start : statement+ ;
		 statement : 'x' | ifStatement;
		 ifStatement : 'if' 'y' statement (|'else' statement) {
		 <writeln("$text")>
		 };
		 ID : 'a'..'z'+ ;
		 WS : (' '|'\n') -> channel(HIDDEN);
""";

	}

	public static class LL1OptionalBlock_1 extends BaseParserTestDescriptor {
		public String input = "";
		public String output = "\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 a : (ID|{}INT)? {
		 <writeln("$text")>
		 };
		 ID : 'a'..'z'+;
		 INT : '0'..'9'+ ;
		 WS : (' '|'\n') -> skip;
""";

	}

	public static class LL1OptionalBlock_2 extends BaseParserTestDescriptor {
		public String input = "a";
		public String output = "a\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 a : (ID|{}INT)? {
		 <writeln("$text")>
		 };
		 ID : 'a'..'z'+;
		 INT : '0'..'9'+ ;
		 WS : (' '|'\n') -> skip;
""";

	}

	public String output = """
	 * This is a regression test for antlr/antlr4#195 "label 'label' type
	 * mismatch with previous definition: TOKEN_LABEL!=RULE_LABEL"
	 * https://github.com/antlr/antlr4/issues/195
""";

		public String errors = null;
		public String startRule = "start";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 start : a* EOF;
		 a
		   : label=subrule {<writeln("$label.text")>} #One
		   | label='y' {<writeln("$label.text")>} #Two
		   ;
		 subrule : 'x';
		 WS : (' '|'\n') -> skip ;
""";

	}

	public static class Labels extends BaseParserTestDescriptor {
		public String input = "abc 34;";
		public String output = null;
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 a : b1=b b2+=b* b3+=';' ;
		 b : id_=ID val+=INT*;
		 ID : 'a'..'z'+ ;
		 INT : '0'..'9'+;
		 WS : (' '|'\n') -> skip ;
""";

	}

	public String grammar = """
	 * This is a regression test for antlr/antlr4#299 "Repeating subtree not
	 * accessible in visitor".
	 * https://github.com/antlr/antlr4/issues/299
""";

	}

	public String grammar = """
	 * This is a regression test for #270 "Fix operator += applied to a set of
	 * tokens".
	 * https://github.com/antlr/antlr4/issues/270
""";

	}

	public String grammar = """
	 * This test ensures that {@link ParserATNSimulator} produces a correct
	 * result when the grammar contains multiple explicit references to
	 * {@code EOF} inside of parser rules.
""";

	}

	public String grammar = """
	 * This test is meant to detect regressions of bug antlr/antlr4#41.
	 * https://github.com/antlr/antlr4/issues/41
""";

	}

	public static class Optional_2 extends BaseParserTestDescriptor {
		public String input = "if x";
		public String output = null;
		public String errors = null;
		public String startRule = "stat";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 stat : ifstat | 'x';
		 ifstat : 'if' stat ('else' stat)?;
		 WS : [ \n\t]+ -> skip ;
""";

	}

	public static class Optional_3 extends BaseParserTestDescriptor {
		public String input = "if x else x";
		public String output = null;
		public String errors = null;
		public String startRule = "stat";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 stat : ifstat | 'x';
		 ifstat : 'if' stat ('else' stat)?;
		 WS : [ \n\t]+ -> skip ;
""";

	}

	public static class Optional_4 extends BaseParserTestDescriptor {
		public String input = "if if x else x";
		public String output = null;
		public String errors = null;
		public String startRule = "stat";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 stat : ifstat | 'x';
		 ifstat : 'if' stat ('else' stat)?;
		 WS : [ \n\t]+ -> skip ;
""";

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

		public String grammar = """
		 grammar T;
		 <ParserPropertyMember()>
		 a : {<ParserPropertyCall({$parser}, "Property()")>}? ID {<writeln("\"valid\"")>}
		   ;
		 ID : 'a'..'z'+ ;
		 WS : (' '|'\n') -> skip ;
""";

	}

	public String grammar = """
	 * This test is meant to test the expected solution to antlr/antlr4#42.
	 * https://github.com/antlr/antlr4/issues/42
""";

	}

	public String grammar = """
	 * This is a regression test for antlr/antlr4#334 "BailErrorStrategy: bails
	 * out on proper input".
	 * https://github.com/antlr/antlr4/issues/334
""";

	}

	public String grammar = """
	 * This is a regression test for antlr/antlr4#561 "Issue with parser
	 * generation in 4.2.2"
	 * https://github.com/antlr/antlr4/issues/561
""";

	}

	public static class ReferenceToATN_2 extends BaseParserTestDescriptor {
		public String input = "a 34 c";
		public String output = "a34c\n";
		public String errors = null;
		public String startRule = "a";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 a : (ID|ATN)* ATN? {<writeln("$text")>} ;
		 ID : 'a'..'z'+ ;
		 ATN : '0'..'9'+;
		 WS : (' '|'\n') -> skip ;
""";
	}

	public String grammar = """
	 * This is a regression test for antlr/antlr4#1545, case 1.
""";
	}

	public String grammar = """
	 * This is a regression test for antlr/antlr4#1545, case 2.
""";
	}

	public String grammar = """
	 * This is a regression test for antlr/antlr4#1545, case 3.
""";
	}

	public String grammar = """
	 * This is a regression test for antlr/antlr4#2301.
""";
	}

	public String grammar = """
	 * This is a regression test for antlr/antlr4#2728
	 * It should generate correct code for grammars with more than 65 tokens.
	 * https://github.com/antlr/antlr4/pull/2728#issuecomment-622940562
""";
	}
}
