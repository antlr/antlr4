/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.runtime.descriptors;

import org.antlr.v4.test.runtime.BaseParserTestDescriptor;

public class ParserErrorsDescriptors {
	public static class ConjuringUpToken extends BaseParserTestDescriptor {
		public String input = "ac";
		public String output = "conjured=[@-1,-1:-1='<missing 'b'>',<2>,1:1]\n";
		public String errors = "line 1:1 missing 'b' at 'c'\n";
		public String startRule = "a";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 a : 'a' x='b' {<Append("\\"conjured=\\"","$x"):writeln()>} 'c' ;
""";

	}

	public static class ConjuringUpTokenFromSet extends BaseParserTestDescriptor {
		public String input = "ad";
		public String output = "conjured=[@-1,-1:-1='<missing 'b'>',<2>,1:1]\n";
		public String errors = "line 1:1 missing {'b', 'c'} at 'd'\n";
		public String startRule = "a";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 a : 'a' x=('b'|'c') {<Append("\\"conjured=\\"","$x"):writeln()>} 'd' ;
""";

	}

    // 	 * Regression test for "Getter for context is not a list when it should be".
    // 	 * https://github.com/antlr/antlr4/issues/19
	public static class ContextListGetters extends BaseParserTestDescriptor {
		public String input = "abab";
		public String output = "abab\n";
		public String errors = null;
		public String startRule = "s";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 @parser::members{
		 <DeclareContextListGettersFunction()>
		 }
		 s : (a | b)+;
		 a : 'a' {<write("\\"a\\"")>};
		 b : 'b' {<write("\\"b\\"")>};
""";

	}

	public static abstract class DuplicatedLeftRecursiveCall extends BaseParserTestDescriptor {
		public String output = null;
		public String errors = null;
		public String startRule = "start";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 start : expr EOF;
		 expr : 'x'
		      | expr expr
		      ;
""";

	}

	public static class DuplicatedLeftRecursiveCall_1 extends DuplicatedLeftRecursiveCall {
		public String input = "x";
	}

	public static class DuplicatedLeftRecursiveCall_2 extends DuplicatedLeftRecursiveCall {
		public String input = "xx";
	}

	public static class DuplicatedLeftRecursiveCall_3 extends DuplicatedLeftRecursiveCall {
		public String input = "xxx";
	}

	public static class DuplicatedLeftRecursiveCall_4 extends DuplicatedLeftRecursiveCall {
		public String input = "xxxx";
	}

    // 	 * This is a regression test for #45 "NullPointerException in ATNConfig.hashCode".
    // 	 * https://github.com/antlr/antlr4/issues/45
    // 	 * <p/>
    // 	 * The original cause of this issue was an error in the tool's ATN state optimization,
    // 	 * which is now detected early in {@link ATNSerializer} by ensuring that all
    // 	 * serialized transitions point to states which were not removed.
	public static class InvalidATNStateRemoval extends BaseParserTestDescriptor {
		public String input = "x:x";
		public String output = null;
		public String errors = null;
		public String startRule = "start";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 start : ID ':' expr;
		 expr : primary expr? {<Pass()>} | expr '->' ID;
		 primary : ID;
		 ID : [a-z]+;
""";

	}

    // 	 * This is a regression test for #6 "NullPointerException in getMissingSymbol".
    // 	 * https://github.com/antlr/antlr4/issues/6
	public static class InvalidEmptyInput extends BaseParserTestDescriptor {
		public String input = "";
		public String output = null;
		public String errors = "line 1:0 mismatched input '<EOF>' expecting ID\n";
		public String startRule = "start";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 start : ID+;
		 ID : [a-z]+;
""";

	}

	public static class LL1ErrorInfo extends BaseParserTestDescriptor {
		public String input = "dog and software";
		public String output = "{'hardware', 'software'}\n";
		public String errors = null;
		public String startRule = "start";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 start : animal (AND acClass)? service EOF;
		 animal : (DOG | CAT );
		 service : (HARDWARE | SOFTWARE) ;
		 AND : 'and';
		 DOG : 'dog';
		 CAT : 'cat';
		 HARDWARE: 'hardware';
		 SOFTWARE: 'software';
		 WS : ' ' -> skip ;
		 acClass
		 @init
		 {<GetExpectedTokenNames():writeln()>}
		   : ;
""";

	}

	public static class LL2 extends BaseParserTestDescriptor {
		public String input = "ae";
		public String output = null;
		public String errors = "line 1:1 no viable alternative at input 'ae'\n";
		public String startRule = "a";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 a : 'a' 'b'
		   | 'a' 'c'
		 ;
		 q : 'e' ;
""";

	}

