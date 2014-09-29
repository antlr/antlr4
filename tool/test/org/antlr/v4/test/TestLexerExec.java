/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.test;

import org.antlr.v4.runtime.misc.Nullable;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestLexerExec extends BaseTest {
    @Test public void testQuoteTranslation() throws Exception {
   		String grammar =
   			"lexer grammar L;\n"+
   			"QUOTE : '\"' ;\n"; // make sure this compiles
   		String found = execLexer("L.g4", grammar, "L", "\"");
   		String expecting =
   			"[@0,0:0='\"',<1>,1:0]\n" +
            "[@1,1:0='<EOF>',<-1>,1:1]\n";
   		assertEquals(expecting, found);
   	}

    @Test public void testRefToRuleDoesNotSetTokenNorEmitAnother() throws Exception {
   		String grammar =
   			"lexer grammar L;\n"+
   			"A : '-' I ;\n" +
   			"I : '0'..'9'+ ;\n"+
   			"WS : (' '|'\\n') -> skip ;";
   		String found = execLexer("L.g4", grammar, "L", "34 -21 3");
   		String expecting =
   			"[@0,0:1='34',<2>,1:0]\n" +
   			"[@1,3:5='-21',<1>,1:3]\n" +
   			"[@2,7:7='3',<2>,1:7]\n" +
   			"[@3,8:7='<EOF>',<-1>,1:8]\n"; // EOF has no length so range is 8:7 not 8:8
   		assertEquals(expecting, found);
   	}

	@Test public void testSlashes() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"Backslash : '\\\\';\n" +
			"Slash : '/';\n" +
			"Vee : '\\\\/';\n" +
			"Wedge : '/\\\\';\n"+
			"WS : [ \\t] -> skip;";
		String found = execLexer("L.g4", grammar, "L", "\\ / \\/ /\\");
		String expecting =
			"[@0,0:0='\\',<1>,1:0]\n" +
			"[@1,2:2='/',<2>,1:2]\n" +
			"[@2,4:5='\\/',<3>,1:4]\n" +
			"[@3,7:8='/\\',<4>,1:7]\n" +
			"[@4,9:8='<EOF>',<-1>,1:9]\n";
		assertEquals(expecting, found);
	}

	/**
	 * This is a regression test for antlr/antlr4#224: "Parentheses without
	 * quantifier in lexer rules have unclear effect".
	 * https://github.com/antlr/antlr4/issues/224
	 */
	@Test public void testParentheses() {
		String grammar =
			"lexer grammar Demo;\n" +
			"\n" +
			"START_BLOCK: '-.-.-';\n" +
			"\n" +
			"ID : (LETTER SEPARATOR) (LETTER SEPARATOR)+;\n" +
			"fragment LETTER: L_A|L_K;\n" +
			"fragment L_A: '.-';\n" +
			"fragment L_K: '-.-';\n" +
			"\n" +
			"SEPARATOR: '!';\n";
		String found = execLexer("Demo.g4", grammar, "Demo", "-.-.-!");
		String expecting =
			"[@0,0:4='-.-.-',<1>,1:0]\n" +
			"[@1,5:5='!',<3>,1:5]\n" +
			"[@2,6:5='<EOF>',<-1>,1:6]\n";
		assertEquals(expecting, found);
	}

	@Test
	public void testNonGreedyTermination() throws Exception {
		String grammar =
			"lexer grammar L;\n"
			+ "STRING : '\"' ('\"\"' | .)*? '\"';";

		String found = execLexer("L.g4", grammar, "L", "\"hi\"\"mom\"");
		assertEquals(
			"[@0,0:3='\"hi\"',<1>,1:0]\n" +
			"[@1,4:8='\"mom\"',<1>,1:4]\n" +
			"[@2,9:8='<EOF>',<-1>,1:9]\n", found);
		assertNull(stderrDuringParse);
	}

	@Test
	public void testNonGreedyTermination2() throws Exception {
		String grammar =
			"lexer grammar L;\n"
			+ "STRING : '\"' ('\"\"' | .)+? '\"';";

		String found = execLexer("L.g4", grammar, "L", "\"\"\"mom\"");
		assertEquals(
			"[@0,0:6='\"\"\"mom\"',<1>,1:0]\n" +
			"[@1,7:6='<EOF>',<-1>,1:7]\n", found);
		assertNull(stderrDuringParse);
	}

	@Test
	public void testGreedyOptional() throws Exception {
		String grammar =
			"lexer grammar L;\n"
			+ "CMT : '//' .*? '\\n' CMT?;\n"
			+ "WS : (' '|'\\t')+;";

		String found = execLexer("L.g4", grammar, "L", "//blah\n//blah\n");
		assertEquals(
			"[@0,0:13='//blah\\n//blah\\n',<1>,1:0]\n" +
			"[@1,14:13='<EOF>',<-1>,3:14]\n", found);
		assertNull(stderrDuringParse);
	}

	@Test
	public void testNonGreedyOptional() throws Exception {
		String grammar =
			"lexer grammar L;\n"
			+ "CMT : '//' .*? '\\n' CMT??;\n"
			+ "WS : (' '|'\\t')+;";

		String found = execLexer("L.g4", grammar, "L", "//blah\n//blah\n");
		assertEquals(
			"[@0,0:6='//blah\\n',<1>,1:0]\n" +
			"[@1,7:13='//blah\\n',<1>,2:0]\n" +
			"[@2,14:13='<EOF>',<-1>,3:7]\n", found);
		assertNull(stderrDuringParse);
	}

	@Test
	public void testGreedyClosure() throws Exception {
		String grammar =
			"lexer grammar L;\n"
			+ "CMT : '//' .*? '\\n' CMT*;\n"
			+ "WS : (' '|'\\t')+;";

		String found = execLexer("L.g4", grammar, "L", "//blah\n//blah\n");
		assertEquals(
			"[@0,0:13='//blah\\n//blah\\n',<1>,1:0]\n" +
			"[@1,14:13='<EOF>',<-1>,3:14]\n", found);
		assertNull(stderrDuringParse);
	}

	@Test
	public void testNonGreedyClosure() throws Exception {
		String grammar =
			"lexer grammar L;\n"
			+ "CMT : '//' .*? '\\n' CMT*?;\n"
			+ "WS : (' '|'\\t')+;";

		String found = execLexer("L.g4", grammar, "L", "//blah\n//blah\n");
		assertEquals(
			"[@0,0:6='//blah\\n',<1>,1:0]\n" +
			"[@1,7:13='//blah\\n',<1>,2:0]\n" +
			"[@2,14:13='<EOF>',<-1>,3:7]\n", found);
		assertNull(stderrDuringParse);
	}

	@Test
	public void testGreedyPositiveClosure() throws Exception {
		String grammar =
			"lexer grammar L;\n"
			+ "CMT : ('//' .*? '\\n')+;\n"
			+ "WS : (' '|'\\t')+;";

		String found = execLexer("L.g4", grammar, "L", "//blah\n//blah\n");
		assertEquals(
			"[@0,0:13='//blah\\n//blah\\n',<1>,1:0]\n" +
			"[@1,14:13='<EOF>',<-1>,3:14]\n", found);
		assertNull(stderrDuringParse);
	}

	@Test
	public void testNonGreedyPositiveClosure() throws Exception {
		String grammar =
			"lexer grammar L;\n"
			+ "CMT : ('//' .*? '\\n')+?;\n"
			+ "WS : (' '|'\\t')+;";

		String found = execLexer("L.g4", grammar, "L", "//blah\n//blah\n");
		assertEquals(
			"[@0,0:6='//blah\\n',<1>,1:0]\n" +
			"[@1,7:13='//blah\\n',<1>,2:0]\n" +
			"[@2,14:13='<EOF>',<-1>,3:7]\n", found);
		assertNull(stderrDuringParse);
	}

	@Test 
	public void testRecursiveLexerRuleRefWithWildcardStar1() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"CMT : '/*' (CMT | .)*? '*/' ;\n" +
			"WS : (' '|'\\n')+ ;\n"
			/*+ "ANY : .;"*/;

		String expecting =
			"[@0,0:8='/* ick */',<1>,1:0]\n" +
			"[@1,9:9='\\n',<2>,1:9]\n" +
			"[@2,10:34='/* /* */\\n/* /*nested*/ */',<1>,2:0]\n" +
			"[@3,35:35='\\n',<2>,3:16]\n" +
			"[@4,36:35='<EOF>',<-1>,4:17]\n";

		// stuff on end of comment matches another rule
		String found = execLexer("L.g4", grammar, "L",
						  "/* ick */\n" +
						  "/* /* */\n" +
						  "/* /*nested*/ */\n");
		assertEquals(expecting, found);
		assertNull(stderrDuringParse);
	}
	
	@Test 
	public void testRecursiveLexerRuleRefWithWildcardStar2() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"CMT : '/*' (CMT | .)*? '*/' ;\n" +
			"WS : (' '|'\\n')+ ;\n"
			/*+ "ANY : .;"*/;

		// stuff on end of comment doesn't match another rule
		String expecting =
			"[@0,0:8='/* ick */',<1>,1:0]\n" +
			"[@1,10:10='\\n',<2>,1:10]\n" +
			"[@2,11:36='/* /* */x\\n/* /*nested*/ */',<1>,2:0]\n" +
			"[@3,38:38='\\n',<2>,3:17]\n" +
			"[@4,39:38='<EOF>',<-1>,4:18]\n";
		String found = execLexer("L.g4", grammar, "L",
						  "/* ick */x\n" +
						  "/* /* */x\n" +
						  "/* /*nested*/ */x\n");
		assertEquals(expecting, found);
		assertEquals(
			"line 1:9 token recognition error at: 'x'\n" +
			"line 3:16 token recognition error at: 'x'\n", stderrDuringParse);
	}

	@Test 
	public void testRecursiveLexerRuleRefWithWildcardPlus1() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"CMT : '/*' (CMT | .)+? '*/' ;\n" +
			"WS : (' '|'\\n')+ ;\n"
			/*+ "ANY : .;"*/;

		String expecting =
			"[@0,0:8='/* ick */',<1>,1:0]\n" +
			"[@1,9:9='\\n',<2>,1:9]\n" +
			"[@2,10:34='/* /* */\\n/* /*nested*/ */',<1>,2:0]\n" +
			"[@3,35:35='\\n',<2>,3:16]\n" +
			"[@4,36:35='<EOF>',<-1>,4:17]\n";

		// stuff on end of comment matches another rule
		String found = execLexer("L.g4", grammar, "L",
						  "/* ick */\n" +
						  "/* /* */\n" +
						  "/* /*nested*/ */\n");
		assertEquals(expecting, found);
		assertNull(stderrDuringParse);
	}
	
	@Test 
	public void testRecursiveLexerRuleRefWithWildcardPlus2() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"CMT : '/*' (CMT | .)+? '*/' ;\n" +
			"WS : (' '|'\\n')+ ;\n"
			/*+ "ANY : .;"*/;

		// stuff on end of comment doesn't match another rule
		String expecting =
			"[@0,0:8='/* ick */',<1>,1:0]\n" +
			"[@1,10:10='\\n',<2>,1:10]\n" +
			"[@2,11:36='/* /* */x\\n/* /*nested*/ */',<1>,2:0]\n" +
			"[@3,38:38='\\n',<2>,3:17]\n" +
			"[@4,39:38='<EOF>',<-1>,4:18]\n";
		String found = execLexer("L.g4", grammar, "L",
						  "/* ick */x\n" +
						  "/* /* */x\n" +
						  "/* /*nested*/ */x\n");
		assertEquals(expecting, found);
		assertEquals(
			"line 1:9 token recognition error at: 'x'\n" +
			"line 3:16 token recognition error at: 'x'\n", stderrDuringParse);
	}

	@Test public void testActionPlacement() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"I : ({System.out.println(\"stuff fail: \" + getText());} 'a' | {System.out.println(\"stuff0: \" + getText());} 'a' {System.out.println(\"stuff1: \" + getText());} 'b' {System.out.println(\"stuff2: \" + getText());}) {System.out.println(getText());} ;\n"+
			"WS : (' '|'\\n') -> skip ;\n" +
			"J : .;\n";
		String found = execLexer("L.g4", grammar, "L", "ab");
		String expecting =
			"stuff0: \n" +
			"stuff1: a\n" +
			"stuff2: ab\n" +
			"ab\n" +
			"[@0,0:1='ab',<1>,1:0]\n" +
			"[@1,2:1='<EOF>',<-1>,1:2]\n";
		assertEquals(expecting, found);
	}

	@Test public void testGreedyConfigs() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"I : ('a' | 'ab') {System.out.println(getText());} ;\n"+
			"WS : (' '|'\\n') -> skip ;\n" +
			"J : .;\n";
		String found = execLexer("L.g4", grammar, "L", "ab");
		String expecting =
			"ab\n" +
			"[@0,0:1='ab',<1>,1:0]\n" +
			"[@1,2:1='<EOF>',<-1>,1:2]\n";
		assertEquals(expecting, found);
	}

	@Test public void testNonGreedyConfigs() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"I : .*? ('a' | 'ab') {System.out.println(getText());} ;\n"+
			"WS : (' '|'\\n') -> skip ;\n" +
			"J : . {System.out.println(getText());};\n";
		String found = execLexer("L.g4", grammar, "L", "ab");
		String expecting =
			"a\n" +
			"b\n" +
			"[@0,0:0='a',<1>,1:0]\n" +
			"[@1,1:1='b',<3>,1:1]\n" +
			"[@2,2:1='<EOF>',<-1>,1:2]\n";
		assertEquals(expecting, found);
	}

	@Test public void testKeywordID() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"KEND : 'end' ;\n" + // has priority
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\\n')+ ;";
		String found = execLexer("L.g4", grammar, "L", "end eend ending a");
		String expecting =
			"[@0,0:2='end',<1>,1:0]\n" +
			"[@1,3:3=' ',<3>,1:3]\n" +
			"[@2,4:7='eend',<2>,1:4]\n" +
			"[@3,8:8=' ',<3>,1:8]\n" +
			"[@4,9:14='ending',<2>,1:9]\n" +
			"[@5,15:15=' ',<3>,1:15]\n" +
			"[@6,16:16='a',<2>,1:16]\n" +
			"[@7,17:16='<EOF>',<-1>,1:17]\n";
		assertEquals(expecting, found);
	}

	@Test public void testHexVsID() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"HexLiteral : '0' ('x'|'X') HexDigit+ ;\n"+
			"DecimalLiteral : ('0' | '1'..'9' '0'..'9'*) ;\n" +
			"FloatingPointLiteral : ('0x' | '0X') HexDigit* ('.' HexDigit*)? ;\n" +
			"DOT : '.' ;\n" +
			"ID : 'a'..'z'+ ;\n" +
			"fragment HexDigit : ('0'..'9'|'a'..'f'|'A'..'F') ;\n" +
			"WS : (' '|'\\n')+ ;";
		String found = execLexer("L.g4", grammar, "L", "x 0 1 a.b a.l");
		String expecting =
			"[@0,0:0='x',<5>,1:0]\n" +
			"[@1,1:1=' ',<6>,1:1]\n" +
			"[@2,2:2='0',<2>,1:2]\n" +
			"[@3,3:3=' ',<6>,1:3]\n" +
			"[@4,4:4='1',<2>,1:4]\n" +
			"[@5,5:5=' ',<6>,1:5]\n" +
			"[@6,6:6='a',<5>,1:6]\n" +
			"[@7,7:7='.',<4>,1:7]\n" +
			"[@8,8:8='b',<5>,1:8]\n" +
			"[@9,9:9=' ',<6>,1:9]\n" +
			"[@10,10:10='a',<5>,1:10]\n" +
			"[@11,11:11='.',<4>,1:11]\n" +
			"[@12,12:12='l',<5>,1:12]\n" +
			"[@13,13:12='<EOF>',<-1>,1:13]\n";
		assertEquals(expecting, found);
	}

	// must get DONE EOF
	@Test public void testEOFByItself() throws Exception {
		String grammar =
			"lexer grammar L;\n" +
			"DONE : EOF ;\n" +
			"A : 'a';\n";
		String found = execLexer("L.g4", grammar, "L", "");
		String expecting =
			"[@0,0:-1='<EOF>',<1>,1:0]\n" +
			"[@1,0:-1='<EOF>',<-1>,1:0]\n";
		assertEquals(expecting, found);
	}

	@Test public void testEOFSuffixInFirstRule() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"A : 'a' EOF ;\n"+
			"B : 'a';\n"+
			"C : 'c';\n";
		String found = execLexer("L.g4", grammar, "L", "");
		String expecting =
			"[@0,0:-1='<EOF>',<-1>,1:0]\n";
		assertEquals(expecting, found);

		found = execLexer("L.g4", grammar, "L", "a");
		expecting =
			"[@0,0:0='a',<1>,1:0]\n" +
			"[@1,1:0='<EOF>',<-1>,1:1]\n";
		assertEquals(expecting, found);
	}

	@Test public void testCharSet() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"I : '0'..'9'+ {System.out.println(\"I\");} ;\n"+
			"WS : [ \\n\\u000D] -> skip ;";
		String found = execLexer("L.g4", grammar, "L", "34\r\n 34");
		String expecting =
			"I\n" +
			"I\n" +
			"[@0,0:1='34',<1>,1:0]\n" +
			"[@1,5:6='34',<1>,2:1]\n" +
			"[@2,7:6='<EOF>',<-1>,2:3]\n";
		assertEquals(expecting, found);
	}

	@Test public void testCharSetPlus() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"I : '0'..'9'+ {System.out.println(\"I\");} ;\n"+
			"WS : [ \\n\\u000D]+ -> skip ;";
		String found = execLexer("L.g4", grammar, "L", "34\r\n 34");
		String expecting =
			"I\n" +
			"I\n" +
			"[@0,0:1='34',<1>,1:0]\n" +
			"[@1,5:6='34',<1>,2:1]\n" +
			"[@2,7:6='<EOF>',<-1>,2:3]\n";
		assertEquals(expecting, found);
	}

	@Test public void testCharSetNot() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"I : ~[ab \\n] ~[ \\ncd]* {System.out.println(\"I\");} ;\n"+
			"WS : [ \\n\\u000D]+ -> skip ;";
		String found = execLexer("L.g4", grammar, "L", "xaf");
		String expecting =
			"I\n" +
			"[@0,0:2='xaf',<1>,1:0]\n" +
			"[@1,3:2='<EOF>',<-1>,1:3]\n";
		assertEquals(expecting, found);
	}

	@Test public void testCharSetInSet() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"I : (~[ab \\n]|'a') {System.out.println(\"I\");} ;\n"+
			"WS : [ \\n\\u000D]+ -> skip ;";
		String found = execLexer("L.g4", grammar, "L", "a x");
		String expecting =
			"I\n" +
			"I\n" +
			"[@0,0:0='a',<1>,1:0]\n" +
			"[@1,2:2='x',<1>,1:2]\n" +
			"[@2,3:2='<EOF>',<-1>,1:3]\n";
		assertEquals(expecting, found);
	}

	@Test public void testCharSetRange() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"I : [0-9]+ {System.out.println(\"I\");} ;\n"+
			"ID : [a-zA-Z] [a-zA-Z0-9]* {System.out.println(\"ID\");} ;\n"+
			"WS : [ \\n\\u0009\\r]+ -> skip ;";
		String found = execLexer("L.g4", grammar, "L", "34\r 34 a2 abc \n   ");
		String expecting =
			"I\n" +
			"I\n" +
			"ID\n" +
			"ID\n" +
			"[@0,0:1='34',<1>,1:0]\n" +
			"[@1,4:5='34',<1>,1:4]\n" +
			"[@2,7:8='a2',<2>,1:7]\n" +
			"[@3,10:12='abc',<2>,1:10]\n" +
			"[@4,18:17='<EOF>',<-1>,2:3]\n";
		assertEquals(expecting, found);
	}

	@Test public void testCharSetWithMissingEndRange() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"I : [0-]+ {System.out.println(\"I\");} ;\n"+
			"WS : [ \\n\\u000D]+ -> skip ;";
		String found = execLexer("L.g4", grammar, "L", "00\r\n");
		String expecting =
			"I\n" +
			"[@0,0:1='00',<1>,1:0]\n" +
			"[@1,4:3='<EOF>',<-1>,2:0]\n";
		assertEquals(expecting, found);
	}

	@Test public void testCharSetWithMissingEscapeChar() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"I : [0-9]+ {System.out.println(\"I\");} ;\n"+
			"WS : [ \\u]+ -> skip ;";
		String found = execLexer("L.g4", grammar, "L", "34 ");
		String expecting =
			"I\n" +
			"[@0,0:1='34',<1>,1:0]\n" +
			"[@1,3:2='<EOF>',<-1>,1:3]\n";
		assertEquals(expecting, found);
	}

	@Test public void testCharSetWithEscapedChar() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"DASHBRACK : [\\-\\]]+ {System.out.println(\"DASHBRACK\");} ;\n"+
			"WS : [ \\u]+ -> skip ;";
		String found = execLexer("L.g4", grammar, "L", "- ] ");
		String expecting =
			"DASHBRACK\n" +
			"DASHBRACK\n" +
			"[@0,0:0='-',<1>,1:0]\n" +
			"[@1,2:2=']',<1>,1:2]\n" +
			"[@2,4:3='<EOF>',<-1>,1:4]\n";
		assertEquals(expecting, found);
	}

	@Test public void testCharSetWithReversedRange() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"A : [z-a9]+ {System.out.println(\"A\");} ;\n"+
			"WS : [ \\u]+ -> skip ;";
		String found = execLexer("L.g4", grammar, "L", "9");
		String expecting =
			"A\n" +
			"[@0,0:0='9',<1>,1:0]\n" +
			"[@1,1:0='<EOF>',<-1>,1:1]\n";
		assertEquals(expecting, found);
	}

	@Test public void testCharSetWithQuote() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"A : [\"a-z]+ {System.out.println(\"A\");} ;\n"+
			"WS : [ \\n\\t]+ -> skip ;";
		String found = execLexer("L.g4", grammar, "L", "b\"a");
		String expecting =
			"A\n" +
			"[@0,0:2='b\"a',<1>,1:0]\n" +
			"[@1,3:2='<EOF>',<-1>,1:3]\n";
		assertEquals(expecting, found);
	}

	@Test public void testCharSetWithQuote2() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"A : [\"\\\\ab]+ {System.out.println(\"A\");} ;\n"+
			"WS : [ \\n\\t]+ -> skip ;";
		String found = execLexer("L.g4", grammar, "L", "b\"\\a");
		String expecting =
			"A\n" +
			"[@0,0:3='b\"\\a',<1>,1:0]\n" +
			"[@1,4:3='<EOF>',<-1>,1:4]\n";
		assertEquals(expecting, found);
	}

	@Test
	public void testPositionAdjustingLexer() throws Exception {
		String grammar = load("PositionAdjustingLexer.g4", null);
		String input =
			"tokens\n" +
			"tokens {\n" +
			"notLabel\n" +
			"label1 =\n" +
			"label2 +=\n" +
			"notLabel\n";
		String found = execLexer("PositionAdjustingLexer.g4", grammar, "PositionAdjustingLexer", input);

		final int TOKENS = 4;
		final int LABEL = 5;
		final int IDENTIFIER = 6;
		String expecting =
			"[@0,0:5='tokens',<" + IDENTIFIER + ">,1:0]\n" +
			"[@1,7:12='tokens',<" + TOKENS + ">,2:0]\n" +
			"[@2,14:14='{',<3>,2:7]\n" +
			"[@3,16:23='notLabel',<" + IDENTIFIER + ">,3:0]\n" +
			"[@4,25:30='label1',<" + LABEL + ">,4:0]\n" +
			"[@5,32:32='=',<1>,4:7]\n" +
			"[@6,34:39='label2',<" + LABEL + ">,5:0]\n" +
			"[@7,41:42='+=',<2>,5:7]\n" +
			"[@8,44:51='notLabel',<" + IDENTIFIER + ">,6:0]\n" +
			"[@9,53:52='<EOF>',<-1>,7:0]\n";

		assertEquals(expecting, found);
	}

	/**
	 * This is a regression test for antlr/antlr4#76 "Serialized ATN strings
	 * should be split when longer than 2^16 bytes (class file limitation)"
	 * https://github.com/antlr/antlr4/issues/76
	 */
	@Test
	public void testLargeLexer() throws Exception {
		StringBuilder grammar = new StringBuilder();
		grammar.append("lexer grammar L;\n");
		grammar.append("WS : [ \\t\\r\\n]+ -> skip;\n");
		for (int i = 0; i < 4000; i++) {
			grammar.append("KW").append(i).append(" : 'KW' '").append(i).append("';\n");
		}

		String input = "KW400";
		String found = execLexer("L.g4", grammar.toString(), "L", input);
		String expecting =
			"[@0,0:4='KW400',<402>,1:0]\n" +
			"[@1,5:4='<EOF>',<-1>,1:5]\n";
		assertEquals(expecting, found);
	}

	/**
	 * This is a regression test for antlr/antlr4#687 "Empty zero-length tokens
	 * cannot have lexer commands" and antlr/antlr4#688 "Lexer cannot match
	 * zero-length tokens"
	 * https://github.com/antlr/antlr4/issues/687
	 * https://github.com/antlr/antlr4/issues/688
	 */
	@Test public void testZeroLengthToken() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"\n" +
			"BeginString\n" +
			"	:	'\\'' -> more, pushMode(StringMode)\n" +
			"	;\n" +
			"\n" +
			"mode StringMode;\n" +
			"\n" +
			"	StringMode_X : 'x' -> more;\n" +
			"	StringMode_Done : -> more, mode(EndStringMode);\n" +
			"\n" +
			"mode EndStringMode;	\n" +
			"\n" +
			"	EndString : '\\'' -> popMode;\n";
		String found = execLexer("L.g4", grammar, "L", "'xxx'");
		String expecting =
			"[@0,0:4=''xxx'',<1>,1:0]\n" +
			"[@1,5:4='<EOF>',<-1>,1:5]\n";
		assertEquals(expecting, found);
	}
}