	public static class LL3 extends BaseParserTestDescriptor {
		public String input = "abe";
		public String output = null;
		public String errors = "line 1:2 no viable alternative at input 'abe'\n";
		public String startRule = "a";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 a : 'a' 'b'* 'c'
		   | 'a' 'b' 'd'
		 ;
		 q : 'e' ;
""";

	}

	public static class LLStar extends BaseParserTestDescriptor {
		public String input = "aaae";
		public String output = null;
		public String errors = "line 1:3 no viable alternative at input 'aaae'\n";
		public String startRule = "a";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 a : 'a'+ 'b'
		   | 'a'+ 'c'
		 ;
		 q : 'e' ;
""";

	}

	public static class MultiTokenDeletionBeforeLoop extends BaseParserTestDescriptor {
		public String input = "aacabc";
		public String output = null;
		public String errors = "line 1:1 extraneous input 'a' expecting {'b', 'c'}\n";
		public String startRule = "a";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 a : 'a' 'b'* 'c';
""";

	}

	public static class MultiTokenDeletionBeforeLoop2 extends BaseParserTestDescriptor {
		public String input = "aacabc";
		public String output = null;
		public String errors = "line 1:1 extraneous input 'a' expecting {'b', 'z', 'c'}\n";
		public String startRule = "a";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 a : 'a' ('b'|'z'{<Pass()>})* 'c';
""";

	}

	public static class MultiTokenDeletionDuringLoop extends BaseParserTestDescriptor {
		public String input = "abaaababc";
		public String output = null;
		public String errors = """
		line 1:2 extraneous input 'a' expecting {'b', 'c'}
		line 1:6 extraneous input 'a' expecting {'b', 'c'}
""";

		public String startRule = "a";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 a : 'a' 'b'* 'c' ;
""";

	}

	public static class MultiTokenDeletionDuringLoop2 extends BaseParserTestDescriptor {
		public String input = "abaaababc";
		public String output = null;
		public String errors = """
		line 1:2 extraneous input 'a' expecting {'b', 'z', 'c'}
		line 1:6 extraneous input 'a' expecting {'b', 'z', 'c'}
""";

		public String startRule = "a";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 a : 'a' ('b'|'z'{<Pass()>})* 'c' ;
""";

	}

	public static class NoViableAltAvoidance extends BaseParserTestDescriptor {
		public String input = "a.";
		public String output = null;
		public String errors = "line 1:1 mismatched input '.' expecting '!'\n";
		public String startRule = "s";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 s : e '!' ;
		 e : 'a' 'b'
		   | 'a'
		   ;
		 DOT : '.' ;
		 WS : [ \\t\\r\\n]+ -> skip;
""";

	}

	public static class SingleSetInsertion extends BaseParserTestDescriptor {
		public String input = "ad";
		public String output = null;
		public String errors = "line 1:1 missing {'b', 'c'} at 'd'\n";
		public String startRule = "a";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 a : 'a' ('b'|'c') 'd' ;
""";

	}

	public static class SingleSetInsertionConsumption extends BaseParserTestDescriptor {
		public String input = "ad";
		public String output = "[@0,0:0='a',<3>,1:0]\n";
		public String errors = "line 1:1 missing {'b', 'c'} at 'd'\n";
		public String startRule = "a";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 myset: ('b'|'c') ;
		 a: 'a' myset 'd' {<writeln(Append("\\"\\"","$myset.stop"))>} ; <! bit complicated because of the JavaScript target !>
""";

	}

	public static class SingleTokenDeletion extends BaseParserTestDescriptor {
		public String input = "aab";
		public String output = null;
		public String errors = "line 1:1 extraneous input 'a' expecting 'b'\n";
		public String startRule = "a";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 a : 'a' 'b' ;
""";

	}

	public static class SingleTokenDeletionBeforeAlt extends BaseParserTestDescriptor {
		public String input = "ac";
		public String output = null;
		public String errors = "line 1:0 extraneous input 'a' expecting {'b', 'c'}\n";
		public String startRule = "a";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 a : ('b' | 'c')
		 ;
		 q : 'a'
		 ;
""";

	}

	public static class SingleTokenDeletionBeforeLoop extends BaseParserTestDescriptor {
		public String input = "aabc";
		public String output = null;
		public String errors = """
		line 1:1 extraneous input 'a' expecting {<EOF>, 'b'}
		line 1:3 token recognition error at: 'c'
""";

		public String startRule = "a";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 a : 'a' 'b'* EOF ;
""";

	}

	public static class SingleTokenDeletionBeforeLoop2 extends BaseParserTestDescriptor {
		public String input = "aabc";
		public String output = null;
		public String errors = """
		line 1:1 extraneous input 'a' expecting {<EOF>, 'b', 'z'}
		line 1:3 token recognition error at: 'c'
""";

		public String startRule = "a";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 a : 'a' ('b'|'z'{<Pass()>})* EOF ;
""";

	}

	public static class SingleTokenDeletionBeforePredict extends BaseParserTestDescriptor {
		public String input = "caaab";
		public String output = null;
		public String errors = "line 1:0 extraneous input 'c' expecting 'a'\n";
		public String startRule = "a";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 a : 'a'+ 'b'
		   | 'a'+ 'c'
		 ;
		 q : 'e' ;
""";

	}

	public static class SingleTokenDeletionConsumption extends BaseParserTestDescriptor {
		public String input = "aabd";
		public String output = "[@2,2:2='b',<1>,1:2]\n";
		public String errors = "line 1:1 extraneous input 'a' expecting {'b', 'c'}\n";
		public String startRule = "a";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 myset: ('b'|'c') ;
		 a: 'a' myset 'd' {<writeln(Append("\\"\\"","$myset.stop"))>} ; <! bit complicated because of the JavaScript target !>
""";

	}

	public static class SingleTokenDeletionDuringLoop extends BaseParserTestDescriptor {
		public String input = "ababbc";
		public String output = null;
		public String errors = "line 1:2 extraneous input 'a' expecting {'b', 'c'}\n";
		public String startRule = "a";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 a : 'a' 'b'* 'c' ;
""";

	}

	public static class SingleTokenDeletionDuringLoop2 extends BaseParserTestDescriptor {
		public String input = "ababbc";
		public String output = null;
		public String errors = "line 1:2 extraneous input 'a' expecting {'b', 'z', 'c'}\n";
		public String startRule = "a";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 a : 'a' ('b'|'z'{<Pass()>})* 'c' ;
""";

	}

	public static class SingleTokenDeletionExpectingSet extends BaseParserTestDescriptor {
		public String input = "aab";
		public String output = null;
		public String errors = "line 1:1 extraneous input 'a' expecting {'b', 'c'}\n";
		public String startRule = "a";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 a : 'a' ('b'|'c') ;
""";

	}

	public static class SingleTokenInsertion extends BaseParserTestDescriptor {
		public String input = "ac";
		public String output = null;
		public String errors = "line 1:1 missing 'b' at 'c'\n";
		public String startRule = "a";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 a : 'a' 'b' 'c' ;
""";

	}

	public static class TokenMismatch extends BaseParserTestDescriptor {
		public String input = "aa";
		public String output = null;
		public String errors = "line 1:1 mismatched input 'a' expecting 'b'\n";
		public String startRule = "a";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;
		 a : 'a' 'b' ;
""";

	}

	public static class TokenMismatch2 extends BaseParserTestDescriptor {
		public String input = "( ~FORCE_ERROR~ ";
		public String output = null;
		public String errors = "line 1:2 mismatched input '~FORCE_ERROR~' expecting {')', ID}\n";
		public String startRule = "stat";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;

		 stat:   ( '(' expr? ')' )? EOF ;
		 expr:   ID '=' STR ;

		 ERR :   '~FORCE_ERROR~' ;
		 ID  :   [a-zA-Z]+ ;
		 STR :   '"' ~["]* '"' ;
		 WS  :   [ \\t\\r\\n]+ -> skip ;
""";

	}

	public static class TokenMismatch3 extends BaseParserTestDescriptor {
		public String input = "";
		public String output = null;
		public String errors = "line 1:0 mismatched input '<EOF>' expecting {'(', BOOLEAN_LITERAL, ID, '$'}\n";
		public String startRule = "expression";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;

		 expression
		 :   value
		 |   expression op=AND expression
		 |   expression op=OR expression
		 ;
		 value
		 :   BOOLEAN_LITERAL
		 |   ID
		 |   ID1
		 |   '(' expression ')'
		 ;

		 AND : '&&';
		 OR  : '||';

		 BOOLEAN_LITERAL : 'true' | 'false';

		 ID  : [a-z]+;
		 ID1 : '$';

		 WS  : [ \\t\\r\\n]+ -> skip ;
""";

	}

	public static class ExtraneousInput extends BaseParserTestDescriptor {
		public String input = "baa";
		public String output = null;
		public String errors = "line 1:0 mismatched input 'b' expecting {<EOF>, 'a'}\n";
		public String startRule = "file";
		public String grammarName = "T";

		public String grammar = """
		 grammar T;

		 member : 'a';
		 body : member*;
		 file : body EOF;
		 B : 'b';
""";

		@Override
		public boolean ignore(String targetName) {
			return !"Java".equals(targetName) && !"Swift".equals(targetName) && !"Dart".equals(targetName);
		}
	}
}
