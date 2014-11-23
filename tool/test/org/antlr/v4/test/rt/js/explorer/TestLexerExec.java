package org.antlr.v4.test.rt.js.explorer;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestLexerExec extends BaseTest {

	@Test
	public void testQuoteTranslation() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "QUOTE : '\"' ; // make sure this compiles\r";
		String found = execLexer("L.g4", grammar, "L", "\"", false);
		assertEquals("[@0,0:0='\"',<1>,1:0]\n" + 
	              "[@1,1:0='<EOF>',<-1>,1:1]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testRefToRuleDoesNotSetTokenNorEmitAnother() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "A : '-' I ;\r\n" +
	                  "I : '0'..'9'+ ;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execLexer("L.g4", grammar, "L", "34 -21 3", false);
		assertEquals("[@0,0:1='34',<2>,1:0]\n" + 
	              "[@1,3:5='-21',<1>,1:3]\n" + 
	              "[@2,7:7='3',<2>,1:7]\n" + 
	              "[@3,8:7='<EOF>',<-1>,1:8]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testSlashes() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "Backslash : '\\\\';\r\n" +
	                  "Slash : '/';\r\n" +
	                  "Vee : '\\\\/';\r\n" +
	                  "Wedge : '/\\\\';\r\n" +
	                  "WS : [ \\t] -> skip;\r";
		String found = execLexer("L.g4", grammar, "L", "\\ / \\/ /\\", false);
		assertEquals("[@0,0:0='\\',<1>,1:0]\n" + 
	              "[@1,2:2='/',<2>,1:2]\n" + 
	              "[@2,4:5='\\/',<3>,1:4]\n" + 
	              "[@3,7:8='/\\',<4>,1:7]\n" + 
	              "[@4,9:8='<EOF>',<-1>,1:9]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testParentheses() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "START_BLOCK: '-.-.-';\r\n" +
	                  "ID : (LETTER SEPARATOR) (LETTER SEPARATOR)+;\r\n" +
	                  "fragment LETTER: L_A|L_K;\r\n" +
	                  "fragment L_A: '.-';\r\n" +
	                  "fragment L_K: '-.-';\r\n" +
	                  "SEPARATOR: '!';\r";
		String found = execLexer("L.g4", grammar, "L", "-.-.-!", false);
		assertEquals("[@0,0:4='-.-.-',<1>,1:0]\n" + 
	              "[@1,5:5='!',<3>,1:5]\n" + 
	              "[@2,6:5='<EOF>',<-1>,1:6]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testNonGreedyTermination1() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "STRING : '\"' ('\"\"' | .)*? '\"';\r";
		String found = execLexer("L.g4", grammar, "L", "\"hi\"\"mom\"", false);
		assertEquals("[@0,0:3='\"hi\"',<1>,1:0]\n" + 
	              "[@1,4:8='\"mom\"',<1>,1:4]\n" + 
	              "[@2,9:8='<EOF>',<-1>,1:9]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testNonGreedyTermination2() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "STRING : '\"' ('\"\"' | .)+? '\"';\r";
		String found = execLexer("L.g4", grammar, "L", "\"\"\"mom\"", false);
		assertEquals("[@0,0:6='\"\"\"mom\"',<1>,1:0]\n" + 
	              "[@1,7:6='<EOF>',<-1>,1:7]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testGreedyOptional() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "CMT : '//' .*? '\\n' CMT?;\r\n" +
	                  "WS : (' '|'\\t')+;\r";
		String found = execLexer("L.g4", grammar, "L", "//blah\n//blah\n", false);
		assertEquals("[@0,0:13='//blah\\n//blah\\n',<1>,1:0]\n" + 
	              "[@1,14:13='<EOF>',<-1>,3:0]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testNonGreedyOptional() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "CMT : '//' .*? '\\n' CMT??;\r\n" +
	                  "WS : (' '|'\\t')+;\r";
		String found = execLexer("L.g4", grammar, "L", "//blah\n//blah\n", false);
		assertEquals("[@0,0:6='//blah\\n',<1>,1:0]\n" + 
	              "[@1,7:13='//blah\\n',<1>,2:0]\n" + 
	              "[@2,14:13='<EOF>',<-1>,3:0]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testGreedyClosure() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "CMT : '//' .*? '\\n' CMT*;\r\n" +
	                  "WS : (' '|'\\t')+;\r";
		String found = execLexer("L.g4", grammar, "L", "//blah\n//blah\n", false);
		assertEquals("[@0,0:13='//blah\\n//blah\\n',<1>,1:0]\n" + 
	              "[@1,14:13='<EOF>',<-1>,3:0]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testNonGreedyClosure() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "CMT : '//' .*? '\\n' CMT*?;\r\n" +
	                  "WS : (' '|'\\t')+;\r";
		String found = execLexer("L.g4", grammar, "L", "//blah\n//blah\n", false);
		assertEquals("[@0,0:6='//blah\\n',<1>,1:0]\n" + 
	              "[@1,7:13='//blah\\n',<1>,2:0]\n" + 
	              "[@2,14:13='<EOF>',<-1>,3:0]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testGreedyPositiveClosure() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "CMT : ('//' .*? '\\n')+;\r\n" +
	                  "WS : (' '|'\\t')+;\r";
		String found = execLexer("L.g4", grammar, "L", "//blah\n//blah\n", false);
		assertEquals("[@0,0:13='//blah\\n//blah\\n',<1>,1:0]\n" + 
	              "[@1,14:13='<EOF>',<-1>,3:0]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testNonGreedyPositiveClosure() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "CMT : ('//' .*? '\\n')+?;\r\n" +
	                  "WS : (' '|'\\t')+;\r";
		String found = execLexer("L.g4", grammar, "L", "//blah\n//blah\n", false);
		assertEquals("[@0,0:6='//blah\\n',<1>,1:0]\n" + 
	              "[@1,7:13='//blah\\n',<1>,2:0]\n" + 
	              "[@2,14:13='<EOF>',<-1>,3:0]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testRecursiveLexerRuleRefWithWildcardStar_1() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "CMT : '/*' (CMT | .)*? '*/' ;\r\n" +
	                  "WS : (' '|'\\n')+;\r";
		String found = execLexer("L.g4", grammar, "L", "/* ick */\n/* /* */\n/* /*nested*/ */\n", false);
		assertEquals("[@0,0:8='/* ick */',<1>,1:0]\n" + 
	              "[@1,9:9='\\n',<2>,1:9]\n" + 
	              "[@2,10:34='/* /* */\\n/* /*nested*/ */',<1>,2:0]\n" + 
	              "[@3,35:35='\\n',<2>,3:16]\n" + 
	              "[@4,36:35='<EOF>',<-1>,4:0]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testRecursiveLexerRuleRefWithWildcardStar_2() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "CMT : '/*' (CMT | .)*? '*/' ;\r\n" +
	                  "WS : (' '|'\\n')+;\r";
		String found = execLexer("L.g4", grammar, "L", "/* ick */x\n/* /* */x\n/* /*nested*/ */x\n", false);
		assertEquals("[@0,0:8='/* ick */',<1>,1:0]\n" + 
	              "[@1,10:10='\\n',<2>,1:10]\n" + 
	              "[@2,11:36='/* /* */x\\n/* /*nested*/ */',<1>,2:0]\n" + 
	              "[@3,38:38='\\n',<2>,3:17]\n" + 
	              "[@4,39:38='<EOF>',<-1>,4:0]\n", found);
		assertEquals("line 1:9 token recognition error at: 'x'\nline 3:16 token recognition error at: 'x'\n", this.stderrDuringParse);
	}

	@Test
	public void testRecursiveLexerRuleRefWithWildcardPlus_1() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "CMT : '/*' (CMT | .)+? '*/' ;\r\n" +
	                  "WS : (' '|'\\n')+;\r";
		String found = execLexer("L.g4", grammar, "L", "/* ick */\n/* /* */\n/* /*nested*/ */\n", false);
		assertEquals("[@0,0:8='/* ick */',<1>,1:0]\n" + 
	              "[@1,9:9='\\n',<2>,1:9]\n" + 
	              "[@2,10:34='/* /* */\\n/* /*nested*/ */',<1>,2:0]\n" + 
	              "[@3,35:35='\\n',<2>,3:16]\n" + 
	              "[@4,36:35='<EOF>',<-1>,4:0]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testRecursiveLexerRuleRefWithWildcardPlus_2() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "CMT : '/*' (CMT | .)+? '*/' ;\r\n" +
	                  "WS : (' '|'\\n')+;\r";
		String found = execLexer("L.g4", grammar, "L", "/* ick */x\n/* /* */x\n/* /*nested*/ */x\n", false);
		assertEquals("[@0,0:8='/* ick */',<1>,1:0]\n" + 
	              "[@1,10:10='\\n',<2>,1:10]\n" + 
	              "[@2,11:36='/* /* */x\\n/* /*nested*/ */',<1>,2:0]\n" + 
	              "[@3,38:38='\\n',<2>,3:17]\n" + 
	              "[@4,39:38='<EOF>',<-1>,4:0]\n", found);
		assertEquals("line 1:9 token recognition error at: 'x'\nline 3:16 token recognition error at: 'x'\n", this.stderrDuringParse);
	}

	@Test
	public void testActionPlacement() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "I : ({document.getElementById('output').value += \"stuff fail: \" + this.text + '\\n';} 'a'\r\n" +
	                  "| {document.getElementById('output').value += \"stuff0: \" + this.text + '\\n';}\r\n" +
	                  "		'a' {document.getElementById('output').value += \"stuff1: \" + this.text + '\\n';}\r\n" +
	                  "		'b' {document.getElementById('output').value += \"stuff2: \" + this.text + '\\n';})\r\n" +
	                  "		{document.getElementById('output').value += this.text + '\\n';} ;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r\n" +
	                  "J : .;\r";
		String found = execLexer("L.g4", grammar, "L", "ab", false);
		assertEquals("stuff0: \n" + 
	              "stuff1: a\n" + 
	              "stuff2: ab\n" + 
	              "ab\n" + 
	              "[@0,0:1='ab',<1>,1:0]\n" + 
	              "[@1,2:1='<EOF>',<-1>,1:2]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testGreedyConfigs() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "I : ('a' | 'ab') {document.getElementById('output').value += this.text + '\\n';} ;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r\n" +
	                  "J : .;\r";
		String found = execLexer("L.g4", grammar, "L", "ab", false);
		assertEquals("ab\n" + 
	              "[@0,0:1='ab',<1>,1:0]\n" + 
	              "[@1,2:1='<EOF>',<-1>,1:2]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testNonGreedyConfigs() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "I : .*? ('a' | 'ab') {document.getElementById('output').value += this.text + '\\n';} ;\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r\n" +
	                  "J : . {document.getElementById('output').value += this.text + '\\n';};\r";
		String found = execLexer("L.g4", grammar, "L", "ab", false);
		assertEquals("a\n" + 
	              "b\n" + 
	              "[@0,0:0='a',<1>,1:0]\n" + 
	              "[@1,1:1='b',<3>,1:1]\n" + 
	              "[@2,2:1='<EOF>',<-1>,1:2]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testKeywordID() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "KEND : 'end' ; // has priority\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "WS : (' '|'\\n')+;\r";
		String found = execLexer("L.g4", grammar, "L", "end eend ending a", false);
		assertEquals("[@0,0:2='end',<1>,1:0]\n" + 
	              "[@1,3:3=' ',<3>,1:3]\n" + 
	              "[@2,4:7='eend',<2>,1:4]\n" + 
	              "[@3,8:8=' ',<3>,1:8]\n" + 
	              "[@4,9:14='ending',<2>,1:9]\n" + 
	              "[@5,15:15=' ',<3>,1:15]\n" + 
	              "[@6,16:16='a',<2>,1:16]\n" + 
	              "[@7,17:16='<EOF>',<-1>,1:17]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testHexVsID() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "HexLiteral : '0' ('x'|'X') HexDigit+ ;\r\n" +
	                  "DecimalLiteral : ('0' | '1'..'9' '0'..'9'*) ;\r\n" +
	                  "FloatingPointLiteral : ('0x' | '0X') HexDigit* ('.' HexDigit*)? ;\r\n" +
	                  "DOT : '.' ;\r\n" +
	                  "ID : 'a'..'z'+ ;\r\n" +
	                  "fragment HexDigit : ('0'..'9'|'a'..'f'|'A'..'F') ;\r\n" +
	                  "WS : (' '|'\\n')+;\r";
		String found = execLexer("L.g4", grammar, "L", "x 0 1 a.b a.l", false);
		assertEquals("[@0,0:0='x',<5>,1:0]\n" + 
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
	              "[@13,13:12='<EOF>',<-1>,1:13]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testEOFByItself() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "DONE : EOF ;\r\n" +
	                  "A : 'a';\r";
		String found = execLexer("L.g4", grammar, "L", "", false);
		assertEquals("[@0,0:-1='<EOF>',<1>,1:0]\n" + 
	              "[@1,0:-1='<EOF>',<-1>,1:0]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testEOFSuffixInFirstRule_1() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "A : 'a' EOF ;\r\n" +
	                  "B : 'a';\r\n" +
	                  "C : 'c';\r";
		String found = execLexer("L.g4", grammar, "L", "", false);
		assertEquals("[@0,0:-1='<EOF>',<-1>,1:0]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testEOFSuffixInFirstRule_2() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "A : 'a' EOF ;\r\n" +
	                  "B : 'a';\r\n" +
	                  "C : 'c';\r";
		String found = execLexer("L.g4", grammar, "L", "a", false);
		assertEquals("[@0,0:0='a',<1>,1:0]\n" + 
	              "[@1,1:0='<EOF>',<-1>,1:1]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testCharSet() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "I : '0'..'9'+ {document.getElementById('output').value += \"I\" + '\\n';} ;\r\n" +
	                  "WS : [ \\n\\u000D] -> skip ;\r";
		String found = execLexer("L.g4", grammar, "L", "34\n 34", false);
		assertEquals("I\n" + 
	              "I\n" + 
	              "[@0,0:1='34',<1>,1:0]\n" + 
	              "[@1,4:5='34',<1>,2:1]\n" + 
	              "[@2,6:5='<EOF>',<-1>,2:3]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testCharSetPlus() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "I : '0'..'9'+ {document.getElementById('output').value += \"I\" + '\\n';} ;\r\n" +
	                  "WS : [ \\n\\u000D]+ -> skip ;\r";
		String found = execLexer("L.g4", grammar, "L", "34\n 34", false);
		assertEquals("I\n" + 
	              "I\n" + 
	              "[@0,0:1='34',<1>,1:0]\n" + 
	              "[@1,4:5='34',<1>,2:1]\n" + 
	              "[@2,6:5='<EOF>',<-1>,2:3]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testCharSetNot() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "I : ~[ab \\n] ~[ \\ncd]* {document.getElementById('output').value += \"I\" + '\\n';} ;\r\n" +
	                  "WS : [ \\n\\u000D]+ -> skip ;\r";
		String found = execLexer("L.g4", grammar, "L", "xaf", false);
		assertEquals("I\n" + 
	              "[@0,0:2='xaf',<1>,1:0]\n" + 
	              "[@1,3:2='<EOF>',<-1>,1:3]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testCharSetInSet() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "I : (~[ab \\n]|'a')  {document.getElementById('output').value += \"I\" + '\\n';} ;\r\n" +
	                  "WS : [ \\n\\u000D]+ -> skip ;\r\n" +
	                  "	";
		String found = execLexer("L.g4", grammar, "L", "a x", false);
		assertEquals("I\n" + 
	              "I\n" + 
	              "[@0,0:0='a',<1>,1:0]\n" + 
	              "[@1,2:2='x',<1>,1:2]\n" + 
	              "[@2,3:2='<EOF>',<-1>,1:3]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testCharSetRange() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "I : [0-9]+ {document.getElementById('output').value += \"I\" + '\\n';} ;\r\n" +
	                  "ID : [a-zA-Z] [a-zA-Z0-9]* {document.getElementById('output').value += \"ID\" + '\\n';} ;\r\n" +
	                  "WS : [ \\n\\u0009\\r]+ -> skip ;\r";
		String found = execLexer("L.g4", grammar, "L", "34\n 34 a2 abc \n   ", false);
		assertEquals("I\n" + 
	              "I\n" + 
	              "ID\n" + 
	              "ID\n" + 
	              "[@0,0:1='34',<1>,1:0]\n" + 
	              "[@1,4:5='34',<1>,2:1]\n" + 
	              "[@2,7:8='a2',<2>,2:4]\n" + 
	              "[@3,10:12='abc',<2>,2:7]\n" + 
	              "[@4,18:17='<EOF>',<-1>,3:3]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testCharSetWithMissingEndRange() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "I : [0-]+ {document.getElementById('output').value += \"I\" + '\\n';} ;\r\n" +
	                  "WS : [ \\n\\u000D]+ -> skip ;\r";
		String found = execLexer("L.g4", grammar, "L", "00\n", false);
		assertEquals("I\n" + 
	              "[@0,0:1='00',<1>,1:0]\n" + 
	              "[@1,3:2='<EOF>',<-1>,2:0]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testCharSetWithMissingEscapeChar() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "I : [0-9]+ {document.getElementById('output').value += \"I\" + '\\n';} ;\r\n" +
	                  "WS : [ \\u]+ -> skip ;\r";
		String found = execLexer("L.g4", grammar, "L", "34 ", false);
		assertEquals("I\n" + 
	              "[@0,0:1='34',<1>,1:0]\n" + 
	              "[@1,3:2='<EOF>',<-1>,1:3]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testCharSetWithEscapedChar() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "DASHBRACK : [\\-\\]]+ {document.getElementById('output').value += \"DASHBRACK\" + '\\n';} ;\r\n" +
	                  "WS : [ \\u]+ -> skip ;\r";
		String found = execLexer("L.g4", grammar, "L", "- ] ", false);
		assertEquals("DASHBRACK\n" + 
	              "DASHBRACK\n" + 
	              "[@0,0:0='-',<1>,1:0]\n" + 
	              "[@1,2:2=']',<1>,1:2]\n" + 
	              "[@2,4:3='<EOF>',<-1>,1:4]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testCharSetWithReversedRange() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "A : [z-a9]+ {document.getElementById('output').value += \"A\" + '\\n';} ;\r\n" +
	                  "WS : [ \\u]+ -> skip ;\r";
		String found = execLexer("L.g4", grammar, "L", "9", false);
		assertEquals("A\n" + 
	              "[@0,0:0='9',<1>,1:0]\n" + 
	              "[@1,1:0='<EOF>',<-1>,1:1]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testCharSetWithQuote1() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "A : [\"a-z]+ {document.getElementById('output').value += \"A\" + '\\n';} ;\r\n" +
	                  "WS : [ \\n\\t]+ -> skip ;\r";
		String found = execLexer("L.g4", grammar, "L", "b\"a", false);
		assertEquals("A\n" + 
	              "[@0,0:2='b\"a',<1>,1:0]\n" + 
	              "[@1,3:2='<EOF>',<-1>,1:3]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testCharSetWithQuote2() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "A : [\"\\ab]+ {document.getElementById('output').value += \"A\" + '\\n';} ;\r\n" +
	                  "WS : [ \\n\\t]+ -> skip ;\r";
		String found = execLexer("L.g4", grammar, "L", "b\"\\a", false);
		assertEquals("A\n" + 
	              "[@0,0:3='b\"\\a',<1>,1:0]\n" + 
	              "[@1,4:3='<EOF>',<-1>,1:4]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testPositionAdjustingLexer() throws Exception {
		String grammar = "lexer grammar PositionAdjustingLexer;\r\n" +
	                  "\r\n" +
	                  "@members {\r\n" +
	                  "PositionAdjustingLexer.prototype.resetAcceptPosition = function(index, line, column) {\r\n" +
	                  "	this._input.seek(index);\r\n" +
	                  "	this.line = line;\r\n" +
	                  "	this.column = column;\r\n" +
	                  "	this._interp.consume(this._input);\r\n" +
	                  "};\r\n" +
	                  "\r\n" +
	                  "PositionAdjustingLexer.prototype.nextToken = function() {\r\n" +
	                  "	if (!(\"resetAcceptPosition\" in this._interp)) {\r\n" +
	                  "		var lexer = this;\r\n" +
	                  "		this._interp.resetAcceptPosition = function(index, line, column) { lexer.resetAcceptPosition(index, line, column); };\r\n" +
	                  "	}\r\n" +
	                  "	return antlr4.Lexer.prototype.nextToken.call(this);\r\n" +
	                  "};\r\n" +
	                  "\r\n" +
	                  "PositionAdjustingLexer.prototype.emit = function() {\r\n" +
	                  "	switch(this._type) {\r\n" +
	                  "	case TOKENS:\r\n" +
	                  "		this.handleAcceptPositionForKeyword(\"tokens\");\r\n" +
	                  "		break;\r\n" +
	                  "	case LABEL:\r\n" +
	                  "		this.handleAcceptPositionForIdentifier();\r\n" +
	                  "		break;\r\n" +
	                  "	}\r\n" +
	                  "	return antlr4.Lexer.prototype.emit.call(this);\r\n" +
	                  "};\r\n" +
	                  "\r\n" +
	                  "PositionAdjustingLexer.prototype.handleAcceptPositionForIdentifier = function() {\r\n" +
	                  "	var tokenText = this.text;\r\n" +
	                  "	var identifierLength = 0;\r\n" +
	                  "	while (identifierLength < tokenText.length && \r\n" +
	                  "		PositionAdjustingLexer.isIdentifierChar(tokenText[identifierLength])\r\n" +
	                  "	) {\r\n" +
	                  "		identifierLength += 1;\r\n" +
	                  "	}\r\n" +
	                  "	if (this._input.index > this._tokenStartCharIndex + identifierLength) {\r\n" +
	                  "		var offset = identifierLength - 1;\r\n" +
	                  "		this._interp.resetAcceptPosition(this._tokenStartCharIndex + offset, \r\n" +
	                  "				this._tokenStartLine, this._tokenStartColumn + offset);\r\n" +
	                  "		return true;\r\n" +
	                  "	} else {\r\n" +
	                  "		return false;\r\n" +
	                  "	}\r\n" +
	                  "};\r\n" +
	                  "\r\n" +
	                  "PositionAdjustingLexer.prototype.handleAcceptPositionForKeyword = function(keyword) {\r\n" +
	                  "	if (this._input.index > this._tokenStartCharIndex + keyword.length) {\r\n" +
	                  "		var offset = keyword.length - 1;\r\n" +
	                  "		this._interp.resetAcceptPosition(this._tokenStartCharIndex + offset, \r\n" +
	                  "			this._tokenStartLine, this._tokenStartColumn + offset);\r\n" +
	                  "		return true;\r\n" +
	                  "	} else {\r\n" +
	                  "		return false;\r\n" +
	                  "	}\r\n" +
	                  "};\r\n" +
	                  "\r\n" +
	                  "PositionAdjustingLexer.isIdentifierChar = function(c) {\r\n" +
	                  "	return c.match(/^[0-9a-zA-Z_]+$/);\r\n" +
	                  "}\r\n" +
	                  "\r\n" +
	                  "}\r\n" +
	                  "\r\n" +
	                  "ASSIGN : '=' ;\r\n" +
	                  "PLUS_ASSIGN : '+=' ;\r\n" +
	                  "LCURLY:	'{';\r\n" +
	                  "\r\n" +
	                  "// 'tokens' followed by '{'\r\n" +
	                  "TOKENS : 'tokens' IGNORED '{';\r\n" +
	                  "\r\n" +
	                  "// IDENTIFIER followed by '+=' or '='\r\n" +
	                  "LABEL\r\n" +
	                  "	:	IDENTIFIER IGNORED '+'? '='\r\n" +
	                  "	;\r\n" +
	                  "\r\n" +
	                  "IDENTIFIER\r\n" +
	                  "	:	[a-zA-Z_] [a-zA-Z0-9_]*\r\n" +
	                  "	;\r\n" +
	                  "\r\n" +
	                  "fragment\r\n" +
	                  "IGNORED\r\n" +
	                  "	:	[ \\t\\r\\n]*\r\n" +
	                  "	;\r\n" +
	                  "\r\n" +
	                  "NEWLINE\r\n" +
	                  "	:	[\\r\\n]+ -> skip\r\n" +
	                  "	;\r\n" +
	                  "\r\n" +
	                  "WS\r\n" +
	                  "	:	[ \\t]+ -> skip\r\n" +
	                  "	;\r";
		String found = execLexer("PositionAdjustingLexer.g4", grammar, "PositionAdjustingLexer", "tokens\ntokens {\nnotLabel\nlabel1 =\nlabel2 +=\nnotLabel\n", false);
		assertEquals("[@0,0:5='tokens',<6>,1:0]\n" + 
	              "[@1,7:12='tokens',<4>,2:0]\n" + 
	              "[@2,14:14='{',<3>,2:7]\n" + 
	              "[@3,16:23='notLabel',<6>,3:0]\n" + 
	              "[@4,25:30='label1',<5>,4:0]\n" + 
	              "[@5,32:32='=',<1>,4:7]\n" + 
	              "[@6,34:39='label2',<5>,5:0]\n" + 
	              "[@7,41:42='+=',<2>,5:7]\n" + 
	              "[@8,44:51='notLabel',<6>,6:0]\n" + 
	              "[@9,53:52='<EOF>',<-1>,7:0]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testLargeLexer() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "WS : [ \\t\\r\\n]+ -> skip;\r\n" +
	                  "KW0 : 'KW' '0';\r\n" +
	                  "KW1 : 'KW' '1';\r\n" +
	                  "KW2 : 'KW' '2';\r\n" +
	                  "KW3 : 'KW' '3';\r\n" +
	                  "KW4 : 'KW' '4';\r\n" +
	                  "KW5 : 'KW' '5';\r\n" +
	                  "KW6 : 'KW' '6';\r\n" +
	                  "KW7 : 'KW' '7';\r\n" +
	                  "KW8 : 'KW' '8';\r\n" +
	                  "KW9 : 'KW' '9';\r\n" +
	                  "KW10 : 'KW' '10';\r\n" +
	                  "KW11 : 'KW' '11';\r\n" +
	                  "KW12 : 'KW' '12';\r\n" +
	                  "KW13 : 'KW' '13';\r\n" +
	                  "KW14 : 'KW' '14';\r\n" +
	                  "KW15 : 'KW' '15';\r\n" +
	                  "KW16 : 'KW' '16';\r\n" +
	                  "KW17 : 'KW' '17';\r\n" +
	                  "KW18 : 'KW' '18';\r\n" +
	                  "KW19 : 'KW' '19';\r\n" +
	                  "KW20 : 'KW' '20';\r\n" +
	                  "KW21 : 'KW' '21';\r\n" +
	                  "KW22 : 'KW' '22';\r\n" +
	                  "KW23 : 'KW' '23';\r\n" +
	                  "KW24 : 'KW' '24';\r\n" +
	                  "KW25 : 'KW' '25';\r\n" +
	                  "KW26 : 'KW' '26';\r\n" +
	                  "KW27 : 'KW' '27';\r\n" +
	                  "KW28 : 'KW' '28';\r\n" +
	                  "KW29 : 'KW' '29';\r\n" +
	                  "KW30 : 'KW' '30';\r\n" +
	                  "KW31 : 'KW' '31';\r\n" +
	                  "KW32 : 'KW' '32';\r\n" +
	                  "KW33 : 'KW' '33';\r\n" +
	                  "KW34 : 'KW' '34';\r\n" +
	                  "KW35 : 'KW' '35';\r\n" +
	                  "KW36 : 'KW' '36';\r\n" +
	                  "KW37 : 'KW' '37';\r\n" +
	                  "KW38 : 'KW' '38';\r\n" +
	                  "KW39 : 'KW' '39';\r\n" +
	                  "KW40 : 'KW' '40';\r\n" +
	                  "KW41 : 'KW' '41';\r\n" +
	                  "KW42 : 'KW' '42';\r\n" +
	                  "KW43 : 'KW' '43';\r\n" +
	                  "KW44 : 'KW' '44';\r\n" +
	                  "KW45 : 'KW' '45';\r\n" +
	                  "KW46 : 'KW' '46';\r\n" +
	                  "KW47 : 'KW' '47';\r\n" +
	                  "KW48 : 'KW' '48';\r\n" +
	                  "KW49 : 'KW' '49';\r\n" +
	                  "KW50 : 'KW' '50';\r\n" +
	                  "KW51 : 'KW' '51';\r\n" +
	                  "KW52 : 'KW' '52';\r\n" +
	                  "KW53 : 'KW' '53';\r\n" +
	                  "KW54 : 'KW' '54';\r\n" +
	                  "KW55 : 'KW' '55';\r\n" +
	                  "KW56 : 'KW' '56';\r\n" +
	                  "KW57 : 'KW' '57';\r\n" +
	                  "KW58 : 'KW' '58';\r\n" +
	                  "KW59 : 'KW' '59';\r\n" +
	                  "KW60 : 'KW' '60';\r\n" +
	                  "KW61 : 'KW' '61';\r\n" +
	                  "KW62 : 'KW' '62';\r\n" +
	                  "KW63 : 'KW' '63';\r\n" +
	                  "KW64 : 'KW' '64';\r\n" +
	                  "KW65 : 'KW' '65';\r\n" +
	                  "KW66 : 'KW' '66';\r\n" +
	                  "KW67 : 'KW' '67';\r\n" +
	                  "KW68 : 'KW' '68';\r\n" +
	                  "KW69 : 'KW' '69';\r\n" +
	                  "KW70 : 'KW' '70';\r\n" +
	                  "KW71 : 'KW' '71';\r\n" +
	                  "KW72 : 'KW' '72';\r\n" +
	                  "KW73 : 'KW' '73';\r\n" +
	                  "KW74 : 'KW' '74';\r\n" +
	                  "KW75 : 'KW' '75';\r\n" +
	                  "KW76 : 'KW' '76';\r\n" +
	                  "KW77 : 'KW' '77';\r\n" +
	                  "KW78 : 'KW' '78';\r\n" +
	                  "KW79 : 'KW' '79';\r\n" +
	                  "KW80 : 'KW' '80';\r\n" +
	                  "KW81 : 'KW' '81';\r\n" +
	                  "KW82 : 'KW' '82';\r\n" +
	                  "KW83 : 'KW' '83';\r\n" +
	                  "KW84 : 'KW' '84';\r\n" +
	                  "KW85 : 'KW' '85';\r\n" +
	                  "KW86 : 'KW' '86';\r\n" +
	                  "KW87 : 'KW' '87';\r\n" +
	                  "KW88 : 'KW' '88';\r\n" +
	                  "KW89 : 'KW' '89';\r\n" +
	                  "KW90 : 'KW' '90';\r\n" +
	                  "KW91 : 'KW' '91';\r\n" +
	                  "KW92 : 'KW' '92';\r\n" +
	                  "KW93 : 'KW' '93';\r\n" +
	                  "KW94 : 'KW' '94';\r\n" +
	                  "KW95 : 'KW' '95';\r\n" +
	                  "KW96 : 'KW' '96';\r\n" +
	                  "KW97 : 'KW' '97';\r\n" +
	                  "KW98 : 'KW' '98';\r\n" +
	                  "KW99 : 'KW' '99';\r\n" +
	                  "KW100 : 'KW' '100';\r\n" +
	                  "KW101 : 'KW' '101';\r\n" +
	                  "KW102 : 'KW' '102';\r\n" +
	                  "KW103 : 'KW' '103';\r\n" +
	                  "KW104 : 'KW' '104';\r\n" +
	                  "KW105 : 'KW' '105';\r\n" +
	                  "KW106 : 'KW' '106';\r\n" +
	                  "KW107 : 'KW' '107';\r\n" +
	                  "KW108 : 'KW' '108';\r\n" +
	                  "KW109 : 'KW' '109';\r\n" +
	                  "KW110 : 'KW' '110';\r\n" +
	                  "KW111 : 'KW' '111';\r\n" +
	                  "KW112 : 'KW' '112';\r\n" +
	                  "KW113 : 'KW' '113';\r\n" +
	                  "KW114 : 'KW' '114';\r\n" +
	                  "KW115 : 'KW' '115';\r\n" +
	                  "KW116 : 'KW' '116';\r\n" +
	                  "KW117 : 'KW' '117';\r\n" +
	                  "KW118 : 'KW' '118';\r\n" +
	                  "KW119 : 'KW' '119';\r\n" +
	                  "KW120 : 'KW' '120';\r\n" +
	                  "KW121 : 'KW' '121';\r\n" +
	                  "KW122 : 'KW' '122';\r\n" +
	                  "KW123 : 'KW' '123';\r\n" +
	                  "KW124 : 'KW' '124';\r\n" +
	                  "KW125 : 'KW' '125';\r\n" +
	                  "KW126 : 'KW' '126';\r\n" +
	                  "KW127 : 'KW' '127';\r\n" +
	                  "KW128 : 'KW' '128';\r\n" +
	                  "KW129 : 'KW' '129';\r\n" +
	                  "KW130 : 'KW' '130';\r\n" +
	                  "KW131 : 'KW' '131';\r\n" +
	                  "KW132 : 'KW' '132';\r\n" +
	                  "KW133 : 'KW' '133';\r\n" +
	                  "KW134 : 'KW' '134';\r\n" +
	                  "KW135 : 'KW' '135';\r\n" +
	                  "KW136 : 'KW' '136';\r\n" +
	                  "KW137 : 'KW' '137';\r\n" +
	                  "KW138 : 'KW' '138';\r\n" +
	                  "KW139 : 'KW' '139';\r\n" +
	                  "KW140 : 'KW' '140';\r\n" +
	                  "KW141 : 'KW' '141';\r\n" +
	                  "KW142 : 'KW' '142';\r\n" +
	                  "KW143 : 'KW' '143';\r\n" +
	                  "KW144 : 'KW' '144';\r\n" +
	                  "KW145 : 'KW' '145';\r\n" +
	                  "KW146 : 'KW' '146';\r\n" +
	                  "KW147 : 'KW' '147';\r\n" +
	                  "KW148 : 'KW' '148';\r\n" +
	                  "KW149 : 'KW' '149';\r\n" +
	                  "KW150 : 'KW' '150';\r\n" +
	                  "KW151 : 'KW' '151';\r\n" +
	                  "KW152 : 'KW' '152';\r\n" +
	                  "KW153 : 'KW' '153';\r\n" +
	                  "KW154 : 'KW' '154';\r\n" +
	                  "KW155 : 'KW' '155';\r\n" +
	                  "KW156 : 'KW' '156';\r\n" +
	                  "KW157 : 'KW' '157';\r\n" +
	                  "KW158 : 'KW' '158';\r\n" +
	                  "KW159 : 'KW' '159';\r\n" +
	                  "KW160 : 'KW' '160';\r\n" +
	                  "KW161 : 'KW' '161';\r\n" +
	                  "KW162 : 'KW' '162';\r\n" +
	                  "KW163 : 'KW' '163';\r\n" +
	                  "KW164 : 'KW' '164';\r\n" +
	                  "KW165 : 'KW' '165';\r\n" +
	                  "KW166 : 'KW' '166';\r\n" +
	                  "KW167 : 'KW' '167';\r\n" +
	                  "KW168 : 'KW' '168';\r\n" +
	                  "KW169 : 'KW' '169';\r\n" +
	                  "KW170 : 'KW' '170';\r\n" +
	                  "KW171 : 'KW' '171';\r\n" +
	                  "KW172 : 'KW' '172';\r\n" +
	                  "KW173 : 'KW' '173';\r\n" +
	                  "KW174 : 'KW' '174';\r\n" +
	                  "KW175 : 'KW' '175';\r\n" +
	                  "KW176 : 'KW' '176';\r\n" +
	                  "KW177 : 'KW' '177';\r\n" +
	                  "KW178 : 'KW' '178';\r\n" +
	                  "KW179 : 'KW' '179';\r\n" +
	                  "KW180 : 'KW' '180';\r\n" +
	                  "KW181 : 'KW' '181';\r\n" +
	                  "KW182 : 'KW' '182';\r\n" +
	                  "KW183 : 'KW' '183';\r\n" +
	                  "KW184 : 'KW' '184';\r\n" +
	                  "KW185 : 'KW' '185';\r\n" +
	                  "KW186 : 'KW' '186';\r\n" +
	                  "KW187 : 'KW' '187';\r\n" +
	                  "KW188 : 'KW' '188';\r\n" +
	                  "KW189 : 'KW' '189';\r\n" +
	                  "KW190 : 'KW' '190';\r\n" +
	                  "KW191 : 'KW' '191';\r\n" +
	                  "KW192 : 'KW' '192';\r\n" +
	                  "KW193 : 'KW' '193';\r\n" +
	                  "KW194 : 'KW' '194';\r\n" +
	                  "KW195 : 'KW' '195';\r\n" +
	                  "KW196 : 'KW' '196';\r\n" +
	                  "KW197 : 'KW' '197';\r\n" +
	                  "KW198 : 'KW' '198';\r\n" +
	                  "KW199 : 'KW' '199';\r\n" +
	                  "KW200 : 'KW' '200';\r\n" +
	                  "KW201 : 'KW' '201';\r\n" +
	                  "KW202 : 'KW' '202';\r\n" +
	                  "KW203 : 'KW' '203';\r\n" +
	                  "KW204 : 'KW' '204';\r\n" +
	                  "KW205 : 'KW' '205';\r\n" +
	                  "KW206 : 'KW' '206';\r\n" +
	                  "KW207 : 'KW' '207';\r\n" +
	                  "KW208 : 'KW' '208';\r\n" +
	                  "KW209 : 'KW' '209';\r\n" +
	                  "KW210 : 'KW' '210';\r\n" +
	                  "KW211 : 'KW' '211';\r\n" +
	                  "KW212 : 'KW' '212';\r\n" +
	                  "KW213 : 'KW' '213';\r\n" +
	                  "KW214 : 'KW' '214';\r\n" +
	                  "KW215 : 'KW' '215';\r\n" +
	                  "KW216 : 'KW' '216';\r\n" +
	                  "KW217 : 'KW' '217';\r\n" +
	                  "KW218 : 'KW' '218';\r\n" +
	                  "KW219 : 'KW' '219';\r\n" +
	                  "KW220 : 'KW' '220';\r\n" +
	                  "KW221 : 'KW' '221';\r\n" +
	                  "KW222 : 'KW' '222';\r\n" +
	                  "KW223 : 'KW' '223';\r\n" +
	                  "KW224 : 'KW' '224';\r\n" +
	                  "KW225 : 'KW' '225';\r\n" +
	                  "KW226 : 'KW' '226';\r\n" +
	                  "KW227 : 'KW' '227';\r\n" +
	                  "KW228 : 'KW' '228';\r\n" +
	                  "KW229 : 'KW' '229';\r\n" +
	                  "KW230 : 'KW' '230';\r\n" +
	                  "KW231 : 'KW' '231';\r\n" +
	                  "KW232 : 'KW' '232';\r\n" +
	                  "KW233 : 'KW' '233';\r\n" +
	                  "KW234 : 'KW' '234';\r\n" +
	                  "KW235 : 'KW' '235';\r\n" +
	                  "KW236 : 'KW' '236';\r\n" +
	                  "KW237 : 'KW' '237';\r\n" +
	                  "KW238 : 'KW' '238';\r\n" +
	                  "KW239 : 'KW' '239';\r\n" +
	                  "KW240 : 'KW' '240';\r\n" +
	                  "KW241 : 'KW' '241';\r\n" +
	                  "KW242 : 'KW' '242';\r\n" +
	                  "KW243 : 'KW' '243';\r\n" +
	                  "KW244 : 'KW' '244';\r\n" +
	                  "KW245 : 'KW' '245';\r\n" +
	                  "KW246 : 'KW' '246';\r\n" +
	                  "KW247 : 'KW' '247';\r\n" +
	                  "KW248 : 'KW' '248';\r\n" +
	                  "KW249 : 'KW' '249';\r\n" +
	                  "KW250 : 'KW' '250';\r\n" +
	                  "KW251 : 'KW' '251';\r\n" +
	                  "KW252 : 'KW' '252';\r\n" +
	                  "KW253 : 'KW' '253';\r\n" +
	                  "KW254 : 'KW' '254';\r\n" +
	                  "KW255 : 'KW' '255';\r\n" +
	                  "KW256 : 'KW' '256';\r\n" +
	                  "KW257 : 'KW' '257';\r\n" +
	                  "KW258 : 'KW' '258';\r\n" +
	                  "KW259 : 'KW' '259';\r\n" +
	                  "KW260 : 'KW' '260';\r\n" +
	                  "KW261 : 'KW' '261';\r\n" +
	                  "KW262 : 'KW' '262';\r\n" +
	                  "KW263 : 'KW' '263';\r\n" +
	                  "KW264 : 'KW' '264';\r\n" +
	                  "KW265 : 'KW' '265';\r\n" +
	                  "KW266 : 'KW' '266';\r\n" +
	                  "KW267 : 'KW' '267';\r\n" +
	                  "KW268 : 'KW' '268';\r\n" +
	                  "KW269 : 'KW' '269';\r\n" +
	                  "KW270 : 'KW' '270';\r\n" +
	                  "KW271 : 'KW' '271';\r\n" +
	                  "KW272 : 'KW' '272';\r\n" +
	                  "KW273 : 'KW' '273';\r\n" +
	                  "KW274 : 'KW' '274';\r\n" +
	                  "KW275 : 'KW' '275';\r\n" +
	                  "KW276 : 'KW' '276';\r\n" +
	                  "KW277 : 'KW' '277';\r\n" +
	                  "KW278 : 'KW' '278';\r\n" +
	                  "KW279 : 'KW' '279';\r\n" +
	                  "KW280 : 'KW' '280';\r\n" +
	                  "KW281 : 'KW' '281';\r\n" +
	                  "KW282 : 'KW' '282';\r\n" +
	                  "KW283 : 'KW' '283';\r\n" +
	                  "KW284 : 'KW' '284';\r\n" +
	                  "KW285 : 'KW' '285';\r\n" +
	                  "KW286 : 'KW' '286';\r\n" +
	                  "KW287 : 'KW' '287';\r\n" +
	                  "KW288 : 'KW' '288';\r\n" +
	                  "KW289 : 'KW' '289';\r\n" +
	                  "KW290 : 'KW' '290';\r\n" +
	                  "KW291 : 'KW' '291';\r\n" +
	                  "KW292 : 'KW' '292';\r\n" +
	                  "KW293 : 'KW' '293';\r\n" +
	                  "KW294 : 'KW' '294';\r\n" +
	                  "KW295 : 'KW' '295';\r\n" +
	                  "KW296 : 'KW' '296';\r\n" +
	                  "KW297 : 'KW' '297';\r\n" +
	                  "KW298 : 'KW' '298';\r\n" +
	                  "KW299 : 'KW' '299';\r\n" +
	                  "KW300 : 'KW' '300';\r\n" +
	                  "KW301 : 'KW' '301';\r\n" +
	                  "KW302 : 'KW' '302';\r\n" +
	                  "KW303 : 'KW' '303';\r\n" +
	                  "KW304 : 'KW' '304';\r\n" +
	                  "KW305 : 'KW' '305';\r\n" +
	                  "KW306 : 'KW' '306';\r\n" +
	                  "KW307 : 'KW' '307';\r\n" +
	                  "KW308 : 'KW' '308';\r\n" +
	                  "KW309 : 'KW' '309';\r\n" +
	                  "KW310 : 'KW' '310';\r\n" +
	                  "KW311 : 'KW' '311';\r\n" +
	                  "KW312 : 'KW' '312';\r\n" +
	                  "KW313 : 'KW' '313';\r\n" +
	                  "KW314 : 'KW' '314';\r\n" +
	                  "KW315 : 'KW' '315';\r\n" +
	                  "KW316 : 'KW' '316';\r\n" +
	                  "KW317 : 'KW' '317';\r\n" +
	                  "KW318 : 'KW' '318';\r\n" +
	                  "KW319 : 'KW' '319';\r\n" +
	                  "KW320 : 'KW' '320';\r\n" +
	                  "KW321 : 'KW' '321';\r\n" +
	                  "KW322 : 'KW' '322';\r\n" +
	                  "KW323 : 'KW' '323';\r\n" +
	                  "KW324 : 'KW' '324';\r\n" +
	                  "KW325 : 'KW' '325';\r\n" +
	                  "KW326 : 'KW' '326';\r\n" +
	                  "KW327 : 'KW' '327';\r\n" +
	                  "KW328 : 'KW' '328';\r\n" +
	                  "KW329 : 'KW' '329';\r\n" +
	                  "KW330 : 'KW' '330';\r\n" +
	                  "KW331 : 'KW' '331';\r\n" +
	                  "KW332 : 'KW' '332';\r\n" +
	                  "KW333 : 'KW' '333';\r\n" +
	                  "KW334 : 'KW' '334';\r\n" +
	                  "KW335 : 'KW' '335';\r\n" +
	                  "KW336 : 'KW' '336';\r\n" +
	                  "KW337 : 'KW' '337';\r\n" +
	                  "KW338 : 'KW' '338';\r\n" +
	                  "KW339 : 'KW' '339';\r\n" +
	                  "KW340 : 'KW' '340';\r\n" +
	                  "KW341 : 'KW' '341';\r\n" +
	                  "KW342 : 'KW' '342';\r\n" +
	                  "KW343 : 'KW' '343';\r\n" +
	                  "KW344 : 'KW' '344';\r\n" +
	                  "KW345 : 'KW' '345';\r\n" +
	                  "KW346 : 'KW' '346';\r\n" +
	                  "KW347 : 'KW' '347';\r\n" +
	                  "KW348 : 'KW' '348';\r\n" +
	                  "KW349 : 'KW' '349';\r\n" +
	                  "KW350 : 'KW' '350';\r\n" +
	                  "KW351 : 'KW' '351';\r\n" +
	                  "KW352 : 'KW' '352';\r\n" +
	                  "KW353 : 'KW' '353';\r\n" +
	                  "KW354 : 'KW' '354';\r\n" +
	                  "KW355 : 'KW' '355';\r\n" +
	                  "KW356 : 'KW' '356';\r\n" +
	                  "KW357 : 'KW' '357';\r\n" +
	                  "KW358 : 'KW' '358';\r\n" +
	                  "KW359 : 'KW' '359';\r\n" +
	                  "KW360 : 'KW' '360';\r\n" +
	                  "KW361 : 'KW' '361';\r\n" +
	                  "KW362 : 'KW' '362';\r\n" +
	                  "KW363 : 'KW' '363';\r\n" +
	                  "KW364 : 'KW' '364';\r\n" +
	                  "KW365 : 'KW' '365';\r\n" +
	                  "KW366 : 'KW' '366';\r\n" +
	                  "KW367 : 'KW' '367';\r\n" +
	                  "KW368 : 'KW' '368';\r\n" +
	                  "KW369 : 'KW' '369';\r\n" +
	                  "KW370 : 'KW' '370';\r\n" +
	                  "KW371 : 'KW' '371';\r\n" +
	                  "KW372 : 'KW' '372';\r\n" +
	                  "KW373 : 'KW' '373';\r\n" +
	                  "KW374 : 'KW' '374';\r\n" +
	                  "KW375 : 'KW' '375';\r\n" +
	                  "KW376 : 'KW' '376';\r\n" +
	                  "KW377 : 'KW' '377';\r\n" +
	                  "KW378 : 'KW' '378';\r\n" +
	                  "KW379 : 'KW' '379';\r\n" +
	                  "KW380 : 'KW' '380';\r\n" +
	                  "KW381 : 'KW' '381';\r\n" +
	                  "KW382 : 'KW' '382';\r\n" +
	                  "KW383 : 'KW' '383';\r\n" +
	                  "KW384 : 'KW' '384';\r\n" +
	                  "KW385 : 'KW' '385';\r\n" +
	                  "KW386 : 'KW' '386';\r\n" +
	                  "KW387 : 'KW' '387';\r\n" +
	                  "KW388 : 'KW' '388';\r\n" +
	                  "KW389 : 'KW' '389';\r\n" +
	                  "KW390 : 'KW' '390';\r\n" +
	                  "KW391 : 'KW' '391';\r\n" +
	                  "KW392 : 'KW' '392';\r\n" +
	                  "KW393 : 'KW' '393';\r\n" +
	                  "KW394 : 'KW' '394';\r\n" +
	                  "KW395 : 'KW' '395';\r\n" +
	                  "KW396 : 'KW' '396';\r\n" +
	                  "KW397 : 'KW' '397';\r\n" +
	                  "KW398 : 'KW' '398';\r\n" +
	                  "KW399 : 'KW' '399';\r\n" +
	                  "KW400 : 'KW' '400';\r\n" +
	                  "KW401 : 'KW' '401';\r\n" +
	                  "KW402 : 'KW' '402';\r\n" +
	                  "KW403 : 'KW' '403';\r\n" +
	                  "KW404 : 'KW' '404';\r\n" +
	                  "KW405 : 'KW' '405';\r\n" +
	                  "KW406 : 'KW' '406';\r\n" +
	                  "KW407 : 'KW' '407';\r\n" +
	                  "KW408 : 'KW' '408';\r\n" +
	                  "KW409 : 'KW' '409';\r\n" +
	                  "KW410 : 'KW' '410';\r\n" +
	                  "KW411 : 'KW' '411';\r\n" +
	                  "KW412 : 'KW' '412';\r\n" +
	                  "KW413 : 'KW' '413';\r\n" +
	                  "KW414 : 'KW' '414';\r\n" +
	                  "KW415 : 'KW' '415';\r\n" +
	                  "KW416 : 'KW' '416';\r\n" +
	                  "KW417 : 'KW' '417';\r\n" +
	                  "KW418 : 'KW' '418';\r\n" +
	                  "KW419 : 'KW' '419';\r\n" +
	                  "KW420 : 'KW' '420';\r\n" +
	                  "KW421 : 'KW' '421';\r\n" +
	                  "KW422 : 'KW' '422';\r\n" +
	                  "KW423 : 'KW' '423';\r\n" +
	                  "KW424 : 'KW' '424';\r\n" +
	                  "KW425 : 'KW' '425';\r\n" +
	                  "KW426 : 'KW' '426';\r\n" +
	                  "KW427 : 'KW' '427';\r\n" +
	                  "KW428 : 'KW' '428';\r\n" +
	                  "KW429 : 'KW' '429';\r\n" +
	                  "KW430 : 'KW' '430';\r\n" +
	                  "KW431 : 'KW' '431';\r\n" +
	                  "KW432 : 'KW' '432';\r\n" +
	                  "KW433 : 'KW' '433';\r\n" +
	                  "KW434 : 'KW' '434';\r\n" +
	                  "KW435 : 'KW' '435';\r\n" +
	                  "KW436 : 'KW' '436';\r\n" +
	                  "KW437 : 'KW' '437';\r\n" +
	                  "KW438 : 'KW' '438';\r\n" +
	                  "KW439 : 'KW' '439';\r\n" +
	                  "KW440 : 'KW' '440';\r\n" +
	                  "KW441 : 'KW' '441';\r\n" +
	                  "KW442 : 'KW' '442';\r\n" +
	                  "KW443 : 'KW' '443';\r\n" +
	                  "KW444 : 'KW' '444';\r\n" +
	                  "KW445 : 'KW' '445';\r\n" +
	                  "KW446 : 'KW' '446';\r\n" +
	                  "KW447 : 'KW' '447';\r\n" +
	                  "KW448 : 'KW' '448';\r\n" +
	                  "KW449 : 'KW' '449';\r\n" +
	                  "KW450 : 'KW' '450';\r\n" +
	                  "KW451 : 'KW' '451';\r\n" +
	                  "KW452 : 'KW' '452';\r\n" +
	                  "KW453 : 'KW' '453';\r\n" +
	                  "KW454 : 'KW' '454';\r\n" +
	                  "KW455 : 'KW' '455';\r\n" +
	                  "KW456 : 'KW' '456';\r\n" +
	                  "KW457 : 'KW' '457';\r\n" +
	                  "KW458 : 'KW' '458';\r\n" +
	                  "KW459 : 'KW' '459';\r\n" +
	                  "KW460 : 'KW' '460';\r\n" +
	                  "KW461 : 'KW' '461';\r\n" +
	                  "KW462 : 'KW' '462';\r\n" +
	                  "KW463 : 'KW' '463';\r\n" +
	                  "KW464 : 'KW' '464';\r\n" +
	                  "KW465 : 'KW' '465';\r\n" +
	                  "KW466 : 'KW' '466';\r\n" +
	                  "KW467 : 'KW' '467';\r\n" +
	                  "KW468 : 'KW' '468';\r\n" +
	                  "KW469 : 'KW' '469';\r\n" +
	                  "KW470 : 'KW' '470';\r\n" +
	                  "KW471 : 'KW' '471';\r\n" +
	                  "KW472 : 'KW' '472';\r\n" +
	                  "KW473 : 'KW' '473';\r\n" +
	                  "KW474 : 'KW' '474';\r\n" +
	                  "KW475 : 'KW' '475';\r\n" +
	                  "KW476 : 'KW' '476';\r\n" +
	                  "KW477 : 'KW' '477';\r\n" +
	                  "KW478 : 'KW' '478';\r\n" +
	                  "KW479 : 'KW' '479';\r\n" +
	                  "KW480 : 'KW' '480';\r\n" +
	                  "KW481 : 'KW' '481';\r\n" +
	                  "KW482 : 'KW' '482';\r\n" +
	                  "KW483 : 'KW' '483';\r\n" +
	                  "KW484 : 'KW' '484';\r\n" +
	                  "KW485 : 'KW' '485';\r\n" +
	                  "KW486 : 'KW' '486';\r\n" +
	                  "KW487 : 'KW' '487';\r\n" +
	                  "KW488 : 'KW' '488';\r\n" +
	                  "KW489 : 'KW' '489';\r\n" +
	                  "KW490 : 'KW' '490';\r\n" +
	                  "KW491 : 'KW' '491';\r\n" +
	                  "KW492 : 'KW' '492';\r\n" +
	                  "KW493 : 'KW' '493';\r\n" +
	                  "KW494 : 'KW' '494';\r\n" +
	                  "KW495 : 'KW' '495';\r\n" +
	                  "KW496 : 'KW' '496';\r\n" +
	                  "KW497 : 'KW' '497';\r\n" +
	                  "KW498 : 'KW' '498';\r\n" +
	                  "KW499 : 'KW' '499';\r\n" +
	                  "KW500 : 'KW' '500';\r\n" +
	                  "KW501 : 'KW' '501';\r\n" +
	                  "KW502 : 'KW' '502';\r\n" +
	                  "KW503 : 'KW' '503';\r\n" +
	                  "KW504 : 'KW' '504';\r\n" +
	                  "KW505 : 'KW' '505';\r\n" +
	                  "KW506 : 'KW' '506';\r\n" +
	                  "KW507 : 'KW' '507';\r\n" +
	                  "KW508 : 'KW' '508';\r\n" +
	                  "KW509 : 'KW' '509';\r\n" +
	                  "KW510 : 'KW' '510';\r\n" +
	                  "KW511 : 'KW' '511';\r\n" +
	                  "KW512 : 'KW' '512';\r\n" +
	                  "KW513 : 'KW' '513';\r\n" +
	                  "KW514 : 'KW' '514';\r\n" +
	                  "KW515 : 'KW' '515';\r\n" +
	                  "KW516 : 'KW' '516';\r\n" +
	                  "KW517 : 'KW' '517';\r\n" +
	                  "KW518 : 'KW' '518';\r\n" +
	                  "KW519 : 'KW' '519';\r\n" +
	                  "KW520 : 'KW' '520';\r\n" +
	                  "KW521 : 'KW' '521';\r\n" +
	                  "KW522 : 'KW' '522';\r\n" +
	                  "KW523 : 'KW' '523';\r\n" +
	                  "KW524 : 'KW' '524';\r\n" +
	                  "KW525 : 'KW' '525';\r\n" +
	                  "KW526 : 'KW' '526';\r\n" +
	                  "KW527 : 'KW' '527';\r\n" +
	                  "KW528 : 'KW' '528';\r\n" +
	                  "KW529 : 'KW' '529';\r\n" +
	                  "KW530 : 'KW' '530';\r\n" +
	                  "KW531 : 'KW' '531';\r\n" +
	                  "KW532 : 'KW' '532';\r\n" +
	                  "KW533 : 'KW' '533';\r\n" +
	                  "KW534 : 'KW' '534';\r\n" +
	                  "KW535 : 'KW' '535';\r\n" +
	                  "KW536 : 'KW' '536';\r\n" +
	                  "KW537 : 'KW' '537';\r\n" +
	                  "KW538 : 'KW' '538';\r\n" +
	                  "KW539 : 'KW' '539';\r\n" +
	                  "KW540 : 'KW' '540';\r\n" +
	                  "KW541 : 'KW' '541';\r\n" +
	                  "KW542 : 'KW' '542';\r\n" +
	                  "KW543 : 'KW' '543';\r\n" +
	                  "KW544 : 'KW' '544';\r\n" +
	                  "KW545 : 'KW' '545';\r\n" +
	                  "KW546 : 'KW' '546';\r\n" +
	                  "KW547 : 'KW' '547';\r\n" +
	                  "KW548 : 'KW' '548';\r\n" +
	                  "KW549 : 'KW' '549';\r\n" +
	                  "KW550 : 'KW' '550';\r\n" +
	                  "KW551 : 'KW' '551';\r\n" +
	                  "KW552 : 'KW' '552';\r\n" +
	                  "KW553 : 'KW' '553';\r\n" +
	                  "KW554 : 'KW' '554';\r\n" +
	                  "KW555 : 'KW' '555';\r\n" +
	                  "KW556 : 'KW' '556';\r\n" +
	                  "KW557 : 'KW' '557';\r\n" +
	                  "KW558 : 'KW' '558';\r\n" +
	                  "KW559 : 'KW' '559';\r\n" +
	                  "KW560 : 'KW' '560';\r\n" +
	                  "KW561 : 'KW' '561';\r\n" +
	                  "KW562 : 'KW' '562';\r\n" +
	                  "KW563 : 'KW' '563';\r\n" +
	                  "KW564 : 'KW' '564';\r\n" +
	                  "KW565 : 'KW' '565';\r\n" +
	                  "KW566 : 'KW' '566';\r\n" +
	                  "KW567 : 'KW' '567';\r\n" +
	                  "KW568 : 'KW' '568';\r\n" +
	                  "KW569 : 'KW' '569';\r\n" +
	                  "KW570 : 'KW' '570';\r\n" +
	                  "KW571 : 'KW' '571';\r\n" +
	                  "KW572 : 'KW' '572';\r\n" +
	                  "KW573 : 'KW' '573';\r\n" +
	                  "KW574 : 'KW' '574';\r\n" +
	                  "KW575 : 'KW' '575';\r\n" +
	                  "KW576 : 'KW' '576';\r\n" +
	                  "KW577 : 'KW' '577';\r\n" +
	                  "KW578 : 'KW' '578';\r\n" +
	                  "KW579 : 'KW' '579';\r\n" +
	                  "KW580 : 'KW' '580';\r\n" +
	                  "KW581 : 'KW' '581';\r\n" +
	                  "KW582 : 'KW' '582';\r\n" +
	                  "KW583 : 'KW' '583';\r\n" +
	                  "KW584 : 'KW' '584';\r\n" +
	                  "KW585 : 'KW' '585';\r\n" +
	                  "KW586 : 'KW' '586';\r\n" +
	                  "KW587 : 'KW' '587';\r\n" +
	                  "KW588 : 'KW' '588';\r\n" +
	                  "KW589 : 'KW' '589';\r\n" +
	                  "KW590 : 'KW' '590';\r\n" +
	                  "KW591 : 'KW' '591';\r\n" +
	                  "KW592 : 'KW' '592';\r\n" +
	                  "KW593 : 'KW' '593';\r\n" +
	                  "KW594 : 'KW' '594';\r\n" +
	                  "KW595 : 'KW' '595';\r\n" +
	                  "KW596 : 'KW' '596';\r\n" +
	                  "KW597 : 'KW' '597';\r\n" +
	                  "KW598 : 'KW' '598';\r\n" +
	                  "KW599 : 'KW' '599';\r\n" +
	                  "KW600 : 'KW' '600';\r\n" +
	                  "KW601 : 'KW' '601';\r\n" +
	                  "KW602 : 'KW' '602';\r\n" +
	                  "KW603 : 'KW' '603';\r\n" +
	                  "KW604 : 'KW' '604';\r\n" +
	                  "KW605 : 'KW' '605';\r\n" +
	                  "KW606 : 'KW' '606';\r\n" +
	                  "KW607 : 'KW' '607';\r\n" +
	                  "KW608 : 'KW' '608';\r\n" +
	                  "KW609 : 'KW' '609';\r\n" +
	                  "KW610 : 'KW' '610';\r\n" +
	                  "KW611 : 'KW' '611';\r\n" +
	                  "KW612 : 'KW' '612';\r\n" +
	                  "KW613 : 'KW' '613';\r\n" +
	                  "KW614 : 'KW' '614';\r\n" +
	                  "KW615 : 'KW' '615';\r\n" +
	                  "KW616 : 'KW' '616';\r\n" +
	                  "KW617 : 'KW' '617';\r\n" +
	                  "KW618 : 'KW' '618';\r\n" +
	                  "KW619 : 'KW' '619';\r\n" +
	                  "KW620 : 'KW' '620';\r\n" +
	                  "KW621 : 'KW' '621';\r\n" +
	                  "KW622 : 'KW' '622';\r\n" +
	                  "KW623 : 'KW' '623';\r\n" +
	                  "KW624 : 'KW' '624';\r\n" +
	                  "KW625 : 'KW' '625';\r\n" +
	                  "KW626 : 'KW' '626';\r\n" +
	                  "KW627 : 'KW' '627';\r\n" +
	                  "KW628 : 'KW' '628';\r\n" +
	                  "KW629 : 'KW' '629';\r\n" +
	                  "KW630 : 'KW' '630';\r\n" +
	                  "KW631 : 'KW' '631';\r\n" +
	                  "KW632 : 'KW' '632';\r\n" +
	                  "KW633 : 'KW' '633';\r\n" +
	                  "KW634 : 'KW' '634';\r\n" +
	                  "KW635 : 'KW' '635';\r\n" +
	                  "KW636 : 'KW' '636';\r\n" +
	                  "KW637 : 'KW' '637';\r\n" +
	                  "KW638 : 'KW' '638';\r\n" +
	                  "KW639 : 'KW' '639';\r\n" +
	                  "KW640 : 'KW' '640';\r\n" +
	                  "KW641 : 'KW' '641';\r\n" +
	                  "KW642 : 'KW' '642';\r\n" +
	                  "KW643 : 'KW' '643';\r\n" +
	                  "KW644 : 'KW' '644';\r\n" +
	                  "KW645 : 'KW' '645';\r\n" +
	                  "KW646 : 'KW' '646';\r\n" +
	                  "KW647 : 'KW' '647';\r\n" +
	                  "KW648 : 'KW' '648';\r\n" +
	                  "KW649 : 'KW' '649';\r\n" +
	                  "KW650 : 'KW' '650';\r\n" +
	                  "KW651 : 'KW' '651';\r\n" +
	                  "KW652 : 'KW' '652';\r\n" +
	                  "KW653 : 'KW' '653';\r\n" +
	                  "KW654 : 'KW' '654';\r\n" +
	                  "KW655 : 'KW' '655';\r\n" +
	                  "KW656 : 'KW' '656';\r\n" +
	                  "KW657 : 'KW' '657';\r\n" +
	                  "KW658 : 'KW' '658';\r\n" +
	                  "KW659 : 'KW' '659';\r\n" +
	                  "KW660 : 'KW' '660';\r\n" +
	                  "KW661 : 'KW' '661';\r\n" +
	                  "KW662 : 'KW' '662';\r\n" +
	                  "KW663 : 'KW' '663';\r\n" +
	                  "KW664 : 'KW' '664';\r\n" +
	                  "KW665 : 'KW' '665';\r\n" +
	                  "KW666 : 'KW' '666';\r\n" +
	                  "KW667 : 'KW' '667';\r\n" +
	                  "KW668 : 'KW' '668';\r\n" +
	                  "KW669 : 'KW' '669';\r\n" +
	                  "KW670 : 'KW' '670';\r\n" +
	                  "KW671 : 'KW' '671';\r\n" +
	                  "KW672 : 'KW' '672';\r\n" +
	                  "KW673 : 'KW' '673';\r\n" +
	                  "KW674 : 'KW' '674';\r\n" +
	                  "KW675 : 'KW' '675';\r\n" +
	                  "KW676 : 'KW' '676';\r\n" +
	                  "KW677 : 'KW' '677';\r\n" +
	                  "KW678 : 'KW' '678';\r\n" +
	                  "KW679 : 'KW' '679';\r\n" +
	                  "KW680 : 'KW' '680';\r\n" +
	                  "KW681 : 'KW' '681';\r\n" +
	                  "KW682 : 'KW' '682';\r\n" +
	                  "KW683 : 'KW' '683';\r\n" +
	                  "KW684 : 'KW' '684';\r\n" +
	                  "KW685 : 'KW' '685';\r\n" +
	                  "KW686 : 'KW' '686';\r\n" +
	                  "KW687 : 'KW' '687';\r\n" +
	                  "KW688 : 'KW' '688';\r\n" +
	                  "KW689 : 'KW' '689';\r\n" +
	                  "KW690 : 'KW' '690';\r\n" +
	                  "KW691 : 'KW' '691';\r\n" +
	                  "KW692 : 'KW' '692';\r\n" +
	                  "KW693 : 'KW' '693';\r\n" +
	                  "KW694 : 'KW' '694';\r\n" +
	                  "KW695 : 'KW' '695';\r\n" +
	                  "KW696 : 'KW' '696';\r\n" +
	                  "KW697 : 'KW' '697';\r\n" +
	                  "KW698 : 'KW' '698';\r\n" +
	                  "KW699 : 'KW' '699';\r\n" +
	                  "KW700 : 'KW' '700';\r\n" +
	                  "KW701 : 'KW' '701';\r\n" +
	                  "KW702 : 'KW' '702';\r\n" +
	                  "KW703 : 'KW' '703';\r\n" +
	                  "KW704 : 'KW' '704';\r\n" +
	                  "KW705 : 'KW' '705';\r\n" +
	                  "KW706 : 'KW' '706';\r\n" +
	                  "KW707 : 'KW' '707';\r\n" +
	                  "KW708 : 'KW' '708';\r\n" +
	                  "KW709 : 'KW' '709';\r\n" +
	                  "KW710 : 'KW' '710';\r\n" +
	                  "KW711 : 'KW' '711';\r\n" +
	                  "KW712 : 'KW' '712';\r\n" +
	                  "KW713 : 'KW' '713';\r\n" +
	                  "KW714 : 'KW' '714';\r\n" +
	                  "KW715 : 'KW' '715';\r\n" +
	                  "KW716 : 'KW' '716';\r\n" +
	                  "KW717 : 'KW' '717';\r\n" +
	                  "KW718 : 'KW' '718';\r\n" +
	                  "KW719 : 'KW' '719';\r\n" +
	                  "KW720 : 'KW' '720';\r\n" +
	                  "KW721 : 'KW' '721';\r\n" +
	                  "KW722 : 'KW' '722';\r\n" +
	                  "KW723 : 'KW' '723';\r\n" +
	                  "KW724 : 'KW' '724';\r\n" +
	                  "KW725 : 'KW' '725';\r\n" +
	                  "KW726 : 'KW' '726';\r\n" +
	                  "KW727 : 'KW' '727';\r\n" +
	                  "KW728 : 'KW' '728';\r\n" +
	                  "KW729 : 'KW' '729';\r\n" +
	                  "KW730 : 'KW' '730';\r\n" +
	                  "KW731 : 'KW' '731';\r\n" +
	                  "KW732 : 'KW' '732';\r\n" +
	                  "KW733 : 'KW' '733';\r\n" +
	                  "KW734 : 'KW' '734';\r\n" +
	                  "KW735 : 'KW' '735';\r\n" +
	                  "KW736 : 'KW' '736';\r\n" +
	                  "KW737 : 'KW' '737';\r\n" +
	                  "KW738 : 'KW' '738';\r\n" +
	                  "KW739 : 'KW' '739';\r\n" +
	                  "KW740 : 'KW' '740';\r\n" +
	                  "KW741 : 'KW' '741';\r\n" +
	                  "KW742 : 'KW' '742';\r\n" +
	                  "KW743 : 'KW' '743';\r\n" +
	                  "KW744 : 'KW' '744';\r\n" +
	                  "KW745 : 'KW' '745';\r\n" +
	                  "KW746 : 'KW' '746';\r\n" +
	                  "KW747 : 'KW' '747';\r\n" +
	                  "KW748 : 'KW' '748';\r\n" +
	                  "KW749 : 'KW' '749';\r\n" +
	                  "KW750 : 'KW' '750';\r\n" +
	                  "KW751 : 'KW' '751';\r\n" +
	                  "KW752 : 'KW' '752';\r\n" +
	                  "KW753 : 'KW' '753';\r\n" +
	                  "KW754 : 'KW' '754';\r\n" +
	                  "KW755 : 'KW' '755';\r\n" +
	                  "KW756 : 'KW' '756';\r\n" +
	                  "KW757 : 'KW' '757';\r\n" +
	                  "KW758 : 'KW' '758';\r\n" +
	                  "KW759 : 'KW' '759';\r\n" +
	                  "KW760 : 'KW' '760';\r\n" +
	                  "KW761 : 'KW' '761';\r\n" +
	                  "KW762 : 'KW' '762';\r\n" +
	                  "KW763 : 'KW' '763';\r\n" +
	                  "KW764 : 'KW' '764';\r\n" +
	                  "KW765 : 'KW' '765';\r\n" +
	                  "KW766 : 'KW' '766';\r\n" +
	                  "KW767 : 'KW' '767';\r\n" +
	                  "KW768 : 'KW' '768';\r\n" +
	                  "KW769 : 'KW' '769';\r\n" +
	                  "KW770 : 'KW' '770';\r\n" +
	                  "KW771 : 'KW' '771';\r\n" +
	                  "KW772 : 'KW' '772';\r\n" +
	                  "KW773 : 'KW' '773';\r\n" +
	                  "KW774 : 'KW' '774';\r\n" +
	                  "KW775 : 'KW' '775';\r\n" +
	                  "KW776 : 'KW' '776';\r\n" +
	                  "KW777 : 'KW' '777';\r\n" +
	                  "KW778 : 'KW' '778';\r\n" +
	                  "KW779 : 'KW' '779';\r\n" +
	                  "KW780 : 'KW' '780';\r\n" +
	                  "KW781 : 'KW' '781';\r\n" +
	                  "KW782 : 'KW' '782';\r\n" +
	                  "KW783 : 'KW' '783';\r\n" +
	                  "KW784 : 'KW' '784';\r\n" +
	                  "KW785 : 'KW' '785';\r\n" +
	                  "KW786 : 'KW' '786';\r\n" +
	                  "KW787 : 'KW' '787';\r\n" +
	                  "KW788 : 'KW' '788';\r\n" +
	                  "KW789 : 'KW' '789';\r\n" +
	                  "KW790 : 'KW' '790';\r\n" +
	                  "KW791 : 'KW' '791';\r\n" +
	                  "KW792 : 'KW' '792';\r\n" +
	                  "KW793 : 'KW' '793';\r\n" +
	                  "KW794 : 'KW' '794';\r\n" +
	                  "KW795 : 'KW' '795';\r\n" +
	                  "KW796 : 'KW' '796';\r\n" +
	                  "KW797 : 'KW' '797';\r\n" +
	                  "KW798 : 'KW' '798';\r\n" +
	                  "KW799 : 'KW' '799';\r\n" +
	                  "KW800 : 'KW' '800';\r\n" +
	                  "KW801 : 'KW' '801';\r\n" +
	                  "KW802 : 'KW' '802';\r\n" +
	                  "KW803 : 'KW' '803';\r\n" +
	                  "KW804 : 'KW' '804';\r\n" +
	                  "KW805 : 'KW' '805';\r\n" +
	                  "KW806 : 'KW' '806';\r\n" +
	                  "KW807 : 'KW' '807';\r\n" +
	                  "KW808 : 'KW' '808';\r\n" +
	                  "KW809 : 'KW' '809';\r\n" +
	                  "KW810 : 'KW' '810';\r\n" +
	                  "KW811 : 'KW' '811';\r\n" +
	                  "KW812 : 'KW' '812';\r\n" +
	                  "KW813 : 'KW' '813';\r\n" +
	                  "KW814 : 'KW' '814';\r\n" +
	                  "KW815 : 'KW' '815';\r\n" +
	                  "KW816 : 'KW' '816';\r\n" +
	                  "KW817 : 'KW' '817';\r\n" +
	                  "KW818 : 'KW' '818';\r\n" +
	                  "KW819 : 'KW' '819';\r\n" +
	                  "KW820 : 'KW' '820';\r\n" +
	                  "KW821 : 'KW' '821';\r\n" +
	                  "KW822 : 'KW' '822';\r\n" +
	                  "KW823 : 'KW' '823';\r\n" +
	                  "KW824 : 'KW' '824';\r\n" +
	                  "KW825 : 'KW' '825';\r\n" +
	                  "KW826 : 'KW' '826';\r\n" +
	                  "KW827 : 'KW' '827';\r\n" +
	                  "KW828 : 'KW' '828';\r\n" +
	                  "KW829 : 'KW' '829';\r\n" +
	                  "KW830 : 'KW' '830';\r\n" +
	                  "KW831 : 'KW' '831';\r\n" +
	                  "KW832 : 'KW' '832';\r\n" +
	                  "KW833 : 'KW' '833';\r\n" +
	                  "KW834 : 'KW' '834';\r\n" +
	                  "KW835 : 'KW' '835';\r\n" +
	                  "KW836 : 'KW' '836';\r\n" +
	                  "KW837 : 'KW' '837';\r\n" +
	                  "KW838 : 'KW' '838';\r\n" +
	                  "KW839 : 'KW' '839';\r\n" +
	                  "KW840 : 'KW' '840';\r\n" +
	                  "KW841 : 'KW' '841';\r\n" +
	                  "KW842 : 'KW' '842';\r\n" +
	                  "KW843 : 'KW' '843';\r\n" +
	                  "KW844 : 'KW' '844';\r\n" +
	                  "KW845 : 'KW' '845';\r\n" +
	                  "KW846 : 'KW' '846';\r\n" +
	                  "KW847 : 'KW' '847';\r\n" +
	                  "KW848 : 'KW' '848';\r\n" +
	                  "KW849 : 'KW' '849';\r\n" +
	                  "KW850 : 'KW' '850';\r\n" +
	                  "KW851 : 'KW' '851';\r\n" +
	                  "KW852 : 'KW' '852';\r\n" +
	                  "KW853 : 'KW' '853';\r\n" +
	                  "KW854 : 'KW' '854';\r\n" +
	                  "KW855 : 'KW' '855';\r\n" +
	                  "KW856 : 'KW' '856';\r\n" +
	                  "KW857 : 'KW' '857';\r\n" +
	                  "KW858 : 'KW' '858';\r\n" +
	                  "KW859 : 'KW' '859';\r\n" +
	                  "KW860 : 'KW' '860';\r\n" +
	                  "KW861 : 'KW' '861';\r\n" +
	                  "KW862 : 'KW' '862';\r\n" +
	                  "KW863 : 'KW' '863';\r\n" +
	                  "KW864 : 'KW' '864';\r\n" +
	                  "KW865 : 'KW' '865';\r\n" +
	                  "KW866 : 'KW' '866';\r\n" +
	                  "KW867 : 'KW' '867';\r\n" +
	                  "KW868 : 'KW' '868';\r\n" +
	                  "KW869 : 'KW' '869';\r\n" +
	                  "KW870 : 'KW' '870';\r\n" +
	                  "KW871 : 'KW' '871';\r\n" +
	                  "KW872 : 'KW' '872';\r\n" +
	                  "KW873 : 'KW' '873';\r\n" +
	                  "KW874 : 'KW' '874';\r\n" +
	                  "KW875 : 'KW' '875';\r\n" +
	                  "KW876 : 'KW' '876';\r\n" +
	                  "KW877 : 'KW' '877';\r\n" +
	                  "KW878 : 'KW' '878';\r\n" +
	                  "KW879 : 'KW' '879';\r\n" +
	                  "KW880 : 'KW' '880';\r\n" +
	                  "KW881 : 'KW' '881';\r\n" +
	                  "KW882 : 'KW' '882';\r\n" +
	                  "KW883 : 'KW' '883';\r\n" +
	                  "KW884 : 'KW' '884';\r\n" +
	                  "KW885 : 'KW' '885';\r\n" +
	                  "KW886 : 'KW' '886';\r\n" +
	                  "KW887 : 'KW' '887';\r\n" +
	                  "KW888 : 'KW' '888';\r\n" +
	                  "KW889 : 'KW' '889';\r\n" +
	                  "KW890 : 'KW' '890';\r\n" +
	                  "KW891 : 'KW' '891';\r\n" +
	                  "KW892 : 'KW' '892';\r\n" +
	                  "KW893 : 'KW' '893';\r\n" +
	                  "KW894 : 'KW' '894';\r\n" +
	                  "KW895 : 'KW' '895';\r\n" +
	                  "KW896 : 'KW' '896';\r\n" +
	                  "KW897 : 'KW' '897';\r\n" +
	                  "KW898 : 'KW' '898';\r\n" +
	                  "KW899 : 'KW' '899';\r\n" +
	                  "KW900 : 'KW' '900';\r\n" +
	                  "KW901 : 'KW' '901';\r\n" +
	                  "KW902 : 'KW' '902';\r\n" +
	                  "KW903 : 'KW' '903';\r\n" +
	                  "KW904 : 'KW' '904';\r\n" +
	                  "KW905 : 'KW' '905';\r\n" +
	                  "KW906 : 'KW' '906';\r\n" +
	                  "KW907 : 'KW' '907';\r\n" +
	                  "KW908 : 'KW' '908';\r\n" +
	                  "KW909 : 'KW' '909';\r\n" +
	                  "KW910 : 'KW' '910';\r\n" +
	                  "KW911 : 'KW' '911';\r\n" +
	                  "KW912 : 'KW' '912';\r\n" +
	                  "KW913 : 'KW' '913';\r\n" +
	                  "KW914 : 'KW' '914';\r\n" +
	                  "KW915 : 'KW' '915';\r\n" +
	                  "KW916 : 'KW' '916';\r\n" +
	                  "KW917 : 'KW' '917';\r\n" +
	                  "KW918 : 'KW' '918';\r\n" +
	                  "KW919 : 'KW' '919';\r\n" +
	                  "KW920 : 'KW' '920';\r\n" +
	                  "KW921 : 'KW' '921';\r\n" +
	                  "KW922 : 'KW' '922';\r\n" +
	                  "KW923 : 'KW' '923';\r\n" +
	                  "KW924 : 'KW' '924';\r\n" +
	                  "KW925 : 'KW' '925';\r\n" +
	                  "KW926 : 'KW' '926';\r\n" +
	                  "KW927 : 'KW' '927';\r\n" +
	                  "KW928 : 'KW' '928';\r\n" +
	                  "KW929 : 'KW' '929';\r\n" +
	                  "KW930 : 'KW' '930';\r\n" +
	                  "KW931 : 'KW' '931';\r\n" +
	                  "KW932 : 'KW' '932';\r\n" +
	                  "KW933 : 'KW' '933';\r\n" +
	                  "KW934 : 'KW' '934';\r\n" +
	                  "KW935 : 'KW' '935';\r\n" +
	                  "KW936 : 'KW' '936';\r\n" +
	                  "KW937 : 'KW' '937';\r\n" +
	                  "KW938 : 'KW' '938';\r\n" +
	                  "KW939 : 'KW' '939';\r\n" +
	                  "KW940 : 'KW' '940';\r\n" +
	                  "KW941 : 'KW' '941';\r\n" +
	                  "KW942 : 'KW' '942';\r\n" +
	                  "KW943 : 'KW' '943';\r\n" +
	                  "KW944 : 'KW' '944';\r\n" +
	                  "KW945 : 'KW' '945';\r\n" +
	                  "KW946 : 'KW' '946';\r\n" +
	                  "KW947 : 'KW' '947';\r\n" +
	                  "KW948 : 'KW' '948';\r\n" +
	                  "KW949 : 'KW' '949';\r\n" +
	                  "KW950 : 'KW' '950';\r\n" +
	                  "KW951 : 'KW' '951';\r\n" +
	                  "KW952 : 'KW' '952';\r\n" +
	                  "KW953 : 'KW' '953';\r\n" +
	                  "KW954 : 'KW' '954';\r\n" +
	                  "KW955 : 'KW' '955';\r\n" +
	                  "KW956 : 'KW' '956';\r\n" +
	                  "KW957 : 'KW' '957';\r\n" +
	                  "KW958 : 'KW' '958';\r\n" +
	                  "KW959 : 'KW' '959';\r\n" +
	                  "KW960 : 'KW' '960';\r\n" +
	                  "KW961 : 'KW' '961';\r\n" +
	                  "KW962 : 'KW' '962';\r\n" +
	                  "KW963 : 'KW' '963';\r\n" +
	                  "KW964 : 'KW' '964';\r\n" +
	                  "KW965 : 'KW' '965';\r\n" +
	                  "KW966 : 'KW' '966';\r\n" +
	                  "KW967 : 'KW' '967';\r\n" +
	                  "KW968 : 'KW' '968';\r\n" +
	                  "KW969 : 'KW' '969';\r\n" +
	                  "KW970 : 'KW' '970';\r\n" +
	                  "KW971 : 'KW' '971';\r\n" +
	                  "KW972 : 'KW' '972';\r\n" +
	                  "KW973 : 'KW' '973';\r\n" +
	                  "KW974 : 'KW' '974';\r\n" +
	                  "KW975 : 'KW' '975';\r\n" +
	                  "KW976 : 'KW' '976';\r\n" +
	                  "KW977 : 'KW' '977';\r\n" +
	                  "KW978 : 'KW' '978';\r\n" +
	                  "KW979 : 'KW' '979';\r\n" +
	                  "KW980 : 'KW' '980';\r\n" +
	                  "KW981 : 'KW' '981';\r\n" +
	                  "KW982 : 'KW' '982';\r\n" +
	                  "KW983 : 'KW' '983';\r\n" +
	                  "KW984 : 'KW' '984';\r\n" +
	                  "KW985 : 'KW' '985';\r\n" +
	                  "KW986 : 'KW' '986';\r\n" +
	                  "KW987 : 'KW' '987';\r\n" +
	                  "KW988 : 'KW' '988';\r\n" +
	                  "KW989 : 'KW' '989';\r\n" +
	                  "KW990 : 'KW' '990';\r\n" +
	                  "KW991 : 'KW' '991';\r\n" +
	                  "KW992 : 'KW' '992';\r\n" +
	                  "KW993 : 'KW' '993';\r\n" +
	                  "KW994 : 'KW' '994';\r\n" +
	                  "KW995 : 'KW' '995';\r\n" +
	                  "KW996 : 'KW' '996';\r\n" +
	                  "KW997 : 'KW' '997';\r\n" +
	                  "KW998 : 'KW' '998';\r\n" +
	                  "KW999 : 'KW' '999';\r\n" +
	                  "KW1000 : 'KW' '1000';\r\n" +
	                  "KW1001 : 'KW' '1001';\r\n" +
	                  "KW1002 : 'KW' '1002';\r\n" +
	                  "KW1003 : 'KW' '1003';\r\n" +
	                  "KW1004 : 'KW' '1004';\r\n" +
	                  "KW1005 : 'KW' '1005';\r\n" +
	                  "KW1006 : 'KW' '1006';\r\n" +
	                  "KW1007 : 'KW' '1007';\r\n" +
	                  "KW1008 : 'KW' '1008';\r\n" +
	                  "KW1009 : 'KW' '1009';\r\n" +
	                  "KW1010 : 'KW' '1010';\r\n" +
	                  "KW1011 : 'KW' '1011';\r\n" +
	                  "KW1012 : 'KW' '1012';\r\n" +
	                  "KW1013 : 'KW' '1013';\r\n" +
	                  "KW1014 : 'KW' '1014';\r\n" +
	                  "KW1015 : 'KW' '1015';\r\n" +
	                  "KW1016 : 'KW' '1016';\r\n" +
	                  "KW1017 : 'KW' '1017';\r\n" +
	                  "KW1018 : 'KW' '1018';\r\n" +
	                  "KW1019 : 'KW' '1019';\r\n" +
	                  "KW1020 : 'KW' '1020';\r\n" +
	                  "KW1021 : 'KW' '1021';\r\n" +
	                  "KW1022 : 'KW' '1022';\r\n" +
	                  "KW1023 : 'KW' '1023';\r\n" +
	                  "KW1024 : 'KW' '1024';\r\n" +
	                  "KW1025 : 'KW' '1025';\r\n" +
	                  "KW1026 : 'KW' '1026';\r\n" +
	                  "KW1027 : 'KW' '1027';\r\n" +
	                  "KW1028 : 'KW' '1028';\r\n" +
	                  "KW1029 : 'KW' '1029';\r\n" +
	                  "KW1030 : 'KW' '1030';\r\n" +
	                  "KW1031 : 'KW' '1031';\r\n" +
	                  "KW1032 : 'KW' '1032';\r\n" +
	                  "KW1033 : 'KW' '1033';\r\n" +
	                  "KW1034 : 'KW' '1034';\r\n" +
	                  "KW1035 : 'KW' '1035';\r\n" +
	                  "KW1036 : 'KW' '1036';\r\n" +
	                  "KW1037 : 'KW' '1037';\r\n" +
	                  "KW1038 : 'KW' '1038';\r\n" +
	                  "KW1039 : 'KW' '1039';\r\n" +
	                  "KW1040 : 'KW' '1040';\r\n" +
	                  "KW1041 : 'KW' '1041';\r\n" +
	                  "KW1042 : 'KW' '1042';\r\n" +
	                  "KW1043 : 'KW' '1043';\r\n" +
	                  "KW1044 : 'KW' '1044';\r\n" +
	                  "KW1045 : 'KW' '1045';\r\n" +
	                  "KW1046 : 'KW' '1046';\r\n" +
	                  "KW1047 : 'KW' '1047';\r\n" +
	                  "KW1048 : 'KW' '1048';\r\n" +
	                  "KW1049 : 'KW' '1049';\r\n" +
	                  "KW1050 : 'KW' '1050';\r\n" +
	                  "KW1051 : 'KW' '1051';\r\n" +
	                  "KW1052 : 'KW' '1052';\r\n" +
	                  "KW1053 : 'KW' '1053';\r\n" +
	                  "KW1054 : 'KW' '1054';\r\n" +
	                  "KW1055 : 'KW' '1055';\r\n" +
	                  "KW1056 : 'KW' '1056';\r\n" +
	                  "KW1057 : 'KW' '1057';\r\n" +
	                  "KW1058 : 'KW' '1058';\r\n" +
	                  "KW1059 : 'KW' '1059';\r\n" +
	                  "KW1060 : 'KW' '1060';\r\n" +
	                  "KW1061 : 'KW' '1061';\r\n" +
	                  "KW1062 : 'KW' '1062';\r\n" +
	                  "KW1063 : 'KW' '1063';\r\n" +
	                  "KW1064 : 'KW' '1064';\r\n" +
	                  "KW1065 : 'KW' '1065';\r\n" +
	                  "KW1066 : 'KW' '1066';\r\n" +
	                  "KW1067 : 'KW' '1067';\r\n" +
	                  "KW1068 : 'KW' '1068';\r\n" +
	                  "KW1069 : 'KW' '1069';\r\n" +
	                  "KW1070 : 'KW' '1070';\r\n" +
	                  "KW1071 : 'KW' '1071';\r\n" +
	                  "KW1072 : 'KW' '1072';\r\n" +
	                  "KW1073 : 'KW' '1073';\r\n" +
	                  "KW1074 : 'KW' '1074';\r\n" +
	                  "KW1075 : 'KW' '1075';\r\n" +
	                  "KW1076 : 'KW' '1076';\r\n" +
	                  "KW1077 : 'KW' '1077';\r\n" +
	                  "KW1078 : 'KW' '1078';\r\n" +
	                  "KW1079 : 'KW' '1079';\r\n" +
	                  "KW1080 : 'KW' '1080';\r\n" +
	                  "KW1081 : 'KW' '1081';\r\n" +
	                  "KW1082 : 'KW' '1082';\r\n" +
	                  "KW1083 : 'KW' '1083';\r\n" +
	                  "KW1084 : 'KW' '1084';\r\n" +
	                  "KW1085 : 'KW' '1085';\r\n" +
	                  "KW1086 : 'KW' '1086';\r\n" +
	                  "KW1087 : 'KW' '1087';\r\n" +
	                  "KW1088 : 'KW' '1088';\r\n" +
	                  "KW1089 : 'KW' '1089';\r\n" +
	                  "KW1090 : 'KW' '1090';\r\n" +
	                  "KW1091 : 'KW' '1091';\r\n" +
	                  "KW1092 : 'KW' '1092';\r\n" +
	                  "KW1093 : 'KW' '1093';\r\n" +
	                  "KW1094 : 'KW' '1094';\r\n" +
	                  "KW1095 : 'KW' '1095';\r\n" +
	                  "KW1096 : 'KW' '1096';\r\n" +
	                  "KW1097 : 'KW' '1097';\r\n" +
	                  "KW1098 : 'KW' '1098';\r\n" +
	                  "KW1099 : 'KW' '1099';\r\n" +
	                  "KW1100 : 'KW' '1100';\r\n" +
	                  "KW1101 : 'KW' '1101';\r\n" +
	                  "KW1102 : 'KW' '1102';\r\n" +
	                  "KW1103 : 'KW' '1103';\r\n" +
	                  "KW1104 : 'KW' '1104';\r\n" +
	                  "KW1105 : 'KW' '1105';\r\n" +
	                  "KW1106 : 'KW' '1106';\r\n" +
	                  "KW1107 : 'KW' '1107';\r\n" +
	                  "KW1108 : 'KW' '1108';\r\n" +
	                  "KW1109 : 'KW' '1109';\r\n" +
	                  "KW1110 : 'KW' '1110';\r\n" +
	                  "KW1111 : 'KW' '1111';\r\n" +
	                  "KW1112 : 'KW' '1112';\r\n" +
	                  "KW1113 : 'KW' '1113';\r\n" +
	                  "KW1114 : 'KW' '1114';\r\n" +
	                  "KW1115 : 'KW' '1115';\r\n" +
	                  "KW1116 : 'KW' '1116';\r\n" +
	                  "KW1117 : 'KW' '1117';\r\n" +
	                  "KW1118 : 'KW' '1118';\r\n" +
	                  "KW1119 : 'KW' '1119';\r\n" +
	                  "KW1120 : 'KW' '1120';\r\n" +
	                  "KW1121 : 'KW' '1121';\r\n" +
	                  "KW1122 : 'KW' '1122';\r\n" +
	                  "KW1123 : 'KW' '1123';\r\n" +
	                  "KW1124 : 'KW' '1124';\r\n" +
	                  "KW1125 : 'KW' '1125';\r\n" +
	                  "KW1126 : 'KW' '1126';\r\n" +
	                  "KW1127 : 'KW' '1127';\r\n" +
	                  "KW1128 : 'KW' '1128';\r\n" +
	                  "KW1129 : 'KW' '1129';\r\n" +
	                  "KW1130 : 'KW' '1130';\r\n" +
	                  "KW1131 : 'KW' '1131';\r\n" +
	                  "KW1132 : 'KW' '1132';\r\n" +
	                  "KW1133 : 'KW' '1133';\r\n" +
	                  "KW1134 : 'KW' '1134';\r\n" +
	                  "KW1135 : 'KW' '1135';\r\n" +
	                  "KW1136 : 'KW' '1136';\r\n" +
	                  "KW1137 : 'KW' '1137';\r\n" +
	                  "KW1138 : 'KW' '1138';\r\n" +
	                  "KW1139 : 'KW' '1139';\r\n" +
	                  "KW1140 : 'KW' '1140';\r\n" +
	                  "KW1141 : 'KW' '1141';\r\n" +
	                  "KW1142 : 'KW' '1142';\r\n" +
	                  "KW1143 : 'KW' '1143';\r\n" +
	                  "KW1144 : 'KW' '1144';\r\n" +
	                  "KW1145 : 'KW' '1145';\r\n" +
	                  "KW1146 : 'KW' '1146';\r\n" +
	                  "KW1147 : 'KW' '1147';\r\n" +
	                  "KW1148 : 'KW' '1148';\r\n" +
	                  "KW1149 : 'KW' '1149';\r\n" +
	                  "KW1150 : 'KW' '1150';\r\n" +
	                  "KW1151 : 'KW' '1151';\r\n" +
	                  "KW1152 : 'KW' '1152';\r\n" +
	                  "KW1153 : 'KW' '1153';\r\n" +
	                  "KW1154 : 'KW' '1154';\r\n" +
	                  "KW1155 : 'KW' '1155';\r\n" +
	                  "KW1156 : 'KW' '1156';\r\n" +
	                  "KW1157 : 'KW' '1157';\r\n" +
	                  "KW1158 : 'KW' '1158';\r\n" +
	                  "KW1159 : 'KW' '1159';\r\n" +
	                  "KW1160 : 'KW' '1160';\r\n" +
	                  "KW1161 : 'KW' '1161';\r\n" +
	                  "KW1162 : 'KW' '1162';\r\n" +
	                  "KW1163 : 'KW' '1163';\r\n" +
	                  "KW1164 : 'KW' '1164';\r\n" +
	                  "KW1165 : 'KW' '1165';\r\n" +
	                  "KW1166 : 'KW' '1166';\r\n" +
	                  "KW1167 : 'KW' '1167';\r\n" +
	                  "KW1168 : 'KW' '1168';\r\n" +
	                  "KW1169 : 'KW' '1169';\r\n" +
	                  "KW1170 : 'KW' '1170';\r\n" +
	                  "KW1171 : 'KW' '1171';\r\n" +
	                  "KW1172 : 'KW' '1172';\r\n" +
	                  "KW1173 : 'KW' '1173';\r\n" +
	                  "KW1174 : 'KW' '1174';\r\n" +
	                  "KW1175 : 'KW' '1175';\r\n" +
	                  "KW1176 : 'KW' '1176';\r\n" +
	                  "KW1177 : 'KW' '1177';\r\n" +
	                  "KW1178 : 'KW' '1178';\r\n" +
	                  "KW1179 : 'KW' '1179';\r\n" +
	                  "KW1180 : 'KW' '1180';\r\n" +
	                  "KW1181 : 'KW' '1181';\r\n" +
	                  "KW1182 : 'KW' '1182';\r\n" +
	                  "KW1183 : 'KW' '1183';\r\n" +
	                  "KW1184 : 'KW' '1184';\r\n" +
	                  "KW1185 : 'KW' '1185';\r\n" +
	                  "KW1186 : 'KW' '1186';\r\n" +
	                  "KW1187 : 'KW' '1187';\r\n" +
	                  "KW1188 : 'KW' '1188';\r\n" +
	                  "KW1189 : 'KW' '1189';\r\n" +
	                  "KW1190 : 'KW' '1190';\r\n" +
	                  "KW1191 : 'KW' '1191';\r\n" +
	                  "KW1192 : 'KW' '1192';\r\n" +
	                  "KW1193 : 'KW' '1193';\r\n" +
	                  "KW1194 : 'KW' '1194';\r\n" +
	                  "KW1195 : 'KW' '1195';\r\n" +
	                  "KW1196 : 'KW' '1196';\r\n" +
	                  "KW1197 : 'KW' '1197';\r\n" +
	                  "KW1198 : 'KW' '1198';\r\n" +
	                  "KW1199 : 'KW' '1199';\r\n" +
	                  "KW1200 : 'KW' '1200';\r\n" +
	                  "KW1201 : 'KW' '1201';\r\n" +
	                  "KW1202 : 'KW' '1202';\r\n" +
	                  "KW1203 : 'KW' '1203';\r\n" +
	                  "KW1204 : 'KW' '1204';\r\n" +
	                  "KW1205 : 'KW' '1205';\r\n" +
	                  "KW1206 : 'KW' '1206';\r\n" +
	                  "KW1207 : 'KW' '1207';\r\n" +
	                  "KW1208 : 'KW' '1208';\r\n" +
	                  "KW1209 : 'KW' '1209';\r\n" +
	                  "KW1210 : 'KW' '1210';\r\n" +
	                  "KW1211 : 'KW' '1211';\r\n" +
	                  "KW1212 : 'KW' '1212';\r\n" +
	                  "KW1213 : 'KW' '1213';\r\n" +
	                  "KW1214 : 'KW' '1214';\r\n" +
	                  "KW1215 : 'KW' '1215';\r\n" +
	                  "KW1216 : 'KW' '1216';\r\n" +
	                  "KW1217 : 'KW' '1217';\r\n" +
	                  "KW1218 : 'KW' '1218';\r\n" +
	                  "KW1219 : 'KW' '1219';\r\n" +
	                  "KW1220 : 'KW' '1220';\r\n" +
	                  "KW1221 : 'KW' '1221';\r\n" +
	                  "KW1222 : 'KW' '1222';\r\n" +
	                  "KW1223 : 'KW' '1223';\r\n" +
	                  "KW1224 : 'KW' '1224';\r\n" +
	                  "KW1225 : 'KW' '1225';\r\n" +
	                  "KW1226 : 'KW' '1226';\r\n" +
	                  "KW1227 : 'KW' '1227';\r\n" +
	                  "KW1228 : 'KW' '1228';\r\n" +
	                  "KW1229 : 'KW' '1229';\r\n" +
	                  "KW1230 : 'KW' '1230';\r\n" +
	                  "KW1231 : 'KW' '1231';\r\n" +
	                  "KW1232 : 'KW' '1232';\r\n" +
	                  "KW1233 : 'KW' '1233';\r\n" +
	                  "KW1234 : 'KW' '1234';\r\n" +
	                  "KW1235 : 'KW' '1235';\r\n" +
	                  "KW1236 : 'KW' '1236';\r\n" +
	                  "KW1237 : 'KW' '1237';\r\n" +
	                  "KW1238 : 'KW' '1238';\r\n" +
	                  "KW1239 : 'KW' '1239';\r\n" +
	                  "KW1240 : 'KW' '1240';\r\n" +
	                  "KW1241 : 'KW' '1241';\r\n" +
	                  "KW1242 : 'KW' '1242';\r\n" +
	                  "KW1243 : 'KW' '1243';\r\n" +
	                  "KW1244 : 'KW' '1244';\r\n" +
	                  "KW1245 : 'KW' '1245';\r\n" +
	                  "KW1246 : 'KW' '1246';\r\n" +
	                  "KW1247 : 'KW' '1247';\r\n" +
	                  "KW1248 : 'KW' '1248';\r\n" +
	                  "KW1249 : 'KW' '1249';\r\n" +
	                  "KW1250 : 'KW' '1250';\r\n" +
	                  "KW1251 : 'KW' '1251';\r\n" +
	                  "KW1252 : 'KW' '1252';\r\n" +
	                  "KW1253 : 'KW' '1253';\r\n" +
	                  "KW1254 : 'KW' '1254';\r\n" +
	                  "KW1255 : 'KW' '1255';\r\n" +
	                  "KW1256 : 'KW' '1256';\r\n" +
	                  "KW1257 : 'KW' '1257';\r\n" +
	                  "KW1258 : 'KW' '1258';\r\n" +
	                  "KW1259 : 'KW' '1259';\r\n" +
	                  "KW1260 : 'KW' '1260';\r\n" +
	                  "KW1261 : 'KW' '1261';\r\n" +
	                  "KW1262 : 'KW' '1262';\r\n" +
	                  "KW1263 : 'KW' '1263';\r\n" +
	                  "KW1264 : 'KW' '1264';\r\n" +
	                  "KW1265 : 'KW' '1265';\r\n" +
	                  "KW1266 : 'KW' '1266';\r\n" +
	                  "KW1267 : 'KW' '1267';\r\n" +
	                  "KW1268 : 'KW' '1268';\r\n" +
	                  "KW1269 : 'KW' '1269';\r\n" +
	                  "KW1270 : 'KW' '1270';\r\n" +
	                  "KW1271 : 'KW' '1271';\r\n" +
	                  "KW1272 : 'KW' '1272';\r\n" +
	                  "KW1273 : 'KW' '1273';\r\n" +
	                  "KW1274 : 'KW' '1274';\r\n" +
	                  "KW1275 : 'KW' '1275';\r\n" +
	                  "KW1276 : 'KW' '1276';\r\n" +
	                  "KW1277 : 'KW' '1277';\r\n" +
	                  "KW1278 : 'KW' '1278';\r\n" +
	                  "KW1279 : 'KW' '1279';\r\n" +
	                  "KW1280 : 'KW' '1280';\r\n" +
	                  "KW1281 : 'KW' '1281';\r\n" +
	                  "KW1282 : 'KW' '1282';\r\n" +
	                  "KW1283 : 'KW' '1283';\r\n" +
	                  "KW1284 : 'KW' '1284';\r\n" +
	                  "KW1285 : 'KW' '1285';\r\n" +
	                  "KW1286 : 'KW' '1286';\r\n" +
	                  "KW1287 : 'KW' '1287';\r\n" +
	                  "KW1288 : 'KW' '1288';\r\n" +
	                  "KW1289 : 'KW' '1289';\r\n" +
	                  "KW1290 : 'KW' '1290';\r\n" +
	                  "KW1291 : 'KW' '1291';\r\n" +
	                  "KW1292 : 'KW' '1292';\r\n" +
	                  "KW1293 : 'KW' '1293';\r\n" +
	                  "KW1294 : 'KW' '1294';\r\n" +
	                  "KW1295 : 'KW' '1295';\r\n" +
	                  "KW1296 : 'KW' '1296';\r\n" +
	                  "KW1297 : 'KW' '1297';\r\n" +
	                  "KW1298 : 'KW' '1298';\r\n" +
	                  "KW1299 : 'KW' '1299';\r\n" +
	                  "KW1300 : 'KW' '1300';\r\n" +
	                  "KW1301 : 'KW' '1301';\r\n" +
	                  "KW1302 : 'KW' '1302';\r\n" +
	                  "KW1303 : 'KW' '1303';\r\n" +
	                  "KW1304 : 'KW' '1304';\r\n" +
	                  "KW1305 : 'KW' '1305';\r\n" +
	                  "KW1306 : 'KW' '1306';\r\n" +
	                  "KW1307 : 'KW' '1307';\r\n" +
	                  "KW1308 : 'KW' '1308';\r\n" +
	                  "KW1309 : 'KW' '1309';\r\n" +
	                  "KW1310 : 'KW' '1310';\r\n" +
	                  "KW1311 : 'KW' '1311';\r\n" +
	                  "KW1312 : 'KW' '1312';\r\n" +
	                  "KW1313 : 'KW' '1313';\r\n" +
	                  "KW1314 : 'KW' '1314';\r\n" +
	                  "KW1315 : 'KW' '1315';\r\n" +
	                  "KW1316 : 'KW' '1316';\r\n" +
	                  "KW1317 : 'KW' '1317';\r\n" +
	                  "KW1318 : 'KW' '1318';\r\n" +
	                  "KW1319 : 'KW' '1319';\r\n" +
	                  "KW1320 : 'KW' '1320';\r\n" +
	                  "KW1321 : 'KW' '1321';\r\n" +
	                  "KW1322 : 'KW' '1322';\r\n" +
	                  "KW1323 : 'KW' '1323';\r\n" +
	                  "KW1324 : 'KW' '1324';\r\n" +
	                  "KW1325 : 'KW' '1325';\r\n" +
	                  "KW1326 : 'KW' '1326';\r\n" +
	                  "KW1327 : 'KW' '1327';\r\n" +
	                  "KW1328 : 'KW' '1328';\r\n" +
	                  "KW1329 : 'KW' '1329';\r\n" +
	                  "KW1330 : 'KW' '1330';\r\n" +
	                  "KW1331 : 'KW' '1331';\r\n" +
	                  "KW1332 : 'KW' '1332';\r\n" +
	                  "KW1333 : 'KW' '1333';\r\n" +
	                  "KW1334 : 'KW' '1334';\r\n" +
	                  "KW1335 : 'KW' '1335';\r\n" +
	                  "KW1336 : 'KW' '1336';\r\n" +
	                  "KW1337 : 'KW' '1337';\r\n" +
	                  "KW1338 : 'KW' '1338';\r\n" +
	                  "KW1339 : 'KW' '1339';\r\n" +
	                  "KW1340 : 'KW' '1340';\r\n" +
	                  "KW1341 : 'KW' '1341';\r\n" +
	                  "KW1342 : 'KW' '1342';\r\n" +
	                  "KW1343 : 'KW' '1343';\r\n" +
	                  "KW1344 : 'KW' '1344';\r\n" +
	                  "KW1345 : 'KW' '1345';\r\n" +
	                  "KW1346 : 'KW' '1346';\r\n" +
	                  "KW1347 : 'KW' '1347';\r\n" +
	                  "KW1348 : 'KW' '1348';\r\n" +
	                  "KW1349 : 'KW' '1349';\r\n" +
	                  "KW1350 : 'KW' '1350';\r\n" +
	                  "KW1351 : 'KW' '1351';\r\n" +
	                  "KW1352 : 'KW' '1352';\r\n" +
	                  "KW1353 : 'KW' '1353';\r\n" +
	                  "KW1354 : 'KW' '1354';\r\n" +
	                  "KW1355 : 'KW' '1355';\r\n" +
	                  "KW1356 : 'KW' '1356';\r\n" +
	                  "KW1357 : 'KW' '1357';\r\n" +
	                  "KW1358 : 'KW' '1358';\r\n" +
	                  "KW1359 : 'KW' '1359';\r\n" +
	                  "KW1360 : 'KW' '1360';\r\n" +
	                  "KW1361 : 'KW' '1361';\r\n" +
	                  "KW1362 : 'KW' '1362';\r\n" +
	                  "KW1363 : 'KW' '1363';\r\n" +
	                  "KW1364 : 'KW' '1364';\r\n" +
	                  "KW1365 : 'KW' '1365';\r\n" +
	                  "KW1366 : 'KW' '1366';\r\n" +
	                  "KW1367 : 'KW' '1367';\r\n" +
	                  "KW1368 : 'KW' '1368';\r\n" +
	                  "KW1369 : 'KW' '1369';\r\n" +
	                  "KW1370 : 'KW' '1370';\r\n" +
	                  "KW1371 : 'KW' '1371';\r\n" +
	                  "KW1372 : 'KW' '1372';\r\n" +
	                  "KW1373 : 'KW' '1373';\r\n" +
	                  "KW1374 : 'KW' '1374';\r\n" +
	                  "KW1375 : 'KW' '1375';\r\n" +
	                  "KW1376 : 'KW' '1376';\r\n" +
	                  "KW1377 : 'KW' '1377';\r\n" +
	                  "KW1378 : 'KW' '1378';\r\n" +
	                  "KW1379 : 'KW' '1379';\r\n" +
	                  "KW1380 : 'KW' '1380';\r\n" +
	                  "KW1381 : 'KW' '1381';\r\n" +
	                  "KW1382 : 'KW' '1382';\r\n" +
	                  "KW1383 : 'KW' '1383';\r\n" +
	                  "KW1384 : 'KW' '1384';\r\n" +
	                  "KW1385 : 'KW' '1385';\r\n" +
	                  "KW1386 : 'KW' '1386';\r\n" +
	                  "KW1387 : 'KW' '1387';\r\n" +
	                  "KW1388 : 'KW' '1388';\r\n" +
	                  "KW1389 : 'KW' '1389';\r\n" +
	                  "KW1390 : 'KW' '1390';\r\n" +
	                  "KW1391 : 'KW' '1391';\r\n" +
	                  "KW1392 : 'KW' '1392';\r\n" +
	                  "KW1393 : 'KW' '1393';\r\n" +
	                  "KW1394 : 'KW' '1394';\r\n" +
	                  "KW1395 : 'KW' '1395';\r\n" +
	                  "KW1396 : 'KW' '1396';\r\n" +
	                  "KW1397 : 'KW' '1397';\r\n" +
	                  "KW1398 : 'KW' '1398';\r\n" +
	                  "KW1399 : 'KW' '1399';\r\n" +
	                  "KW1400 : 'KW' '1400';\r\n" +
	                  "KW1401 : 'KW' '1401';\r\n" +
	                  "KW1402 : 'KW' '1402';\r\n" +
	                  "KW1403 : 'KW' '1403';\r\n" +
	                  "KW1404 : 'KW' '1404';\r\n" +
	                  "KW1405 : 'KW' '1405';\r\n" +
	                  "KW1406 : 'KW' '1406';\r\n" +
	                  "KW1407 : 'KW' '1407';\r\n" +
	                  "KW1408 : 'KW' '1408';\r\n" +
	                  "KW1409 : 'KW' '1409';\r\n" +
	                  "KW1410 : 'KW' '1410';\r\n" +
	                  "KW1411 : 'KW' '1411';\r\n" +
	                  "KW1412 : 'KW' '1412';\r\n" +
	                  "KW1413 : 'KW' '1413';\r\n" +
	                  "KW1414 : 'KW' '1414';\r\n" +
	                  "KW1415 : 'KW' '1415';\r\n" +
	                  "KW1416 : 'KW' '1416';\r\n" +
	                  "KW1417 : 'KW' '1417';\r\n" +
	                  "KW1418 : 'KW' '1418';\r\n" +
	                  "KW1419 : 'KW' '1419';\r\n" +
	                  "KW1420 : 'KW' '1420';\r\n" +
	                  "KW1421 : 'KW' '1421';\r\n" +
	                  "KW1422 : 'KW' '1422';\r\n" +
	                  "KW1423 : 'KW' '1423';\r\n" +
	                  "KW1424 : 'KW' '1424';\r\n" +
	                  "KW1425 : 'KW' '1425';\r\n" +
	                  "KW1426 : 'KW' '1426';\r\n" +
	                  "KW1427 : 'KW' '1427';\r\n" +
	                  "KW1428 : 'KW' '1428';\r\n" +
	                  "KW1429 : 'KW' '1429';\r\n" +
	                  "KW1430 : 'KW' '1430';\r\n" +
	                  "KW1431 : 'KW' '1431';\r\n" +
	                  "KW1432 : 'KW' '1432';\r\n" +
	                  "KW1433 : 'KW' '1433';\r\n" +
	                  "KW1434 : 'KW' '1434';\r\n" +
	                  "KW1435 : 'KW' '1435';\r\n" +
	                  "KW1436 : 'KW' '1436';\r\n" +
	                  "KW1437 : 'KW' '1437';\r\n" +
	                  "KW1438 : 'KW' '1438';\r\n" +
	                  "KW1439 : 'KW' '1439';\r\n" +
	                  "KW1440 : 'KW' '1440';\r\n" +
	                  "KW1441 : 'KW' '1441';\r\n" +
	                  "KW1442 : 'KW' '1442';\r\n" +
	                  "KW1443 : 'KW' '1443';\r\n" +
	                  "KW1444 : 'KW' '1444';\r\n" +
	                  "KW1445 : 'KW' '1445';\r\n" +
	                  "KW1446 : 'KW' '1446';\r\n" +
	                  "KW1447 : 'KW' '1447';\r\n" +
	                  "KW1448 : 'KW' '1448';\r\n" +
	                  "KW1449 : 'KW' '1449';\r\n" +
	                  "KW1450 : 'KW' '1450';\r\n" +
	                  "KW1451 : 'KW' '1451';\r\n" +
	                  "KW1452 : 'KW' '1452';\r\n" +
	                  "KW1453 : 'KW' '1453';\r\n" +
	                  "KW1454 : 'KW' '1454';\r\n" +
	                  "KW1455 : 'KW' '1455';\r\n" +
	                  "KW1456 : 'KW' '1456';\r\n" +
	                  "KW1457 : 'KW' '1457';\r\n" +
	                  "KW1458 : 'KW' '1458';\r\n" +
	                  "KW1459 : 'KW' '1459';\r\n" +
	                  "KW1460 : 'KW' '1460';\r\n" +
	                  "KW1461 : 'KW' '1461';\r\n" +
	                  "KW1462 : 'KW' '1462';\r\n" +
	                  "KW1463 : 'KW' '1463';\r\n" +
	                  "KW1464 : 'KW' '1464';\r\n" +
	                  "KW1465 : 'KW' '1465';\r\n" +
	                  "KW1466 : 'KW' '1466';\r\n" +
	                  "KW1467 : 'KW' '1467';\r\n" +
	                  "KW1468 : 'KW' '1468';\r\n" +
	                  "KW1469 : 'KW' '1469';\r\n" +
	                  "KW1470 : 'KW' '1470';\r\n" +
	                  "KW1471 : 'KW' '1471';\r\n" +
	                  "KW1472 : 'KW' '1472';\r\n" +
	                  "KW1473 : 'KW' '1473';\r\n" +
	                  "KW1474 : 'KW' '1474';\r\n" +
	                  "KW1475 : 'KW' '1475';\r\n" +
	                  "KW1476 : 'KW' '1476';\r\n" +
	                  "KW1477 : 'KW' '1477';\r\n" +
	                  "KW1478 : 'KW' '1478';\r\n" +
	                  "KW1479 : 'KW' '1479';\r\n" +
	                  "KW1480 : 'KW' '1480';\r\n" +
	                  "KW1481 : 'KW' '1481';\r\n" +
	                  "KW1482 : 'KW' '1482';\r\n" +
	                  "KW1483 : 'KW' '1483';\r\n" +
	                  "KW1484 : 'KW' '1484';\r\n" +
	                  "KW1485 : 'KW' '1485';\r\n" +
	                  "KW1486 : 'KW' '1486';\r\n" +
	                  "KW1487 : 'KW' '1487';\r\n" +
	                  "KW1488 : 'KW' '1488';\r\n" +
	                  "KW1489 : 'KW' '1489';\r\n" +
	                  "KW1490 : 'KW' '1490';\r\n" +
	                  "KW1491 : 'KW' '1491';\r\n" +
	                  "KW1492 : 'KW' '1492';\r\n" +
	                  "KW1493 : 'KW' '1493';\r\n" +
	                  "KW1494 : 'KW' '1494';\r\n" +
	                  "KW1495 : 'KW' '1495';\r\n" +
	                  "KW1496 : 'KW' '1496';\r\n" +
	                  "KW1497 : 'KW' '1497';\r\n" +
	                  "KW1498 : 'KW' '1498';\r\n" +
	                  "KW1499 : 'KW' '1499';\r\n" +
	                  "KW1500 : 'KW' '1500';\r\n" +
	                  "KW1501 : 'KW' '1501';\r\n" +
	                  "KW1502 : 'KW' '1502';\r\n" +
	                  "KW1503 : 'KW' '1503';\r\n" +
	                  "KW1504 : 'KW' '1504';\r\n" +
	                  "KW1505 : 'KW' '1505';\r\n" +
	                  "KW1506 : 'KW' '1506';\r\n" +
	                  "KW1507 : 'KW' '1507';\r\n" +
	                  "KW1508 : 'KW' '1508';\r\n" +
	                  "KW1509 : 'KW' '1509';\r\n" +
	                  "KW1510 : 'KW' '1510';\r\n" +
	                  "KW1511 : 'KW' '1511';\r\n" +
	                  "KW1512 : 'KW' '1512';\r\n" +
	                  "KW1513 : 'KW' '1513';\r\n" +
	                  "KW1514 : 'KW' '1514';\r\n" +
	                  "KW1515 : 'KW' '1515';\r\n" +
	                  "KW1516 : 'KW' '1516';\r\n" +
	                  "KW1517 : 'KW' '1517';\r\n" +
	                  "KW1518 : 'KW' '1518';\r\n" +
	                  "KW1519 : 'KW' '1519';\r\n" +
	                  "KW1520 : 'KW' '1520';\r\n" +
	                  "KW1521 : 'KW' '1521';\r\n" +
	                  "KW1522 : 'KW' '1522';\r\n" +
	                  "KW1523 : 'KW' '1523';\r\n" +
	                  "KW1524 : 'KW' '1524';\r\n" +
	                  "KW1525 : 'KW' '1525';\r\n" +
	                  "KW1526 : 'KW' '1526';\r\n" +
	                  "KW1527 : 'KW' '1527';\r\n" +
	                  "KW1528 : 'KW' '1528';\r\n" +
	                  "KW1529 : 'KW' '1529';\r\n" +
	                  "KW1530 : 'KW' '1530';\r\n" +
	                  "KW1531 : 'KW' '1531';\r\n" +
	                  "KW1532 : 'KW' '1532';\r\n" +
	                  "KW1533 : 'KW' '1533';\r\n" +
	                  "KW1534 : 'KW' '1534';\r\n" +
	                  "KW1535 : 'KW' '1535';\r\n" +
	                  "KW1536 : 'KW' '1536';\r\n" +
	                  "KW1537 : 'KW' '1537';\r\n" +
	                  "KW1538 : 'KW' '1538';\r\n" +
	                  "KW1539 : 'KW' '1539';\r\n" +
	                  "KW1540 : 'KW' '1540';\r\n" +
	                  "KW1541 : 'KW' '1541';\r\n" +
	                  "KW1542 : 'KW' '1542';\r\n" +
	                  "KW1543 : 'KW' '1543';\r\n" +
	                  "KW1544 : 'KW' '1544';\r\n" +
	                  "KW1545 : 'KW' '1545';\r\n" +
	                  "KW1546 : 'KW' '1546';\r\n" +
	                  "KW1547 : 'KW' '1547';\r\n" +
	                  "KW1548 : 'KW' '1548';\r\n" +
	                  "KW1549 : 'KW' '1549';\r\n" +
	                  "KW1550 : 'KW' '1550';\r\n" +
	                  "KW1551 : 'KW' '1551';\r\n" +
	                  "KW1552 : 'KW' '1552';\r\n" +
	                  "KW1553 : 'KW' '1553';\r\n" +
	                  "KW1554 : 'KW' '1554';\r\n" +
	                  "KW1555 : 'KW' '1555';\r\n" +
	                  "KW1556 : 'KW' '1556';\r\n" +
	                  "KW1557 : 'KW' '1557';\r\n" +
	                  "KW1558 : 'KW' '1558';\r\n" +
	                  "KW1559 : 'KW' '1559';\r\n" +
	                  "KW1560 : 'KW' '1560';\r\n" +
	                  "KW1561 : 'KW' '1561';\r\n" +
	                  "KW1562 : 'KW' '1562';\r\n" +
	                  "KW1563 : 'KW' '1563';\r\n" +
	                  "KW1564 : 'KW' '1564';\r\n" +
	                  "KW1565 : 'KW' '1565';\r\n" +
	                  "KW1566 : 'KW' '1566';\r\n" +
	                  "KW1567 : 'KW' '1567';\r\n" +
	                  "KW1568 : 'KW' '1568';\r\n" +
	                  "KW1569 : 'KW' '1569';\r\n" +
	                  "KW1570 : 'KW' '1570';\r\n" +
	                  "KW1571 : 'KW' '1571';\r\n" +
	                  "KW1572 : 'KW' '1572';\r\n" +
	                  "KW1573 : 'KW' '1573';\r\n" +
	                  "KW1574 : 'KW' '1574';\r\n" +
	                  "KW1575 : 'KW' '1575';\r\n" +
	                  "KW1576 : 'KW' '1576';\r\n" +
	                  "KW1577 : 'KW' '1577';\r\n" +
	                  "KW1578 : 'KW' '1578';\r\n" +
	                  "KW1579 : 'KW' '1579';\r\n" +
	                  "KW1580 : 'KW' '1580';\r\n" +
	                  "KW1581 : 'KW' '1581';\r\n" +
	                  "KW1582 : 'KW' '1582';\r\n" +
	                  "KW1583 : 'KW' '1583';\r\n" +
	                  "KW1584 : 'KW' '1584';\r\n" +
	                  "KW1585 : 'KW' '1585';\r\n" +
	                  "KW1586 : 'KW' '1586';\r\n" +
	                  "KW1587 : 'KW' '1587';\r\n" +
	                  "KW1588 : 'KW' '1588';\r\n" +
	                  "KW1589 : 'KW' '1589';\r\n" +
	                  "KW1590 : 'KW' '1590';\r\n" +
	                  "KW1591 : 'KW' '1591';\r\n" +
	                  "KW1592 : 'KW' '1592';\r\n" +
	                  "KW1593 : 'KW' '1593';\r\n" +
	                  "KW1594 : 'KW' '1594';\r\n" +
	                  "KW1595 : 'KW' '1595';\r\n" +
	                  "KW1596 : 'KW' '1596';\r\n" +
	                  "KW1597 : 'KW' '1597';\r\n" +
	                  "KW1598 : 'KW' '1598';\r\n" +
	                  "KW1599 : 'KW' '1599';\r\n" +
	                  "KW1600 : 'KW' '1600';\r\n" +
	                  "KW1601 : 'KW' '1601';\r\n" +
	                  "KW1602 : 'KW' '1602';\r\n" +
	                  "KW1603 : 'KW' '1603';\r\n" +
	                  "KW1604 : 'KW' '1604';\r\n" +
	                  "KW1605 : 'KW' '1605';\r\n" +
	                  "KW1606 : 'KW' '1606';\r\n" +
	                  "KW1607 : 'KW' '1607';\r\n" +
	                  "KW1608 : 'KW' '1608';\r\n" +
	                  "KW1609 : 'KW' '1609';\r\n" +
	                  "KW1610 : 'KW' '1610';\r\n" +
	                  "KW1611 : 'KW' '1611';\r\n" +
	                  "KW1612 : 'KW' '1612';\r\n" +
	                  "KW1613 : 'KW' '1613';\r\n" +
	                  "KW1614 : 'KW' '1614';\r\n" +
	                  "KW1615 : 'KW' '1615';\r\n" +
	                  "KW1616 : 'KW' '1616';\r\n" +
	                  "KW1617 : 'KW' '1617';\r\n" +
	                  "KW1618 : 'KW' '1618';\r\n" +
	                  "KW1619 : 'KW' '1619';\r\n" +
	                  "KW1620 : 'KW' '1620';\r\n" +
	                  "KW1621 : 'KW' '1621';\r\n" +
	                  "KW1622 : 'KW' '1622';\r\n" +
	                  "KW1623 : 'KW' '1623';\r\n" +
	                  "KW1624 : 'KW' '1624';\r\n" +
	                  "KW1625 : 'KW' '1625';\r\n" +
	                  "KW1626 : 'KW' '1626';\r\n" +
	                  "KW1627 : 'KW' '1627';\r\n" +
	                  "KW1628 : 'KW' '1628';\r\n" +
	                  "KW1629 : 'KW' '1629';\r\n" +
	                  "KW1630 : 'KW' '1630';\r\n" +
	                  "KW1631 : 'KW' '1631';\r\n" +
	                  "KW1632 : 'KW' '1632';\r\n" +
	                  "KW1633 : 'KW' '1633';\r\n" +
	                  "KW1634 : 'KW' '1634';\r\n" +
	                  "KW1635 : 'KW' '1635';\r\n" +
	                  "KW1636 : 'KW' '1636';\r\n" +
	                  "KW1637 : 'KW' '1637';\r\n" +
	                  "KW1638 : 'KW' '1638';\r\n" +
	                  "KW1639 : 'KW' '1639';\r\n" +
	                  "KW1640 : 'KW' '1640';\r\n" +
	                  "KW1641 : 'KW' '1641';\r\n" +
	                  "KW1642 : 'KW' '1642';\r\n" +
	                  "KW1643 : 'KW' '1643';\r\n" +
	                  "KW1644 : 'KW' '1644';\r\n" +
	                  "KW1645 : 'KW' '1645';\r\n" +
	                  "KW1646 : 'KW' '1646';\r\n" +
	                  "KW1647 : 'KW' '1647';\r\n" +
	                  "KW1648 : 'KW' '1648';\r\n" +
	                  "KW1649 : 'KW' '1649';\r\n" +
	                  "KW1650 : 'KW' '1650';\r\n" +
	                  "KW1651 : 'KW' '1651';\r\n" +
	                  "KW1652 : 'KW' '1652';\r\n" +
	                  "KW1653 : 'KW' '1653';\r\n" +
	                  "KW1654 : 'KW' '1654';\r\n" +
	                  "KW1655 : 'KW' '1655';\r\n" +
	                  "KW1656 : 'KW' '1656';\r\n" +
	                  "KW1657 : 'KW' '1657';\r\n" +
	                  "KW1658 : 'KW' '1658';\r\n" +
	                  "KW1659 : 'KW' '1659';\r\n" +
	                  "KW1660 : 'KW' '1660';\r\n" +
	                  "KW1661 : 'KW' '1661';\r\n" +
	                  "KW1662 : 'KW' '1662';\r\n" +
	                  "KW1663 : 'KW' '1663';\r\n" +
	                  "KW1664 : 'KW' '1664';\r\n" +
	                  "KW1665 : 'KW' '1665';\r\n" +
	                  "KW1666 : 'KW' '1666';\r\n" +
	                  "KW1667 : 'KW' '1667';\r\n" +
	                  "KW1668 : 'KW' '1668';\r\n" +
	                  "KW1669 : 'KW' '1669';\r\n" +
	                  "KW1670 : 'KW' '1670';\r\n" +
	                  "KW1671 : 'KW' '1671';\r\n" +
	                  "KW1672 : 'KW' '1672';\r\n" +
	                  "KW1673 : 'KW' '1673';\r\n" +
	                  "KW1674 : 'KW' '1674';\r\n" +
	                  "KW1675 : 'KW' '1675';\r\n" +
	                  "KW1676 : 'KW' '1676';\r\n" +
	                  "KW1677 : 'KW' '1677';\r\n" +
	                  "KW1678 : 'KW' '1678';\r\n" +
	                  "KW1679 : 'KW' '1679';\r\n" +
	                  "KW1680 : 'KW' '1680';\r\n" +
	                  "KW1681 : 'KW' '1681';\r\n" +
	                  "KW1682 : 'KW' '1682';\r\n" +
	                  "KW1683 : 'KW' '1683';\r\n" +
	                  "KW1684 : 'KW' '1684';\r\n" +
	                  "KW1685 : 'KW' '1685';\r\n" +
	                  "KW1686 : 'KW' '1686';\r\n" +
	                  "KW1687 : 'KW' '1687';\r\n" +
	                  "KW1688 : 'KW' '1688';\r\n" +
	                  "KW1689 : 'KW' '1689';\r\n" +
	                  "KW1690 : 'KW' '1690';\r\n" +
	                  "KW1691 : 'KW' '1691';\r\n" +
	                  "KW1692 : 'KW' '1692';\r\n" +
	                  "KW1693 : 'KW' '1693';\r\n" +
	                  "KW1694 : 'KW' '1694';\r\n" +
	                  "KW1695 : 'KW' '1695';\r\n" +
	                  "KW1696 : 'KW' '1696';\r\n" +
	                  "KW1697 : 'KW' '1697';\r\n" +
	                  "KW1698 : 'KW' '1698';\r\n" +
	                  "KW1699 : 'KW' '1699';\r\n" +
	                  "KW1700 : 'KW' '1700';\r\n" +
	                  "KW1701 : 'KW' '1701';\r\n" +
	                  "KW1702 : 'KW' '1702';\r\n" +
	                  "KW1703 : 'KW' '1703';\r\n" +
	                  "KW1704 : 'KW' '1704';\r\n" +
	                  "KW1705 : 'KW' '1705';\r\n" +
	                  "KW1706 : 'KW' '1706';\r\n" +
	                  "KW1707 : 'KW' '1707';\r\n" +
	                  "KW1708 : 'KW' '1708';\r\n" +
	                  "KW1709 : 'KW' '1709';\r\n" +
	                  "KW1710 : 'KW' '1710';\r\n" +
	                  "KW1711 : 'KW' '1711';\r\n" +
	                  "KW1712 : 'KW' '1712';\r\n" +
	                  "KW1713 : 'KW' '1713';\r\n" +
	                  "KW1714 : 'KW' '1714';\r\n" +
	                  "KW1715 : 'KW' '1715';\r\n" +
	                  "KW1716 : 'KW' '1716';\r\n" +
	                  "KW1717 : 'KW' '1717';\r\n" +
	                  "KW1718 : 'KW' '1718';\r\n" +
	                  "KW1719 : 'KW' '1719';\r\n" +
	                  "KW1720 : 'KW' '1720';\r\n" +
	                  "KW1721 : 'KW' '1721';\r\n" +
	                  "KW1722 : 'KW' '1722';\r\n" +
	                  "KW1723 : 'KW' '1723';\r\n" +
	                  "KW1724 : 'KW' '1724';\r\n" +
	                  "KW1725 : 'KW' '1725';\r\n" +
	                  "KW1726 : 'KW' '1726';\r\n" +
	                  "KW1727 : 'KW' '1727';\r\n" +
	                  "KW1728 : 'KW' '1728';\r\n" +
	                  "KW1729 : 'KW' '1729';\r\n" +
	                  "KW1730 : 'KW' '1730';\r\n" +
	                  "KW1731 : 'KW' '1731';\r\n" +
	                  "KW1732 : 'KW' '1732';\r\n" +
	                  "KW1733 : 'KW' '1733';\r\n" +
	                  "KW1734 : 'KW' '1734';\r\n" +
	                  "KW1735 : 'KW' '1735';\r\n" +
	                  "KW1736 : 'KW' '1736';\r\n" +
	                  "KW1737 : 'KW' '1737';\r\n" +
	                  "KW1738 : 'KW' '1738';\r\n" +
	                  "KW1739 : 'KW' '1739';\r\n" +
	                  "KW1740 : 'KW' '1740';\r\n" +
	                  "KW1741 : 'KW' '1741';\r\n" +
	                  "KW1742 : 'KW' '1742';\r\n" +
	                  "KW1743 : 'KW' '1743';\r\n" +
	                  "KW1744 : 'KW' '1744';\r\n" +
	                  "KW1745 : 'KW' '1745';\r\n" +
	                  "KW1746 : 'KW' '1746';\r\n" +
	                  "KW1747 : 'KW' '1747';\r\n" +
	                  "KW1748 : 'KW' '1748';\r\n" +
	                  "KW1749 : 'KW' '1749';\r\n" +
	                  "KW1750 : 'KW' '1750';\r\n" +
	                  "KW1751 : 'KW' '1751';\r\n" +
	                  "KW1752 : 'KW' '1752';\r\n" +
	                  "KW1753 : 'KW' '1753';\r\n" +
	                  "KW1754 : 'KW' '1754';\r\n" +
	                  "KW1755 : 'KW' '1755';\r\n" +
	                  "KW1756 : 'KW' '1756';\r\n" +
	                  "KW1757 : 'KW' '1757';\r\n" +
	                  "KW1758 : 'KW' '1758';\r\n" +
	                  "KW1759 : 'KW' '1759';\r\n" +
	                  "KW1760 : 'KW' '1760';\r\n" +
	                  "KW1761 : 'KW' '1761';\r\n" +
	                  "KW1762 : 'KW' '1762';\r\n" +
	                  "KW1763 : 'KW' '1763';\r\n" +
	                  "KW1764 : 'KW' '1764';\r\n" +
	                  "KW1765 : 'KW' '1765';\r\n" +
	                  "KW1766 : 'KW' '1766';\r\n" +
	                  "KW1767 : 'KW' '1767';\r\n" +
	                  "KW1768 : 'KW' '1768';\r\n" +
	                  "KW1769 : 'KW' '1769';\r\n" +
	                  "KW1770 : 'KW' '1770';\r\n" +
	                  "KW1771 : 'KW' '1771';\r\n" +
	                  "KW1772 : 'KW' '1772';\r\n" +
	                  "KW1773 : 'KW' '1773';\r\n" +
	                  "KW1774 : 'KW' '1774';\r\n" +
	                  "KW1775 : 'KW' '1775';\r\n" +
	                  "KW1776 : 'KW' '1776';\r\n" +
	                  "KW1777 : 'KW' '1777';\r\n" +
	                  "KW1778 : 'KW' '1778';\r\n" +
	                  "KW1779 : 'KW' '1779';\r\n" +
	                  "KW1780 : 'KW' '1780';\r\n" +
	                  "KW1781 : 'KW' '1781';\r\n" +
	                  "KW1782 : 'KW' '1782';\r\n" +
	                  "KW1783 : 'KW' '1783';\r\n" +
	                  "KW1784 : 'KW' '1784';\r\n" +
	                  "KW1785 : 'KW' '1785';\r\n" +
	                  "KW1786 : 'KW' '1786';\r\n" +
	                  "KW1787 : 'KW' '1787';\r\n" +
	                  "KW1788 : 'KW' '1788';\r\n" +
	                  "KW1789 : 'KW' '1789';\r\n" +
	                  "KW1790 : 'KW' '1790';\r\n" +
	                  "KW1791 : 'KW' '1791';\r\n" +
	                  "KW1792 : 'KW' '1792';\r\n" +
	                  "KW1793 : 'KW' '1793';\r\n" +
	                  "KW1794 : 'KW' '1794';\r\n" +
	                  "KW1795 : 'KW' '1795';\r\n" +
	                  "KW1796 : 'KW' '1796';\r\n" +
	                  "KW1797 : 'KW' '1797';\r\n" +
	                  "KW1798 : 'KW' '1798';\r\n" +
	                  "KW1799 : 'KW' '1799';\r\n" +
	                  "KW1800 : 'KW' '1800';\r\n" +
	                  "KW1801 : 'KW' '1801';\r\n" +
	                  "KW1802 : 'KW' '1802';\r\n" +
	                  "KW1803 : 'KW' '1803';\r\n" +
	                  "KW1804 : 'KW' '1804';\r\n" +
	                  "KW1805 : 'KW' '1805';\r\n" +
	                  "KW1806 : 'KW' '1806';\r\n" +
	                  "KW1807 : 'KW' '1807';\r\n" +
	                  "KW1808 : 'KW' '1808';\r\n" +
	                  "KW1809 : 'KW' '1809';\r\n" +
	                  "KW1810 : 'KW' '1810';\r\n" +
	                  "KW1811 : 'KW' '1811';\r\n" +
	                  "KW1812 : 'KW' '1812';\r\n" +
	                  "KW1813 : 'KW' '1813';\r\n" +
	                  "KW1814 : 'KW' '1814';\r\n" +
	                  "KW1815 : 'KW' '1815';\r\n" +
	                  "KW1816 : 'KW' '1816';\r\n" +
	                  "KW1817 : 'KW' '1817';\r\n" +
	                  "KW1818 : 'KW' '1818';\r\n" +
	                  "KW1819 : 'KW' '1819';\r\n" +
	                  "KW1820 : 'KW' '1820';\r\n" +
	                  "KW1821 : 'KW' '1821';\r\n" +
	                  "KW1822 : 'KW' '1822';\r\n" +
	                  "KW1823 : 'KW' '1823';\r\n" +
	                  "KW1824 : 'KW' '1824';\r\n" +
	                  "KW1825 : 'KW' '1825';\r\n" +
	                  "KW1826 : 'KW' '1826';\r\n" +
	                  "KW1827 : 'KW' '1827';\r\n" +
	                  "KW1828 : 'KW' '1828';\r\n" +
	                  "KW1829 : 'KW' '1829';\r\n" +
	                  "KW1830 : 'KW' '1830';\r\n" +
	                  "KW1831 : 'KW' '1831';\r\n" +
	                  "KW1832 : 'KW' '1832';\r\n" +
	                  "KW1833 : 'KW' '1833';\r\n" +
	                  "KW1834 : 'KW' '1834';\r\n" +
	                  "KW1835 : 'KW' '1835';\r\n" +
	                  "KW1836 : 'KW' '1836';\r\n" +
	                  "KW1837 : 'KW' '1837';\r\n" +
	                  "KW1838 : 'KW' '1838';\r\n" +
	                  "KW1839 : 'KW' '1839';\r\n" +
	                  "KW1840 : 'KW' '1840';\r\n" +
	                  "KW1841 : 'KW' '1841';\r\n" +
	                  "KW1842 : 'KW' '1842';\r\n" +
	                  "KW1843 : 'KW' '1843';\r\n" +
	                  "KW1844 : 'KW' '1844';\r\n" +
	                  "KW1845 : 'KW' '1845';\r\n" +
	                  "KW1846 : 'KW' '1846';\r\n" +
	                  "KW1847 : 'KW' '1847';\r\n" +
	                  "KW1848 : 'KW' '1848';\r\n" +
	                  "KW1849 : 'KW' '1849';\r\n" +
	                  "KW1850 : 'KW' '1850';\r\n" +
	                  "KW1851 : 'KW' '1851';\r\n" +
	                  "KW1852 : 'KW' '1852';\r\n" +
	                  "KW1853 : 'KW' '1853';\r\n" +
	                  "KW1854 : 'KW' '1854';\r\n" +
	                  "KW1855 : 'KW' '1855';\r\n" +
	                  "KW1856 : 'KW' '1856';\r\n" +
	                  "KW1857 : 'KW' '1857';\r\n" +
	                  "KW1858 : 'KW' '1858';\r\n" +
	                  "KW1859 : 'KW' '1859';\r\n" +
	                  "KW1860 : 'KW' '1860';\r\n" +
	                  "KW1861 : 'KW' '1861';\r\n" +
	                  "KW1862 : 'KW' '1862';\r\n" +
	                  "KW1863 : 'KW' '1863';\r\n" +
	                  "KW1864 : 'KW' '1864';\r\n" +
	                  "KW1865 : 'KW' '1865';\r\n" +
	                  "KW1866 : 'KW' '1866';\r\n" +
	                  "KW1867 : 'KW' '1867';\r\n" +
	                  "KW1868 : 'KW' '1868';\r\n" +
	                  "KW1869 : 'KW' '1869';\r\n" +
	                  "KW1870 : 'KW' '1870';\r\n" +
	                  "KW1871 : 'KW' '1871';\r\n" +
	                  "KW1872 : 'KW' '1872';\r\n" +
	                  "KW1873 : 'KW' '1873';\r\n" +
	                  "KW1874 : 'KW' '1874';\r\n" +
	                  "KW1875 : 'KW' '1875';\r\n" +
	                  "KW1876 : 'KW' '1876';\r\n" +
	                  "KW1877 : 'KW' '1877';\r\n" +
	                  "KW1878 : 'KW' '1878';\r\n" +
	                  "KW1879 : 'KW' '1879';\r\n" +
	                  "KW1880 : 'KW' '1880';\r\n" +
	                  "KW1881 : 'KW' '1881';\r\n" +
	                  "KW1882 : 'KW' '1882';\r\n" +
	                  "KW1883 : 'KW' '1883';\r\n" +
	                  "KW1884 : 'KW' '1884';\r\n" +
	                  "KW1885 : 'KW' '1885';\r\n" +
	                  "KW1886 : 'KW' '1886';\r\n" +
	                  "KW1887 : 'KW' '1887';\r\n" +
	                  "KW1888 : 'KW' '1888';\r\n" +
	                  "KW1889 : 'KW' '1889';\r\n" +
	                  "KW1890 : 'KW' '1890';\r\n" +
	                  "KW1891 : 'KW' '1891';\r\n" +
	                  "KW1892 : 'KW' '1892';\r\n" +
	                  "KW1893 : 'KW' '1893';\r\n" +
	                  "KW1894 : 'KW' '1894';\r\n" +
	                  "KW1895 : 'KW' '1895';\r\n" +
	                  "KW1896 : 'KW' '1896';\r\n" +
	                  "KW1897 : 'KW' '1897';\r\n" +
	                  "KW1898 : 'KW' '1898';\r\n" +
	                  "KW1899 : 'KW' '1899';\r\n" +
	                  "KW1900 : 'KW' '1900';\r\n" +
	                  "KW1901 : 'KW' '1901';\r\n" +
	                  "KW1902 : 'KW' '1902';\r\n" +
	                  "KW1903 : 'KW' '1903';\r\n" +
	                  "KW1904 : 'KW' '1904';\r\n" +
	                  "KW1905 : 'KW' '1905';\r\n" +
	                  "KW1906 : 'KW' '1906';\r\n" +
	                  "KW1907 : 'KW' '1907';\r\n" +
	                  "KW1908 : 'KW' '1908';\r\n" +
	                  "KW1909 : 'KW' '1909';\r\n" +
	                  "KW1910 : 'KW' '1910';\r\n" +
	                  "KW1911 : 'KW' '1911';\r\n" +
	                  "KW1912 : 'KW' '1912';\r\n" +
	                  "KW1913 : 'KW' '1913';\r\n" +
	                  "KW1914 : 'KW' '1914';\r\n" +
	                  "KW1915 : 'KW' '1915';\r\n" +
	                  "KW1916 : 'KW' '1916';\r\n" +
	                  "KW1917 : 'KW' '1917';\r\n" +
	                  "KW1918 : 'KW' '1918';\r\n" +
	                  "KW1919 : 'KW' '1919';\r\n" +
	                  "KW1920 : 'KW' '1920';\r\n" +
	                  "KW1921 : 'KW' '1921';\r\n" +
	                  "KW1922 : 'KW' '1922';\r\n" +
	                  "KW1923 : 'KW' '1923';\r\n" +
	                  "KW1924 : 'KW' '1924';\r\n" +
	                  "KW1925 : 'KW' '1925';\r\n" +
	                  "KW1926 : 'KW' '1926';\r\n" +
	                  "KW1927 : 'KW' '1927';\r\n" +
	                  "KW1928 : 'KW' '1928';\r\n" +
	                  "KW1929 : 'KW' '1929';\r\n" +
	                  "KW1930 : 'KW' '1930';\r\n" +
	                  "KW1931 : 'KW' '1931';\r\n" +
	                  "KW1932 : 'KW' '1932';\r\n" +
	                  "KW1933 : 'KW' '1933';\r\n" +
	                  "KW1934 : 'KW' '1934';\r\n" +
	                  "KW1935 : 'KW' '1935';\r\n" +
	                  "KW1936 : 'KW' '1936';\r\n" +
	                  "KW1937 : 'KW' '1937';\r\n" +
	                  "KW1938 : 'KW' '1938';\r\n" +
	                  "KW1939 : 'KW' '1939';\r\n" +
	                  "KW1940 : 'KW' '1940';\r\n" +
	                  "KW1941 : 'KW' '1941';\r\n" +
	                  "KW1942 : 'KW' '1942';\r\n" +
	                  "KW1943 : 'KW' '1943';\r\n" +
	                  "KW1944 : 'KW' '1944';\r\n" +
	                  "KW1945 : 'KW' '1945';\r\n" +
	                  "KW1946 : 'KW' '1946';\r\n" +
	                  "KW1947 : 'KW' '1947';\r\n" +
	                  "KW1948 : 'KW' '1948';\r\n" +
	                  "KW1949 : 'KW' '1949';\r\n" +
	                  "KW1950 : 'KW' '1950';\r\n" +
	                  "KW1951 : 'KW' '1951';\r\n" +
	                  "KW1952 : 'KW' '1952';\r\n" +
	                  "KW1953 : 'KW' '1953';\r\n" +
	                  "KW1954 : 'KW' '1954';\r\n" +
	                  "KW1955 : 'KW' '1955';\r\n" +
	                  "KW1956 : 'KW' '1956';\r\n" +
	                  "KW1957 : 'KW' '1957';\r\n" +
	                  "KW1958 : 'KW' '1958';\r\n" +
	                  "KW1959 : 'KW' '1959';\r\n" +
	                  "KW1960 : 'KW' '1960';\r\n" +
	                  "KW1961 : 'KW' '1961';\r\n" +
	                  "KW1962 : 'KW' '1962';\r\n" +
	                  "KW1963 : 'KW' '1963';\r\n" +
	                  "KW1964 : 'KW' '1964';\r\n" +
	                  "KW1965 : 'KW' '1965';\r\n" +
	                  "KW1966 : 'KW' '1966';\r\n" +
	                  "KW1967 : 'KW' '1967';\r\n" +
	                  "KW1968 : 'KW' '1968';\r\n" +
	                  "KW1969 : 'KW' '1969';\r\n" +
	                  "KW1970 : 'KW' '1970';\r\n" +
	                  "KW1971 : 'KW' '1971';\r\n" +
	                  "KW1972 : 'KW' '1972';\r\n" +
	                  "KW1973 : 'KW' '1973';\r\n" +
	                  "KW1974 : 'KW' '1974';\r\n" +
	                  "KW1975 : 'KW' '1975';\r\n" +
	                  "KW1976 : 'KW' '1976';\r\n" +
	                  "KW1977 : 'KW' '1977';\r\n" +
	                  "KW1978 : 'KW' '1978';\r\n" +
	                  "KW1979 : 'KW' '1979';\r\n" +
	                  "KW1980 : 'KW' '1980';\r\n" +
	                  "KW1981 : 'KW' '1981';\r\n" +
	                  "KW1982 : 'KW' '1982';\r\n" +
	                  "KW1983 : 'KW' '1983';\r\n" +
	                  "KW1984 : 'KW' '1984';\r\n" +
	                  "KW1985 : 'KW' '1985';\r\n" +
	                  "KW1986 : 'KW' '1986';\r\n" +
	                  "KW1987 : 'KW' '1987';\r\n" +
	                  "KW1988 : 'KW' '1988';\r\n" +
	                  "KW1989 : 'KW' '1989';\r\n" +
	                  "KW1990 : 'KW' '1990';\r\n" +
	                  "KW1991 : 'KW' '1991';\r\n" +
	                  "KW1992 : 'KW' '1992';\r\n" +
	                  "KW1993 : 'KW' '1993';\r\n" +
	                  "KW1994 : 'KW' '1994';\r\n" +
	                  "KW1995 : 'KW' '1995';\r\n" +
	                  "KW1996 : 'KW' '1996';\r\n" +
	                  "KW1997 : 'KW' '1997';\r\n" +
	                  "KW1998 : 'KW' '1998';\r\n" +
	                  "KW1999 : 'KW' '1999';\r\n" +
	                  "KW2000 : 'KW' '2000';\r\n" +
	                  "KW2001 : 'KW' '2001';\r\n" +
	                  "KW2002 : 'KW' '2002';\r\n" +
	                  "KW2003 : 'KW' '2003';\r\n" +
	                  "KW2004 : 'KW' '2004';\r\n" +
	                  "KW2005 : 'KW' '2005';\r\n" +
	                  "KW2006 : 'KW' '2006';\r\n" +
	                  "KW2007 : 'KW' '2007';\r\n" +
	                  "KW2008 : 'KW' '2008';\r\n" +
	                  "KW2009 : 'KW' '2009';\r\n" +
	                  "KW2010 : 'KW' '2010';\r\n" +
	                  "KW2011 : 'KW' '2011';\r\n" +
	                  "KW2012 : 'KW' '2012';\r\n" +
	                  "KW2013 : 'KW' '2013';\r\n" +
	                  "KW2014 : 'KW' '2014';\r\n" +
	                  "KW2015 : 'KW' '2015';\r\n" +
	                  "KW2016 : 'KW' '2016';\r\n" +
	                  "KW2017 : 'KW' '2017';\r\n" +
	                  "KW2018 : 'KW' '2018';\r\n" +
	                  "KW2019 : 'KW' '2019';\r\n" +
	                  "KW2020 : 'KW' '2020';\r\n" +
	                  "KW2021 : 'KW' '2021';\r\n" +
	                  "KW2022 : 'KW' '2022';\r\n" +
	                  "KW2023 : 'KW' '2023';\r\n" +
	                  "KW2024 : 'KW' '2024';\r\n" +
	                  "KW2025 : 'KW' '2025';\r\n" +
	                  "KW2026 : 'KW' '2026';\r\n" +
	                  "KW2027 : 'KW' '2027';\r\n" +
	                  "KW2028 : 'KW' '2028';\r\n" +
	                  "KW2029 : 'KW' '2029';\r\n" +
	                  "KW2030 : 'KW' '2030';\r\n" +
	                  "KW2031 : 'KW' '2031';\r\n" +
	                  "KW2032 : 'KW' '2032';\r\n" +
	                  "KW2033 : 'KW' '2033';\r\n" +
	                  "KW2034 : 'KW' '2034';\r\n" +
	                  "KW2035 : 'KW' '2035';\r\n" +
	                  "KW2036 : 'KW' '2036';\r\n" +
	                  "KW2037 : 'KW' '2037';\r\n" +
	                  "KW2038 : 'KW' '2038';\r\n" +
	                  "KW2039 : 'KW' '2039';\r\n" +
	                  "KW2040 : 'KW' '2040';\r\n" +
	                  "KW2041 : 'KW' '2041';\r\n" +
	                  "KW2042 : 'KW' '2042';\r\n" +
	                  "KW2043 : 'KW' '2043';\r\n" +
	                  "KW2044 : 'KW' '2044';\r\n" +
	                  "KW2045 : 'KW' '2045';\r\n" +
	                  "KW2046 : 'KW' '2046';\r\n" +
	                  "KW2047 : 'KW' '2047';\r\n" +
	                  "KW2048 : 'KW' '2048';\r\n" +
	                  "KW2049 : 'KW' '2049';\r\n" +
	                  "KW2050 : 'KW' '2050';\r\n" +
	                  "KW2051 : 'KW' '2051';\r\n" +
	                  "KW2052 : 'KW' '2052';\r\n" +
	                  "KW2053 : 'KW' '2053';\r\n" +
	                  "KW2054 : 'KW' '2054';\r\n" +
	                  "KW2055 : 'KW' '2055';\r\n" +
	                  "KW2056 : 'KW' '2056';\r\n" +
	                  "KW2057 : 'KW' '2057';\r\n" +
	                  "KW2058 : 'KW' '2058';\r\n" +
	                  "KW2059 : 'KW' '2059';\r\n" +
	                  "KW2060 : 'KW' '2060';\r\n" +
	                  "KW2061 : 'KW' '2061';\r\n" +
	                  "KW2062 : 'KW' '2062';\r\n" +
	                  "KW2063 : 'KW' '2063';\r\n" +
	                  "KW2064 : 'KW' '2064';\r\n" +
	                  "KW2065 : 'KW' '2065';\r\n" +
	                  "KW2066 : 'KW' '2066';\r\n" +
	                  "KW2067 : 'KW' '2067';\r\n" +
	                  "KW2068 : 'KW' '2068';\r\n" +
	                  "KW2069 : 'KW' '2069';\r\n" +
	                  "KW2070 : 'KW' '2070';\r\n" +
	                  "KW2071 : 'KW' '2071';\r\n" +
	                  "KW2072 : 'KW' '2072';\r\n" +
	                  "KW2073 : 'KW' '2073';\r\n" +
	                  "KW2074 : 'KW' '2074';\r\n" +
	                  "KW2075 : 'KW' '2075';\r\n" +
	                  "KW2076 : 'KW' '2076';\r\n" +
	                  "KW2077 : 'KW' '2077';\r\n" +
	                  "KW2078 : 'KW' '2078';\r\n" +
	                  "KW2079 : 'KW' '2079';\r\n" +
	                  "KW2080 : 'KW' '2080';\r\n" +
	                  "KW2081 : 'KW' '2081';\r\n" +
	                  "KW2082 : 'KW' '2082';\r\n" +
	                  "KW2083 : 'KW' '2083';\r\n" +
	                  "KW2084 : 'KW' '2084';\r\n" +
	                  "KW2085 : 'KW' '2085';\r\n" +
	                  "KW2086 : 'KW' '2086';\r\n" +
	                  "KW2087 : 'KW' '2087';\r\n" +
	                  "KW2088 : 'KW' '2088';\r\n" +
	                  "KW2089 : 'KW' '2089';\r\n" +
	                  "KW2090 : 'KW' '2090';\r\n" +
	                  "KW2091 : 'KW' '2091';\r\n" +
	                  "KW2092 : 'KW' '2092';\r\n" +
	                  "KW2093 : 'KW' '2093';\r\n" +
	                  "KW2094 : 'KW' '2094';\r\n" +
	                  "KW2095 : 'KW' '2095';\r\n" +
	                  "KW2096 : 'KW' '2096';\r\n" +
	                  "KW2097 : 'KW' '2097';\r\n" +
	                  "KW2098 : 'KW' '2098';\r\n" +
	                  "KW2099 : 'KW' '2099';\r\n" +
	                  "KW2100 : 'KW' '2100';\r\n" +
	                  "KW2101 : 'KW' '2101';\r\n" +
	                  "KW2102 : 'KW' '2102';\r\n" +
	                  "KW2103 : 'KW' '2103';\r\n" +
	                  "KW2104 : 'KW' '2104';\r\n" +
	                  "KW2105 : 'KW' '2105';\r\n" +
	                  "KW2106 : 'KW' '2106';\r\n" +
	                  "KW2107 : 'KW' '2107';\r\n" +
	                  "KW2108 : 'KW' '2108';\r\n" +
	                  "KW2109 : 'KW' '2109';\r\n" +
	                  "KW2110 : 'KW' '2110';\r\n" +
	                  "KW2111 : 'KW' '2111';\r\n" +
	                  "KW2112 : 'KW' '2112';\r\n" +
	                  "KW2113 : 'KW' '2113';\r\n" +
	                  "KW2114 : 'KW' '2114';\r\n" +
	                  "KW2115 : 'KW' '2115';\r\n" +
	                  "KW2116 : 'KW' '2116';\r\n" +
	                  "KW2117 : 'KW' '2117';\r\n" +
	                  "KW2118 : 'KW' '2118';\r\n" +
	                  "KW2119 : 'KW' '2119';\r\n" +
	                  "KW2120 : 'KW' '2120';\r\n" +
	                  "KW2121 : 'KW' '2121';\r\n" +
	                  "KW2122 : 'KW' '2122';\r\n" +
	                  "KW2123 : 'KW' '2123';\r\n" +
	                  "KW2124 : 'KW' '2124';\r\n" +
	                  "KW2125 : 'KW' '2125';\r\n" +
	                  "KW2126 : 'KW' '2126';\r\n" +
	                  "KW2127 : 'KW' '2127';\r\n" +
	                  "KW2128 : 'KW' '2128';\r\n" +
	                  "KW2129 : 'KW' '2129';\r\n" +
	                  "KW2130 : 'KW' '2130';\r\n" +
	                  "KW2131 : 'KW' '2131';\r\n" +
	                  "KW2132 : 'KW' '2132';\r\n" +
	                  "KW2133 : 'KW' '2133';\r\n" +
	                  "KW2134 : 'KW' '2134';\r\n" +
	                  "KW2135 : 'KW' '2135';\r\n" +
	                  "KW2136 : 'KW' '2136';\r\n" +
	                  "KW2137 : 'KW' '2137';\r\n" +
	                  "KW2138 : 'KW' '2138';\r\n" +
	                  "KW2139 : 'KW' '2139';\r\n" +
	                  "KW2140 : 'KW' '2140';\r\n" +
	                  "KW2141 : 'KW' '2141';\r\n" +
	                  "KW2142 : 'KW' '2142';\r\n" +
	                  "KW2143 : 'KW' '2143';\r\n" +
	                  "KW2144 : 'KW' '2144';\r\n" +
	                  "KW2145 : 'KW' '2145';\r\n" +
	                  "KW2146 : 'KW' '2146';\r\n" +
	                  "KW2147 : 'KW' '2147';\r\n" +
	                  "KW2148 : 'KW' '2148';\r\n" +
	                  "KW2149 : 'KW' '2149';\r\n" +
	                  "KW2150 : 'KW' '2150';\r\n" +
	                  "KW2151 : 'KW' '2151';\r\n" +
	                  "KW2152 : 'KW' '2152';\r\n" +
	                  "KW2153 : 'KW' '2153';\r\n" +
	                  "KW2154 : 'KW' '2154';\r\n" +
	                  "KW2155 : 'KW' '2155';\r\n" +
	                  "KW2156 : 'KW' '2156';\r\n" +
	                  "KW2157 : 'KW' '2157';\r\n" +
	                  "KW2158 : 'KW' '2158';\r\n" +
	                  "KW2159 : 'KW' '2159';\r\n" +
	                  "KW2160 : 'KW' '2160';\r\n" +
	                  "KW2161 : 'KW' '2161';\r\n" +
	                  "KW2162 : 'KW' '2162';\r\n" +
	                  "KW2163 : 'KW' '2163';\r\n" +
	                  "KW2164 : 'KW' '2164';\r\n" +
	                  "KW2165 : 'KW' '2165';\r\n" +
	                  "KW2166 : 'KW' '2166';\r\n" +
	                  "KW2167 : 'KW' '2167';\r\n" +
	                  "KW2168 : 'KW' '2168';\r\n" +
	                  "KW2169 : 'KW' '2169';\r\n" +
	                  "KW2170 : 'KW' '2170';\r\n" +
	                  "KW2171 : 'KW' '2171';\r\n" +
	                  "KW2172 : 'KW' '2172';\r\n" +
	                  "KW2173 : 'KW' '2173';\r\n" +
	                  "KW2174 : 'KW' '2174';\r\n" +
	                  "KW2175 : 'KW' '2175';\r\n" +
	                  "KW2176 : 'KW' '2176';\r\n" +
	                  "KW2177 : 'KW' '2177';\r\n" +
	                  "KW2178 : 'KW' '2178';\r\n" +
	                  "KW2179 : 'KW' '2179';\r\n" +
	                  "KW2180 : 'KW' '2180';\r\n" +
	                  "KW2181 : 'KW' '2181';\r\n" +
	                  "KW2182 : 'KW' '2182';\r\n" +
	                  "KW2183 : 'KW' '2183';\r\n" +
	                  "KW2184 : 'KW' '2184';\r\n" +
	                  "KW2185 : 'KW' '2185';\r\n" +
	                  "KW2186 : 'KW' '2186';\r\n" +
	                  "KW2187 : 'KW' '2187';\r\n" +
	                  "KW2188 : 'KW' '2188';\r\n" +
	                  "KW2189 : 'KW' '2189';\r\n" +
	                  "KW2190 : 'KW' '2190';\r\n" +
	                  "KW2191 : 'KW' '2191';\r\n" +
	                  "KW2192 : 'KW' '2192';\r\n" +
	                  "KW2193 : 'KW' '2193';\r\n" +
	                  "KW2194 : 'KW' '2194';\r\n" +
	                  "KW2195 : 'KW' '2195';\r\n" +
	                  "KW2196 : 'KW' '2196';\r\n" +
	                  "KW2197 : 'KW' '2197';\r\n" +
	                  "KW2198 : 'KW' '2198';\r\n" +
	                  "KW2199 : 'KW' '2199';\r\n" +
	                  "KW2200 : 'KW' '2200';\r\n" +
	                  "KW2201 : 'KW' '2201';\r\n" +
	                  "KW2202 : 'KW' '2202';\r\n" +
	                  "KW2203 : 'KW' '2203';\r\n" +
	                  "KW2204 : 'KW' '2204';\r\n" +
	                  "KW2205 : 'KW' '2205';\r\n" +
	                  "KW2206 : 'KW' '2206';\r\n" +
	                  "KW2207 : 'KW' '2207';\r\n" +
	                  "KW2208 : 'KW' '2208';\r\n" +
	                  "KW2209 : 'KW' '2209';\r\n" +
	                  "KW2210 : 'KW' '2210';\r\n" +
	                  "KW2211 : 'KW' '2211';\r\n" +
	                  "KW2212 : 'KW' '2212';\r\n" +
	                  "KW2213 : 'KW' '2213';\r\n" +
	                  "KW2214 : 'KW' '2214';\r\n" +
	                  "KW2215 : 'KW' '2215';\r\n" +
	                  "KW2216 : 'KW' '2216';\r\n" +
	                  "KW2217 : 'KW' '2217';\r\n" +
	                  "KW2218 : 'KW' '2218';\r\n" +
	                  "KW2219 : 'KW' '2219';\r\n" +
	                  "KW2220 : 'KW' '2220';\r\n" +
	                  "KW2221 : 'KW' '2221';\r\n" +
	                  "KW2222 : 'KW' '2222';\r\n" +
	                  "KW2223 : 'KW' '2223';\r\n" +
	                  "KW2224 : 'KW' '2224';\r\n" +
	                  "KW2225 : 'KW' '2225';\r\n" +
	                  "KW2226 : 'KW' '2226';\r\n" +
	                  "KW2227 : 'KW' '2227';\r\n" +
	                  "KW2228 : 'KW' '2228';\r\n" +
	                  "KW2229 : 'KW' '2229';\r\n" +
	                  "KW2230 : 'KW' '2230';\r\n" +
	                  "KW2231 : 'KW' '2231';\r\n" +
	                  "KW2232 : 'KW' '2232';\r\n" +
	                  "KW2233 : 'KW' '2233';\r\n" +
	                  "KW2234 : 'KW' '2234';\r\n" +
	                  "KW2235 : 'KW' '2235';\r\n" +
	                  "KW2236 : 'KW' '2236';\r\n" +
	                  "KW2237 : 'KW' '2237';\r\n" +
	                  "KW2238 : 'KW' '2238';\r\n" +
	                  "KW2239 : 'KW' '2239';\r\n" +
	                  "KW2240 : 'KW' '2240';\r\n" +
	                  "KW2241 : 'KW' '2241';\r\n" +
	                  "KW2242 : 'KW' '2242';\r\n" +
	                  "KW2243 : 'KW' '2243';\r\n" +
	                  "KW2244 : 'KW' '2244';\r\n" +
	                  "KW2245 : 'KW' '2245';\r\n" +
	                  "KW2246 : 'KW' '2246';\r\n" +
	                  "KW2247 : 'KW' '2247';\r\n" +
	                  "KW2248 : 'KW' '2248';\r\n" +
	                  "KW2249 : 'KW' '2249';\r\n" +
	                  "KW2250 : 'KW' '2250';\r\n" +
	                  "KW2251 : 'KW' '2251';\r\n" +
	                  "KW2252 : 'KW' '2252';\r\n" +
	                  "KW2253 : 'KW' '2253';\r\n" +
	                  "KW2254 : 'KW' '2254';\r\n" +
	                  "KW2255 : 'KW' '2255';\r\n" +
	                  "KW2256 : 'KW' '2256';\r\n" +
	                  "KW2257 : 'KW' '2257';\r\n" +
	                  "KW2258 : 'KW' '2258';\r\n" +
	                  "KW2259 : 'KW' '2259';\r\n" +
	                  "KW2260 : 'KW' '2260';\r\n" +
	                  "KW2261 : 'KW' '2261';\r\n" +
	                  "KW2262 : 'KW' '2262';\r\n" +
	                  "KW2263 : 'KW' '2263';\r\n" +
	                  "KW2264 : 'KW' '2264';\r\n" +
	                  "KW2265 : 'KW' '2265';\r\n" +
	                  "KW2266 : 'KW' '2266';\r\n" +
	                  "KW2267 : 'KW' '2267';\r\n" +
	                  "KW2268 : 'KW' '2268';\r\n" +
	                  "KW2269 : 'KW' '2269';\r\n" +
	                  "KW2270 : 'KW' '2270';\r\n" +
	                  "KW2271 : 'KW' '2271';\r\n" +
	                  "KW2272 : 'KW' '2272';\r\n" +
	                  "KW2273 : 'KW' '2273';\r\n" +
	                  "KW2274 : 'KW' '2274';\r\n" +
	                  "KW2275 : 'KW' '2275';\r\n" +
	                  "KW2276 : 'KW' '2276';\r\n" +
	                  "KW2277 : 'KW' '2277';\r\n" +
	                  "KW2278 : 'KW' '2278';\r\n" +
	                  "KW2279 : 'KW' '2279';\r\n" +
	                  "KW2280 : 'KW' '2280';\r\n" +
	                  "KW2281 : 'KW' '2281';\r\n" +
	                  "KW2282 : 'KW' '2282';\r\n" +
	                  "KW2283 : 'KW' '2283';\r\n" +
	                  "KW2284 : 'KW' '2284';\r\n" +
	                  "KW2285 : 'KW' '2285';\r\n" +
	                  "KW2286 : 'KW' '2286';\r\n" +
	                  "KW2287 : 'KW' '2287';\r\n" +
	                  "KW2288 : 'KW' '2288';\r\n" +
	                  "KW2289 : 'KW' '2289';\r\n" +
	                  "KW2290 : 'KW' '2290';\r\n" +
	                  "KW2291 : 'KW' '2291';\r\n" +
	                  "KW2292 : 'KW' '2292';\r\n" +
	                  "KW2293 : 'KW' '2293';\r\n" +
	                  "KW2294 : 'KW' '2294';\r\n" +
	                  "KW2295 : 'KW' '2295';\r\n" +
	                  "KW2296 : 'KW' '2296';\r\n" +
	                  "KW2297 : 'KW' '2297';\r\n" +
	                  "KW2298 : 'KW' '2298';\r\n" +
	                  "KW2299 : 'KW' '2299';\r\n" +
	                  "KW2300 : 'KW' '2300';\r\n" +
	                  "KW2301 : 'KW' '2301';\r\n" +
	                  "KW2302 : 'KW' '2302';\r\n" +
	                  "KW2303 : 'KW' '2303';\r\n" +
	                  "KW2304 : 'KW' '2304';\r\n" +
	                  "KW2305 : 'KW' '2305';\r\n" +
	                  "KW2306 : 'KW' '2306';\r\n" +
	                  "KW2307 : 'KW' '2307';\r\n" +
	                  "KW2308 : 'KW' '2308';\r\n" +
	                  "KW2309 : 'KW' '2309';\r\n" +
	                  "KW2310 : 'KW' '2310';\r\n" +
	                  "KW2311 : 'KW' '2311';\r\n" +
	                  "KW2312 : 'KW' '2312';\r\n" +
	                  "KW2313 : 'KW' '2313';\r\n" +
	                  "KW2314 : 'KW' '2314';\r\n" +
	                  "KW2315 : 'KW' '2315';\r\n" +
	                  "KW2316 : 'KW' '2316';\r\n" +
	                  "KW2317 : 'KW' '2317';\r\n" +
	                  "KW2318 : 'KW' '2318';\r\n" +
	                  "KW2319 : 'KW' '2319';\r\n" +
	                  "KW2320 : 'KW' '2320';\r\n" +
	                  "KW2321 : 'KW' '2321';\r\n" +
	                  "KW2322 : 'KW' '2322';\r\n" +
	                  "KW2323 : 'KW' '2323';\r\n" +
	                  "KW2324 : 'KW' '2324';\r\n" +
	                  "KW2325 : 'KW' '2325';\r\n" +
	                  "KW2326 : 'KW' '2326';\r\n" +
	                  "KW2327 : 'KW' '2327';\r\n" +
	                  "KW2328 : 'KW' '2328';\r\n" +
	                  "KW2329 : 'KW' '2329';\r\n" +
	                  "KW2330 : 'KW' '2330';\r\n" +
	                  "KW2331 : 'KW' '2331';\r\n" +
	                  "KW2332 : 'KW' '2332';\r\n" +
	                  "KW2333 : 'KW' '2333';\r\n" +
	                  "KW2334 : 'KW' '2334';\r\n" +
	                  "KW2335 : 'KW' '2335';\r\n" +
	                  "KW2336 : 'KW' '2336';\r\n" +
	                  "KW2337 : 'KW' '2337';\r\n" +
	                  "KW2338 : 'KW' '2338';\r\n" +
	                  "KW2339 : 'KW' '2339';\r\n" +
	                  "KW2340 : 'KW' '2340';\r\n" +
	                  "KW2341 : 'KW' '2341';\r\n" +
	                  "KW2342 : 'KW' '2342';\r\n" +
	                  "KW2343 : 'KW' '2343';\r\n" +
	                  "KW2344 : 'KW' '2344';\r\n" +
	                  "KW2345 : 'KW' '2345';\r\n" +
	                  "KW2346 : 'KW' '2346';\r\n" +
	                  "KW2347 : 'KW' '2347';\r\n" +
	                  "KW2348 : 'KW' '2348';\r\n" +
	                  "KW2349 : 'KW' '2349';\r\n" +
	                  "KW2350 : 'KW' '2350';\r\n" +
	                  "KW2351 : 'KW' '2351';\r\n" +
	                  "KW2352 : 'KW' '2352';\r\n" +
	                  "KW2353 : 'KW' '2353';\r\n" +
	                  "KW2354 : 'KW' '2354';\r\n" +
	                  "KW2355 : 'KW' '2355';\r\n" +
	                  "KW2356 : 'KW' '2356';\r\n" +
	                  "KW2357 : 'KW' '2357';\r\n" +
	                  "KW2358 : 'KW' '2358';\r\n" +
	                  "KW2359 : 'KW' '2359';\r\n" +
	                  "KW2360 : 'KW' '2360';\r\n" +
	                  "KW2361 : 'KW' '2361';\r\n" +
	                  "KW2362 : 'KW' '2362';\r\n" +
	                  "KW2363 : 'KW' '2363';\r\n" +
	                  "KW2364 : 'KW' '2364';\r\n" +
	                  "KW2365 : 'KW' '2365';\r\n" +
	                  "KW2366 : 'KW' '2366';\r\n" +
	                  "KW2367 : 'KW' '2367';\r\n" +
	                  "KW2368 : 'KW' '2368';\r\n" +
	                  "KW2369 : 'KW' '2369';\r\n" +
	                  "KW2370 : 'KW' '2370';\r\n" +
	                  "KW2371 : 'KW' '2371';\r\n" +
	                  "KW2372 : 'KW' '2372';\r\n" +
	                  "KW2373 : 'KW' '2373';\r\n" +
	                  "KW2374 : 'KW' '2374';\r\n" +
	                  "KW2375 : 'KW' '2375';\r\n" +
	                  "KW2376 : 'KW' '2376';\r\n" +
	                  "KW2377 : 'KW' '2377';\r\n" +
	                  "KW2378 : 'KW' '2378';\r\n" +
	                  "KW2379 : 'KW' '2379';\r\n" +
	                  "KW2380 : 'KW' '2380';\r\n" +
	                  "KW2381 : 'KW' '2381';\r\n" +
	                  "KW2382 : 'KW' '2382';\r\n" +
	                  "KW2383 : 'KW' '2383';\r\n" +
	                  "KW2384 : 'KW' '2384';\r\n" +
	                  "KW2385 : 'KW' '2385';\r\n" +
	                  "KW2386 : 'KW' '2386';\r\n" +
	                  "KW2387 : 'KW' '2387';\r\n" +
	                  "KW2388 : 'KW' '2388';\r\n" +
	                  "KW2389 : 'KW' '2389';\r\n" +
	                  "KW2390 : 'KW' '2390';\r\n" +
	                  "KW2391 : 'KW' '2391';\r\n" +
	                  "KW2392 : 'KW' '2392';\r\n" +
	                  "KW2393 : 'KW' '2393';\r\n" +
	                  "KW2394 : 'KW' '2394';\r\n" +
	                  "KW2395 : 'KW' '2395';\r\n" +
	                  "KW2396 : 'KW' '2396';\r\n" +
	                  "KW2397 : 'KW' '2397';\r\n" +
	                  "KW2398 : 'KW' '2398';\r\n" +
	                  "KW2399 : 'KW' '2399';\r\n" +
	                  "KW2400 : 'KW' '2400';\r\n" +
	                  "KW2401 : 'KW' '2401';\r\n" +
	                  "KW2402 : 'KW' '2402';\r\n" +
	                  "KW2403 : 'KW' '2403';\r\n" +
	                  "KW2404 : 'KW' '2404';\r\n" +
	                  "KW2405 : 'KW' '2405';\r\n" +
	                  "KW2406 : 'KW' '2406';\r\n" +
	                  "KW2407 : 'KW' '2407';\r\n" +
	                  "KW2408 : 'KW' '2408';\r\n" +
	                  "KW2409 : 'KW' '2409';\r\n" +
	                  "KW2410 : 'KW' '2410';\r\n" +
	                  "KW2411 : 'KW' '2411';\r\n" +
	                  "KW2412 : 'KW' '2412';\r\n" +
	                  "KW2413 : 'KW' '2413';\r\n" +
	                  "KW2414 : 'KW' '2414';\r\n" +
	                  "KW2415 : 'KW' '2415';\r\n" +
	                  "KW2416 : 'KW' '2416';\r\n" +
	                  "KW2417 : 'KW' '2417';\r\n" +
	                  "KW2418 : 'KW' '2418';\r\n" +
	                  "KW2419 : 'KW' '2419';\r\n" +
	                  "KW2420 : 'KW' '2420';\r\n" +
	                  "KW2421 : 'KW' '2421';\r\n" +
	                  "KW2422 : 'KW' '2422';\r\n" +
	                  "KW2423 : 'KW' '2423';\r\n" +
	                  "KW2424 : 'KW' '2424';\r\n" +
	                  "KW2425 : 'KW' '2425';\r\n" +
	                  "KW2426 : 'KW' '2426';\r\n" +
	                  "KW2427 : 'KW' '2427';\r\n" +
	                  "KW2428 : 'KW' '2428';\r\n" +
	                  "KW2429 : 'KW' '2429';\r\n" +
	                  "KW2430 : 'KW' '2430';\r\n" +
	                  "KW2431 : 'KW' '2431';\r\n" +
	                  "KW2432 : 'KW' '2432';\r\n" +
	                  "KW2433 : 'KW' '2433';\r\n" +
	                  "KW2434 : 'KW' '2434';\r\n" +
	                  "KW2435 : 'KW' '2435';\r\n" +
	                  "KW2436 : 'KW' '2436';\r\n" +
	                  "KW2437 : 'KW' '2437';\r\n" +
	                  "KW2438 : 'KW' '2438';\r\n" +
	                  "KW2439 : 'KW' '2439';\r\n" +
	                  "KW2440 : 'KW' '2440';\r\n" +
	                  "KW2441 : 'KW' '2441';\r\n" +
	                  "KW2442 : 'KW' '2442';\r\n" +
	                  "KW2443 : 'KW' '2443';\r\n" +
	                  "KW2444 : 'KW' '2444';\r\n" +
	                  "KW2445 : 'KW' '2445';\r\n" +
	                  "KW2446 : 'KW' '2446';\r\n" +
	                  "KW2447 : 'KW' '2447';\r\n" +
	                  "KW2448 : 'KW' '2448';\r\n" +
	                  "KW2449 : 'KW' '2449';\r\n" +
	                  "KW2450 : 'KW' '2450';\r\n" +
	                  "KW2451 : 'KW' '2451';\r\n" +
	                  "KW2452 : 'KW' '2452';\r\n" +
	                  "KW2453 : 'KW' '2453';\r\n" +
	                  "KW2454 : 'KW' '2454';\r\n" +
	                  "KW2455 : 'KW' '2455';\r\n" +
	                  "KW2456 : 'KW' '2456';\r\n" +
	                  "KW2457 : 'KW' '2457';\r\n" +
	                  "KW2458 : 'KW' '2458';\r\n" +
	                  "KW2459 : 'KW' '2459';\r\n" +
	                  "KW2460 : 'KW' '2460';\r\n" +
	                  "KW2461 : 'KW' '2461';\r\n" +
	                  "KW2462 : 'KW' '2462';\r\n" +
	                  "KW2463 : 'KW' '2463';\r\n" +
	                  "KW2464 : 'KW' '2464';\r\n" +
	                  "KW2465 : 'KW' '2465';\r\n" +
	                  "KW2466 : 'KW' '2466';\r\n" +
	                  "KW2467 : 'KW' '2467';\r\n" +
	                  "KW2468 : 'KW' '2468';\r\n" +
	                  "KW2469 : 'KW' '2469';\r\n" +
	                  "KW2470 : 'KW' '2470';\r\n" +
	                  "KW2471 : 'KW' '2471';\r\n" +
	                  "KW2472 : 'KW' '2472';\r\n" +
	                  "KW2473 : 'KW' '2473';\r\n" +
	                  "KW2474 : 'KW' '2474';\r\n" +
	                  "KW2475 : 'KW' '2475';\r\n" +
	                  "KW2476 : 'KW' '2476';\r\n" +
	                  "KW2477 : 'KW' '2477';\r\n" +
	                  "KW2478 : 'KW' '2478';\r\n" +
	                  "KW2479 : 'KW' '2479';\r\n" +
	                  "KW2480 : 'KW' '2480';\r\n" +
	                  "KW2481 : 'KW' '2481';\r\n" +
	                  "KW2482 : 'KW' '2482';\r\n" +
	                  "KW2483 : 'KW' '2483';\r\n" +
	                  "KW2484 : 'KW' '2484';\r\n" +
	                  "KW2485 : 'KW' '2485';\r\n" +
	                  "KW2486 : 'KW' '2486';\r\n" +
	                  "KW2487 : 'KW' '2487';\r\n" +
	                  "KW2488 : 'KW' '2488';\r\n" +
	                  "KW2489 : 'KW' '2489';\r\n" +
	                  "KW2490 : 'KW' '2490';\r\n" +
	                  "KW2491 : 'KW' '2491';\r\n" +
	                  "KW2492 : 'KW' '2492';\r\n" +
	                  "KW2493 : 'KW' '2493';\r\n" +
	                  "KW2494 : 'KW' '2494';\r\n" +
	                  "KW2495 : 'KW' '2495';\r\n" +
	                  "KW2496 : 'KW' '2496';\r\n" +
	                  "KW2497 : 'KW' '2497';\r\n" +
	                  "KW2498 : 'KW' '2498';\r\n" +
	                  "KW2499 : 'KW' '2499';\r\n" +
	                  "KW2500 : 'KW' '2500';\r\n" +
	                  "KW2501 : 'KW' '2501';\r\n" +
	                  "KW2502 : 'KW' '2502';\r\n" +
	                  "KW2503 : 'KW' '2503';\r\n" +
	                  "KW2504 : 'KW' '2504';\r\n" +
	                  "KW2505 : 'KW' '2505';\r\n" +
	                  "KW2506 : 'KW' '2506';\r\n" +
	                  "KW2507 : 'KW' '2507';\r\n" +
	                  "KW2508 : 'KW' '2508';\r\n" +
	                  "KW2509 : 'KW' '2509';\r\n" +
	                  "KW2510 : 'KW' '2510';\r\n" +
	                  "KW2511 : 'KW' '2511';\r\n" +
	                  "KW2512 : 'KW' '2512';\r\n" +
	                  "KW2513 : 'KW' '2513';\r\n" +
	                  "KW2514 : 'KW' '2514';\r\n" +
	                  "KW2515 : 'KW' '2515';\r\n" +
	                  "KW2516 : 'KW' '2516';\r\n" +
	                  "KW2517 : 'KW' '2517';\r\n" +
	                  "KW2518 : 'KW' '2518';\r\n" +
	                  "KW2519 : 'KW' '2519';\r\n" +
	                  "KW2520 : 'KW' '2520';\r\n" +
	                  "KW2521 : 'KW' '2521';\r\n" +
	                  "KW2522 : 'KW' '2522';\r\n" +
	                  "KW2523 : 'KW' '2523';\r\n" +
	                  "KW2524 : 'KW' '2524';\r\n" +
	                  "KW2525 : 'KW' '2525';\r\n" +
	                  "KW2526 : 'KW' '2526';\r\n" +
	                  "KW2527 : 'KW' '2527';\r\n" +
	                  "KW2528 : 'KW' '2528';\r\n" +
	                  "KW2529 : 'KW' '2529';\r\n" +
	                  "KW2530 : 'KW' '2530';\r\n" +
	                  "KW2531 : 'KW' '2531';\r\n" +
	                  "KW2532 : 'KW' '2532';\r\n" +
	                  "KW2533 : 'KW' '2533';\r\n" +
	                  "KW2534 : 'KW' '2534';\r\n" +
	                  "KW2535 : 'KW' '2535';\r\n" +
	                  "KW2536 : 'KW' '2536';\r\n" +
	                  "KW2537 : 'KW' '2537';\r\n" +
	                  "KW2538 : 'KW' '2538';\r\n" +
	                  "KW2539 : 'KW' '2539';\r\n" +
	                  "KW2540 : 'KW' '2540';\r\n" +
	                  "KW2541 : 'KW' '2541';\r\n" +
	                  "KW2542 : 'KW' '2542';\r\n" +
	                  "KW2543 : 'KW' '2543';\r\n" +
	                  "KW2544 : 'KW' '2544';\r\n" +
	                  "KW2545 : 'KW' '2545';\r\n" +
	                  "KW2546 : 'KW' '2546';\r\n" +
	                  "KW2547 : 'KW' '2547';\r\n" +
	                  "KW2548 : 'KW' '2548';\r\n" +
	                  "KW2549 : 'KW' '2549';\r\n" +
	                  "KW2550 : 'KW' '2550';\r\n" +
	                  "KW2551 : 'KW' '2551';\r\n" +
	                  "KW2552 : 'KW' '2552';\r\n" +
	                  "KW2553 : 'KW' '2553';\r\n" +
	                  "KW2554 : 'KW' '2554';\r\n" +
	                  "KW2555 : 'KW' '2555';\r\n" +
	                  "KW2556 : 'KW' '2556';\r\n" +
	                  "KW2557 : 'KW' '2557';\r\n" +
	                  "KW2558 : 'KW' '2558';\r\n" +
	                  "KW2559 : 'KW' '2559';\r\n" +
	                  "KW2560 : 'KW' '2560';\r\n" +
	                  "KW2561 : 'KW' '2561';\r\n" +
	                  "KW2562 : 'KW' '2562';\r\n" +
	                  "KW2563 : 'KW' '2563';\r\n" +
	                  "KW2564 : 'KW' '2564';\r\n" +
	                  "KW2565 : 'KW' '2565';\r\n" +
	                  "KW2566 : 'KW' '2566';\r\n" +
	                  "KW2567 : 'KW' '2567';\r\n" +
	                  "KW2568 : 'KW' '2568';\r\n" +
	                  "KW2569 : 'KW' '2569';\r\n" +
	                  "KW2570 : 'KW' '2570';\r\n" +
	                  "KW2571 : 'KW' '2571';\r\n" +
	                  "KW2572 : 'KW' '2572';\r\n" +
	                  "KW2573 : 'KW' '2573';\r\n" +
	                  "KW2574 : 'KW' '2574';\r\n" +
	                  "KW2575 : 'KW' '2575';\r\n" +
	                  "KW2576 : 'KW' '2576';\r\n" +
	                  "KW2577 : 'KW' '2577';\r\n" +
	                  "KW2578 : 'KW' '2578';\r\n" +
	                  "KW2579 : 'KW' '2579';\r\n" +
	                  "KW2580 : 'KW' '2580';\r\n" +
	                  "KW2581 : 'KW' '2581';\r\n" +
	                  "KW2582 : 'KW' '2582';\r\n" +
	                  "KW2583 : 'KW' '2583';\r\n" +
	                  "KW2584 : 'KW' '2584';\r\n" +
	                  "KW2585 : 'KW' '2585';\r\n" +
	                  "KW2586 : 'KW' '2586';\r\n" +
	                  "KW2587 : 'KW' '2587';\r\n" +
	                  "KW2588 : 'KW' '2588';\r\n" +
	                  "KW2589 : 'KW' '2589';\r\n" +
	                  "KW2590 : 'KW' '2590';\r\n" +
	                  "KW2591 : 'KW' '2591';\r\n" +
	                  "KW2592 : 'KW' '2592';\r\n" +
	                  "KW2593 : 'KW' '2593';\r\n" +
	                  "KW2594 : 'KW' '2594';\r\n" +
	                  "KW2595 : 'KW' '2595';\r\n" +
	                  "KW2596 : 'KW' '2596';\r\n" +
	                  "KW2597 : 'KW' '2597';\r\n" +
	                  "KW2598 : 'KW' '2598';\r\n" +
	                  "KW2599 : 'KW' '2599';\r\n" +
	                  "KW2600 : 'KW' '2600';\r\n" +
	                  "KW2601 : 'KW' '2601';\r\n" +
	                  "KW2602 : 'KW' '2602';\r\n" +
	                  "KW2603 : 'KW' '2603';\r\n" +
	                  "KW2604 : 'KW' '2604';\r\n" +
	                  "KW2605 : 'KW' '2605';\r\n" +
	                  "KW2606 : 'KW' '2606';\r\n" +
	                  "KW2607 : 'KW' '2607';\r\n" +
	                  "KW2608 : 'KW' '2608';\r\n" +
	                  "KW2609 : 'KW' '2609';\r\n" +
	                  "KW2610 : 'KW' '2610';\r\n" +
	                  "KW2611 : 'KW' '2611';\r\n" +
	                  "KW2612 : 'KW' '2612';\r\n" +
	                  "KW2613 : 'KW' '2613';\r\n" +
	                  "KW2614 : 'KW' '2614';\r\n" +
	                  "KW2615 : 'KW' '2615';\r\n" +
	                  "KW2616 : 'KW' '2616';\r\n" +
	                  "KW2617 : 'KW' '2617';\r\n" +
	                  "KW2618 : 'KW' '2618';\r\n" +
	                  "KW2619 : 'KW' '2619';\r\n" +
	                  "KW2620 : 'KW' '2620';\r\n" +
	                  "KW2621 : 'KW' '2621';\r\n" +
	                  "KW2622 : 'KW' '2622';\r\n" +
	                  "KW2623 : 'KW' '2623';\r\n" +
	                  "KW2624 : 'KW' '2624';\r\n" +
	                  "KW2625 : 'KW' '2625';\r\n" +
	                  "KW2626 : 'KW' '2626';\r\n" +
	                  "KW2627 : 'KW' '2627';\r\n" +
	                  "KW2628 : 'KW' '2628';\r\n" +
	                  "KW2629 : 'KW' '2629';\r\n" +
	                  "KW2630 : 'KW' '2630';\r\n" +
	                  "KW2631 : 'KW' '2631';\r\n" +
	                  "KW2632 : 'KW' '2632';\r\n" +
	                  "KW2633 : 'KW' '2633';\r\n" +
	                  "KW2634 : 'KW' '2634';\r\n" +
	                  "KW2635 : 'KW' '2635';\r\n" +
	                  "KW2636 : 'KW' '2636';\r\n" +
	                  "KW2637 : 'KW' '2637';\r\n" +
	                  "KW2638 : 'KW' '2638';\r\n" +
	                  "KW2639 : 'KW' '2639';\r\n" +
	                  "KW2640 : 'KW' '2640';\r\n" +
	                  "KW2641 : 'KW' '2641';\r\n" +
	                  "KW2642 : 'KW' '2642';\r\n" +
	                  "KW2643 : 'KW' '2643';\r\n" +
	                  "KW2644 : 'KW' '2644';\r\n" +
	                  "KW2645 : 'KW' '2645';\r\n" +
	                  "KW2646 : 'KW' '2646';\r\n" +
	                  "KW2647 : 'KW' '2647';\r\n" +
	                  "KW2648 : 'KW' '2648';\r\n" +
	                  "KW2649 : 'KW' '2649';\r\n" +
	                  "KW2650 : 'KW' '2650';\r\n" +
	                  "KW2651 : 'KW' '2651';\r\n" +
	                  "KW2652 : 'KW' '2652';\r\n" +
	                  "KW2653 : 'KW' '2653';\r\n" +
	                  "KW2654 : 'KW' '2654';\r\n" +
	                  "KW2655 : 'KW' '2655';\r\n" +
	                  "KW2656 : 'KW' '2656';\r\n" +
	                  "KW2657 : 'KW' '2657';\r\n" +
	                  "KW2658 : 'KW' '2658';\r\n" +
	                  "KW2659 : 'KW' '2659';\r\n" +
	                  "KW2660 : 'KW' '2660';\r\n" +
	                  "KW2661 : 'KW' '2661';\r\n" +
	                  "KW2662 : 'KW' '2662';\r\n" +
	                  "KW2663 : 'KW' '2663';\r\n" +
	                  "KW2664 : 'KW' '2664';\r\n" +
	                  "KW2665 : 'KW' '2665';\r\n" +
	                  "KW2666 : 'KW' '2666';\r\n" +
	                  "KW2667 : 'KW' '2667';\r\n" +
	                  "KW2668 : 'KW' '2668';\r\n" +
	                  "KW2669 : 'KW' '2669';\r\n" +
	                  "KW2670 : 'KW' '2670';\r\n" +
	                  "KW2671 : 'KW' '2671';\r\n" +
	                  "KW2672 : 'KW' '2672';\r\n" +
	                  "KW2673 : 'KW' '2673';\r\n" +
	                  "KW2674 : 'KW' '2674';\r\n" +
	                  "KW2675 : 'KW' '2675';\r\n" +
	                  "KW2676 : 'KW' '2676';\r\n" +
	                  "KW2677 : 'KW' '2677';\r\n" +
	                  "KW2678 : 'KW' '2678';\r\n" +
	                  "KW2679 : 'KW' '2679';\r\n" +
	                  "KW2680 : 'KW' '2680';\r\n" +
	                  "KW2681 : 'KW' '2681';\r\n" +
	                  "KW2682 : 'KW' '2682';\r\n" +
	                  "KW2683 : 'KW' '2683';\r\n" +
	                  "KW2684 : 'KW' '2684';\r\n" +
	                  "KW2685 : 'KW' '2685';\r\n" +
	                  "KW2686 : 'KW' '2686';\r\n" +
	                  "KW2687 : 'KW' '2687';\r\n" +
	                  "KW2688 : 'KW' '2688';\r\n" +
	                  "KW2689 : 'KW' '2689';\r\n" +
	                  "KW2690 : 'KW' '2690';\r\n" +
	                  "KW2691 : 'KW' '2691';\r\n" +
	                  "KW2692 : 'KW' '2692';\r\n" +
	                  "KW2693 : 'KW' '2693';\r\n" +
	                  "KW2694 : 'KW' '2694';\r\n" +
	                  "KW2695 : 'KW' '2695';\r\n" +
	                  "KW2696 : 'KW' '2696';\r\n" +
	                  "KW2697 : 'KW' '2697';\r\n" +
	                  "KW2698 : 'KW' '2698';\r\n" +
	                  "KW2699 : 'KW' '2699';\r\n" +
	                  "KW2700 : 'KW' '2700';\r\n" +
	                  "KW2701 : 'KW' '2701';\r\n" +
	                  "KW2702 : 'KW' '2702';\r\n" +
	                  "KW2703 : 'KW' '2703';\r\n" +
	                  "KW2704 : 'KW' '2704';\r\n" +
	                  "KW2705 : 'KW' '2705';\r\n" +
	                  "KW2706 : 'KW' '2706';\r\n" +
	                  "KW2707 : 'KW' '2707';\r\n" +
	                  "KW2708 : 'KW' '2708';\r\n" +
	                  "KW2709 : 'KW' '2709';\r\n" +
	                  "KW2710 : 'KW' '2710';\r\n" +
	                  "KW2711 : 'KW' '2711';\r\n" +
	                  "KW2712 : 'KW' '2712';\r\n" +
	                  "KW2713 : 'KW' '2713';\r\n" +
	                  "KW2714 : 'KW' '2714';\r\n" +
	                  "KW2715 : 'KW' '2715';\r\n" +
	                  "KW2716 : 'KW' '2716';\r\n" +
	                  "KW2717 : 'KW' '2717';\r\n" +
	                  "KW2718 : 'KW' '2718';\r\n" +
	                  "KW2719 : 'KW' '2719';\r\n" +
	                  "KW2720 : 'KW' '2720';\r\n" +
	                  "KW2721 : 'KW' '2721';\r\n" +
	                  "KW2722 : 'KW' '2722';\r\n" +
	                  "KW2723 : 'KW' '2723';\r\n" +
	                  "KW2724 : 'KW' '2724';\r\n" +
	                  "KW2725 : 'KW' '2725';\r\n" +
	                  "KW2726 : 'KW' '2726';\r\n" +
	                  "KW2727 : 'KW' '2727';\r\n" +
	                  "KW2728 : 'KW' '2728';\r\n" +
	                  "KW2729 : 'KW' '2729';\r\n" +
	                  "KW2730 : 'KW' '2730';\r\n" +
	                  "KW2731 : 'KW' '2731';\r\n" +
	                  "KW2732 : 'KW' '2732';\r\n" +
	                  "KW2733 : 'KW' '2733';\r\n" +
	                  "KW2734 : 'KW' '2734';\r\n" +
	                  "KW2735 : 'KW' '2735';\r\n" +
	                  "KW2736 : 'KW' '2736';\r\n" +
	                  "KW2737 : 'KW' '2737';\r\n" +
	                  "KW2738 : 'KW' '2738';\r\n" +
	                  "KW2739 : 'KW' '2739';\r\n" +
	                  "KW2740 : 'KW' '2740';\r\n" +
	                  "KW2741 : 'KW' '2741';\r\n" +
	                  "KW2742 : 'KW' '2742';\r\n" +
	                  "KW2743 : 'KW' '2743';\r\n" +
	                  "KW2744 : 'KW' '2744';\r\n" +
	                  "KW2745 : 'KW' '2745';\r\n" +
	                  "KW2746 : 'KW' '2746';\r\n" +
	                  "KW2747 : 'KW' '2747';\r\n" +
	                  "KW2748 : 'KW' '2748';\r\n" +
	                  "KW2749 : 'KW' '2749';\r\n" +
	                  "KW2750 : 'KW' '2750';\r\n" +
	                  "KW2751 : 'KW' '2751';\r\n" +
	                  "KW2752 : 'KW' '2752';\r\n" +
	                  "KW2753 : 'KW' '2753';\r\n" +
	                  "KW2754 : 'KW' '2754';\r\n" +
	                  "KW2755 : 'KW' '2755';\r\n" +
	                  "KW2756 : 'KW' '2756';\r\n" +
	                  "KW2757 : 'KW' '2757';\r\n" +
	                  "KW2758 : 'KW' '2758';\r\n" +
	                  "KW2759 : 'KW' '2759';\r\n" +
	                  "KW2760 : 'KW' '2760';\r\n" +
	                  "KW2761 : 'KW' '2761';\r\n" +
	                  "KW2762 : 'KW' '2762';\r\n" +
	                  "KW2763 : 'KW' '2763';\r\n" +
	                  "KW2764 : 'KW' '2764';\r\n" +
	                  "KW2765 : 'KW' '2765';\r\n" +
	                  "KW2766 : 'KW' '2766';\r\n" +
	                  "KW2767 : 'KW' '2767';\r\n" +
	                  "KW2768 : 'KW' '2768';\r\n" +
	                  "KW2769 : 'KW' '2769';\r\n" +
	                  "KW2770 : 'KW' '2770';\r\n" +
	                  "KW2771 : 'KW' '2771';\r\n" +
	                  "KW2772 : 'KW' '2772';\r\n" +
	                  "KW2773 : 'KW' '2773';\r\n" +
	                  "KW2774 : 'KW' '2774';\r\n" +
	                  "KW2775 : 'KW' '2775';\r\n" +
	                  "KW2776 : 'KW' '2776';\r\n" +
	                  "KW2777 : 'KW' '2777';\r\n" +
	                  "KW2778 : 'KW' '2778';\r\n" +
	                  "KW2779 : 'KW' '2779';\r\n" +
	                  "KW2780 : 'KW' '2780';\r\n" +
	                  "KW2781 : 'KW' '2781';\r\n" +
	                  "KW2782 : 'KW' '2782';\r\n" +
	                  "KW2783 : 'KW' '2783';\r\n" +
	                  "KW2784 : 'KW' '2784';\r\n" +
	                  "KW2785 : 'KW' '2785';\r\n" +
	                  "KW2786 : 'KW' '2786';\r\n" +
	                  "KW2787 : 'KW' '2787';\r\n" +
	                  "KW2788 : 'KW' '2788';\r\n" +
	                  "KW2789 : 'KW' '2789';\r\n" +
	                  "KW2790 : 'KW' '2790';\r\n" +
	                  "KW2791 : 'KW' '2791';\r\n" +
	                  "KW2792 : 'KW' '2792';\r\n" +
	                  "KW2793 : 'KW' '2793';\r\n" +
	                  "KW2794 : 'KW' '2794';\r\n" +
	                  "KW2795 : 'KW' '2795';\r\n" +
	                  "KW2796 : 'KW' '2796';\r\n" +
	                  "KW2797 : 'KW' '2797';\r\n" +
	                  "KW2798 : 'KW' '2798';\r\n" +
	                  "KW2799 : 'KW' '2799';\r\n" +
	                  "KW2800 : 'KW' '2800';\r\n" +
	                  "KW2801 : 'KW' '2801';\r\n" +
	                  "KW2802 : 'KW' '2802';\r\n" +
	                  "KW2803 : 'KW' '2803';\r\n" +
	                  "KW2804 : 'KW' '2804';\r\n" +
	                  "KW2805 : 'KW' '2805';\r\n" +
	                  "KW2806 : 'KW' '2806';\r\n" +
	                  "KW2807 : 'KW' '2807';\r\n" +
	                  "KW2808 : 'KW' '2808';\r\n" +
	                  "KW2809 : 'KW' '2809';\r\n" +
	                  "KW2810 : 'KW' '2810';\r\n" +
	                  "KW2811 : 'KW' '2811';\r\n" +
	                  "KW2812 : 'KW' '2812';\r\n" +
	                  "KW2813 : 'KW' '2813';\r\n" +
	                  "KW2814 : 'KW' '2814';\r\n" +
	                  "KW2815 : 'KW' '2815';\r\n" +
	                  "KW2816 : 'KW' '2816';\r\n" +
	                  "KW2817 : 'KW' '2817';\r\n" +
	                  "KW2818 : 'KW' '2818';\r\n" +
	                  "KW2819 : 'KW' '2819';\r\n" +
	                  "KW2820 : 'KW' '2820';\r\n" +
	                  "KW2821 : 'KW' '2821';\r\n" +
	                  "KW2822 : 'KW' '2822';\r\n" +
	                  "KW2823 : 'KW' '2823';\r\n" +
	                  "KW2824 : 'KW' '2824';\r\n" +
	                  "KW2825 : 'KW' '2825';\r\n" +
	                  "KW2826 : 'KW' '2826';\r\n" +
	                  "KW2827 : 'KW' '2827';\r\n" +
	                  "KW2828 : 'KW' '2828';\r\n" +
	                  "KW2829 : 'KW' '2829';\r\n" +
	                  "KW2830 : 'KW' '2830';\r\n" +
	                  "KW2831 : 'KW' '2831';\r\n" +
	                  "KW2832 : 'KW' '2832';\r\n" +
	                  "KW2833 : 'KW' '2833';\r\n" +
	                  "KW2834 : 'KW' '2834';\r\n" +
	                  "KW2835 : 'KW' '2835';\r\n" +
	                  "KW2836 : 'KW' '2836';\r\n" +
	                  "KW2837 : 'KW' '2837';\r\n" +
	                  "KW2838 : 'KW' '2838';\r\n" +
	                  "KW2839 : 'KW' '2839';\r\n" +
	                  "KW2840 : 'KW' '2840';\r\n" +
	                  "KW2841 : 'KW' '2841';\r\n" +
	                  "KW2842 : 'KW' '2842';\r\n" +
	                  "KW2843 : 'KW' '2843';\r\n" +
	                  "KW2844 : 'KW' '2844';\r\n" +
	                  "KW2845 : 'KW' '2845';\r\n" +
	                  "KW2846 : 'KW' '2846';\r\n" +
	                  "KW2847 : 'KW' '2847';\r\n" +
	                  "KW2848 : 'KW' '2848';\r\n" +
	                  "KW2849 : 'KW' '2849';\r\n" +
	                  "KW2850 : 'KW' '2850';\r\n" +
	                  "KW2851 : 'KW' '2851';\r\n" +
	                  "KW2852 : 'KW' '2852';\r\n" +
	                  "KW2853 : 'KW' '2853';\r\n" +
	                  "KW2854 : 'KW' '2854';\r\n" +
	                  "KW2855 : 'KW' '2855';\r\n" +
	                  "KW2856 : 'KW' '2856';\r\n" +
	                  "KW2857 : 'KW' '2857';\r\n" +
	                  "KW2858 : 'KW' '2858';\r\n" +
	                  "KW2859 : 'KW' '2859';\r\n" +
	                  "KW2860 : 'KW' '2860';\r\n" +
	                  "KW2861 : 'KW' '2861';\r\n" +
	                  "KW2862 : 'KW' '2862';\r\n" +
	                  "KW2863 : 'KW' '2863';\r\n" +
	                  "KW2864 : 'KW' '2864';\r\n" +
	                  "KW2865 : 'KW' '2865';\r\n" +
	                  "KW2866 : 'KW' '2866';\r\n" +
	                  "KW2867 : 'KW' '2867';\r\n" +
	                  "KW2868 : 'KW' '2868';\r\n" +
	                  "KW2869 : 'KW' '2869';\r\n" +
	                  "KW2870 : 'KW' '2870';\r\n" +
	                  "KW2871 : 'KW' '2871';\r\n" +
	                  "KW2872 : 'KW' '2872';\r\n" +
	                  "KW2873 : 'KW' '2873';\r\n" +
	                  "KW2874 : 'KW' '2874';\r\n" +
	                  "KW2875 : 'KW' '2875';\r\n" +
	                  "KW2876 : 'KW' '2876';\r\n" +
	                  "KW2877 : 'KW' '2877';\r\n" +
	                  "KW2878 : 'KW' '2878';\r\n" +
	                  "KW2879 : 'KW' '2879';\r\n" +
	                  "KW2880 : 'KW' '2880';\r\n" +
	                  "KW2881 : 'KW' '2881';\r\n" +
	                  "KW2882 : 'KW' '2882';\r\n" +
	                  "KW2883 : 'KW' '2883';\r\n" +
	                  "KW2884 : 'KW' '2884';\r\n" +
	                  "KW2885 : 'KW' '2885';\r\n" +
	                  "KW2886 : 'KW' '2886';\r\n" +
	                  "KW2887 : 'KW' '2887';\r\n" +
	                  "KW2888 : 'KW' '2888';\r\n" +
	                  "KW2889 : 'KW' '2889';\r\n" +
	                  "KW2890 : 'KW' '2890';\r\n" +
	                  "KW2891 : 'KW' '2891';\r\n" +
	                  "KW2892 : 'KW' '2892';\r\n" +
	                  "KW2893 : 'KW' '2893';\r\n" +
	                  "KW2894 : 'KW' '2894';\r\n" +
	                  "KW2895 : 'KW' '2895';\r\n" +
	                  "KW2896 : 'KW' '2896';\r\n" +
	                  "KW2897 : 'KW' '2897';\r\n" +
	                  "KW2898 : 'KW' '2898';\r\n" +
	                  "KW2899 : 'KW' '2899';\r\n" +
	                  "KW2900 : 'KW' '2900';\r\n" +
	                  "KW2901 : 'KW' '2901';\r\n" +
	                  "KW2902 : 'KW' '2902';\r\n" +
	                  "KW2903 : 'KW' '2903';\r\n" +
	                  "KW2904 : 'KW' '2904';\r\n" +
	                  "KW2905 : 'KW' '2905';\r\n" +
	                  "KW2906 : 'KW' '2906';\r\n" +
	                  "KW2907 : 'KW' '2907';\r\n" +
	                  "KW2908 : 'KW' '2908';\r\n" +
	                  "KW2909 : 'KW' '2909';\r\n" +
	                  "KW2910 : 'KW' '2910';\r\n" +
	                  "KW2911 : 'KW' '2911';\r\n" +
	                  "KW2912 : 'KW' '2912';\r\n" +
	                  "KW2913 : 'KW' '2913';\r\n" +
	                  "KW2914 : 'KW' '2914';\r\n" +
	                  "KW2915 : 'KW' '2915';\r\n" +
	                  "KW2916 : 'KW' '2916';\r\n" +
	                  "KW2917 : 'KW' '2917';\r\n" +
	                  "KW2918 : 'KW' '2918';\r\n" +
	                  "KW2919 : 'KW' '2919';\r\n" +
	                  "KW2920 : 'KW' '2920';\r\n" +
	                  "KW2921 : 'KW' '2921';\r\n" +
	                  "KW2922 : 'KW' '2922';\r\n" +
	                  "KW2923 : 'KW' '2923';\r\n" +
	                  "KW2924 : 'KW' '2924';\r\n" +
	                  "KW2925 : 'KW' '2925';\r\n" +
	                  "KW2926 : 'KW' '2926';\r\n" +
	                  "KW2927 : 'KW' '2927';\r\n" +
	                  "KW2928 : 'KW' '2928';\r\n" +
	                  "KW2929 : 'KW' '2929';\r\n" +
	                  "KW2930 : 'KW' '2930';\r\n" +
	                  "KW2931 : 'KW' '2931';\r\n" +
	                  "KW2932 : 'KW' '2932';\r\n" +
	                  "KW2933 : 'KW' '2933';\r\n" +
	                  "KW2934 : 'KW' '2934';\r\n" +
	                  "KW2935 : 'KW' '2935';\r\n" +
	                  "KW2936 : 'KW' '2936';\r\n" +
	                  "KW2937 : 'KW' '2937';\r\n" +
	                  "KW2938 : 'KW' '2938';\r\n" +
	                  "KW2939 : 'KW' '2939';\r\n" +
	                  "KW2940 : 'KW' '2940';\r\n" +
	                  "KW2941 : 'KW' '2941';\r\n" +
	                  "KW2942 : 'KW' '2942';\r\n" +
	                  "KW2943 : 'KW' '2943';\r\n" +
	                  "KW2944 : 'KW' '2944';\r\n" +
	                  "KW2945 : 'KW' '2945';\r\n" +
	                  "KW2946 : 'KW' '2946';\r\n" +
	                  "KW2947 : 'KW' '2947';\r\n" +
	                  "KW2948 : 'KW' '2948';\r\n" +
	                  "KW2949 : 'KW' '2949';\r\n" +
	                  "KW2950 : 'KW' '2950';\r\n" +
	                  "KW2951 : 'KW' '2951';\r\n" +
	                  "KW2952 : 'KW' '2952';\r\n" +
	                  "KW2953 : 'KW' '2953';\r\n" +
	                  "KW2954 : 'KW' '2954';\r\n" +
	                  "KW2955 : 'KW' '2955';\r\n" +
	                  "KW2956 : 'KW' '2956';\r\n" +
	                  "KW2957 : 'KW' '2957';\r\n" +
	                  "KW2958 : 'KW' '2958';\r\n" +
	                  "KW2959 : 'KW' '2959';\r\n" +
	                  "KW2960 : 'KW' '2960';\r\n" +
	                  "KW2961 : 'KW' '2961';\r\n" +
	                  "KW2962 : 'KW' '2962';\r\n" +
	                  "KW2963 : 'KW' '2963';\r\n" +
	                  "KW2964 : 'KW' '2964';\r\n" +
	                  "KW2965 : 'KW' '2965';\r\n" +
	                  "KW2966 : 'KW' '2966';\r\n" +
	                  "KW2967 : 'KW' '2967';\r\n" +
	                  "KW2968 : 'KW' '2968';\r\n" +
	                  "KW2969 : 'KW' '2969';\r\n" +
	                  "KW2970 : 'KW' '2970';\r\n" +
	                  "KW2971 : 'KW' '2971';\r\n" +
	                  "KW2972 : 'KW' '2972';\r\n" +
	                  "KW2973 : 'KW' '2973';\r\n" +
	                  "KW2974 : 'KW' '2974';\r\n" +
	                  "KW2975 : 'KW' '2975';\r\n" +
	                  "KW2976 : 'KW' '2976';\r\n" +
	                  "KW2977 : 'KW' '2977';\r\n" +
	                  "KW2978 : 'KW' '2978';\r\n" +
	                  "KW2979 : 'KW' '2979';\r\n" +
	                  "KW2980 : 'KW' '2980';\r\n" +
	                  "KW2981 : 'KW' '2981';\r\n" +
	                  "KW2982 : 'KW' '2982';\r\n" +
	                  "KW2983 : 'KW' '2983';\r\n" +
	                  "KW2984 : 'KW' '2984';\r\n" +
	                  "KW2985 : 'KW' '2985';\r\n" +
	                  "KW2986 : 'KW' '2986';\r\n" +
	                  "KW2987 : 'KW' '2987';\r\n" +
	                  "KW2988 : 'KW' '2988';\r\n" +
	                  "KW2989 : 'KW' '2989';\r\n" +
	                  "KW2990 : 'KW' '2990';\r\n" +
	                  "KW2991 : 'KW' '2991';\r\n" +
	                  "KW2992 : 'KW' '2992';\r\n" +
	                  "KW2993 : 'KW' '2993';\r\n" +
	                  "KW2994 : 'KW' '2994';\r\n" +
	                  "KW2995 : 'KW' '2995';\r\n" +
	                  "KW2996 : 'KW' '2996';\r\n" +
	                  "KW2997 : 'KW' '2997';\r\n" +
	                  "KW2998 : 'KW' '2998';\r\n" +
	                  "KW2999 : 'KW' '2999';\r\n" +
	                  "KW3000 : 'KW' '3000';\r\n" +
	                  "KW3001 : 'KW' '3001';\r\n" +
	                  "KW3002 : 'KW' '3002';\r\n" +
	                  "KW3003 : 'KW' '3003';\r\n" +
	                  "KW3004 : 'KW' '3004';\r\n" +
	                  "KW3005 : 'KW' '3005';\r\n" +
	                  "KW3006 : 'KW' '3006';\r\n" +
	                  "KW3007 : 'KW' '3007';\r\n" +
	                  "KW3008 : 'KW' '3008';\r\n" +
	                  "KW3009 : 'KW' '3009';\r\n" +
	                  "KW3010 : 'KW' '3010';\r\n" +
	                  "KW3011 : 'KW' '3011';\r\n" +
	                  "KW3012 : 'KW' '3012';\r\n" +
	                  "KW3013 : 'KW' '3013';\r\n" +
	                  "KW3014 : 'KW' '3014';\r\n" +
	                  "KW3015 : 'KW' '3015';\r\n" +
	                  "KW3016 : 'KW' '3016';\r\n" +
	                  "KW3017 : 'KW' '3017';\r\n" +
	                  "KW3018 : 'KW' '3018';\r\n" +
	                  "KW3019 : 'KW' '3019';\r\n" +
	                  "KW3020 : 'KW' '3020';\r\n" +
	                  "KW3021 : 'KW' '3021';\r\n" +
	                  "KW3022 : 'KW' '3022';\r\n" +
	                  "KW3023 : 'KW' '3023';\r\n" +
	                  "KW3024 : 'KW' '3024';\r\n" +
	                  "KW3025 : 'KW' '3025';\r\n" +
	                  "KW3026 : 'KW' '3026';\r\n" +
	                  "KW3027 : 'KW' '3027';\r\n" +
	                  "KW3028 : 'KW' '3028';\r\n" +
	                  "KW3029 : 'KW' '3029';\r\n" +
	                  "KW3030 : 'KW' '3030';\r\n" +
	                  "KW3031 : 'KW' '3031';\r\n" +
	                  "KW3032 : 'KW' '3032';\r\n" +
	                  "KW3033 : 'KW' '3033';\r\n" +
	                  "KW3034 : 'KW' '3034';\r\n" +
	                  "KW3035 : 'KW' '3035';\r\n" +
	                  "KW3036 : 'KW' '3036';\r\n" +
	                  "KW3037 : 'KW' '3037';\r\n" +
	                  "KW3038 : 'KW' '3038';\r\n" +
	                  "KW3039 : 'KW' '3039';\r\n" +
	                  "KW3040 : 'KW' '3040';\r\n" +
	                  "KW3041 : 'KW' '3041';\r\n" +
	                  "KW3042 : 'KW' '3042';\r\n" +
	                  "KW3043 : 'KW' '3043';\r\n" +
	                  "KW3044 : 'KW' '3044';\r\n" +
	                  "KW3045 : 'KW' '3045';\r\n" +
	                  "KW3046 : 'KW' '3046';\r\n" +
	                  "KW3047 : 'KW' '3047';\r\n" +
	                  "KW3048 : 'KW' '3048';\r\n" +
	                  "KW3049 : 'KW' '3049';\r\n" +
	                  "KW3050 : 'KW' '3050';\r\n" +
	                  "KW3051 : 'KW' '3051';\r\n" +
	                  "KW3052 : 'KW' '3052';\r\n" +
	                  "KW3053 : 'KW' '3053';\r\n" +
	                  "KW3054 : 'KW' '3054';\r\n" +
	                  "KW3055 : 'KW' '3055';\r\n" +
	                  "KW3056 : 'KW' '3056';\r\n" +
	                  "KW3057 : 'KW' '3057';\r\n" +
	                  "KW3058 : 'KW' '3058';\r\n" +
	                  "KW3059 : 'KW' '3059';\r\n" +
	                  "KW3060 : 'KW' '3060';\r\n" +
	                  "KW3061 : 'KW' '3061';\r\n" +
	                  "KW3062 : 'KW' '3062';\r\n" +
	                  "KW3063 : 'KW' '3063';\r\n" +
	                  "KW3064 : 'KW' '3064';\r\n" +
	                  "KW3065 : 'KW' '3065';\r\n" +
	                  "KW3066 : 'KW' '3066';\r\n" +
	                  "KW3067 : 'KW' '3067';\r\n" +
	                  "KW3068 : 'KW' '3068';\r\n" +
	                  "KW3069 : 'KW' '3069';\r\n" +
	                  "KW3070 : 'KW' '3070';\r\n" +
	                  "KW3071 : 'KW' '3071';\r\n" +
	                  "KW3072 : 'KW' '3072';\r\n" +
	                  "KW3073 : 'KW' '3073';\r\n" +
	                  "KW3074 : 'KW' '3074';\r\n" +
	                  "KW3075 : 'KW' '3075';\r\n" +
	                  "KW3076 : 'KW' '3076';\r\n" +
	                  "KW3077 : 'KW' '3077';\r\n" +
	                  "KW3078 : 'KW' '3078';\r\n" +
	                  "KW3079 : 'KW' '3079';\r\n" +
	                  "KW3080 : 'KW' '3080';\r\n" +
	                  "KW3081 : 'KW' '3081';\r\n" +
	                  "KW3082 : 'KW' '3082';\r\n" +
	                  "KW3083 : 'KW' '3083';\r\n" +
	                  "KW3084 : 'KW' '3084';\r\n" +
	                  "KW3085 : 'KW' '3085';\r\n" +
	                  "KW3086 : 'KW' '3086';\r\n" +
	                  "KW3087 : 'KW' '3087';\r\n" +
	                  "KW3088 : 'KW' '3088';\r\n" +
	                  "KW3089 : 'KW' '3089';\r\n" +
	                  "KW3090 : 'KW' '3090';\r\n" +
	                  "KW3091 : 'KW' '3091';\r\n" +
	                  "KW3092 : 'KW' '3092';\r\n" +
	                  "KW3093 : 'KW' '3093';\r\n" +
	                  "KW3094 : 'KW' '3094';\r\n" +
	                  "KW3095 : 'KW' '3095';\r\n" +
	                  "KW3096 : 'KW' '3096';\r\n" +
	                  "KW3097 : 'KW' '3097';\r\n" +
	                  "KW3098 : 'KW' '3098';\r\n" +
	                  "KW3099 : 'KW' '3099';\r\n" +
	                  "KW3100 : 'KW' '3100';\r\n" +
	                  "KW3101 : 'KW' '3101';\r\n" +
	                  "KW3102 : 'KW' '3102';\r\n" +
	                  "KW3103 : 'KW' '3103';\r\n" +
	                  "KW3104 : 'KW' '3104';\r\n" +
	                  "KW3105 : 'KW' '3105';\r\n" +
	                  "KW3106 : 'KW' '3106';\r\n" +
	                  "KW3107 : 'KW' '3107';\r\n" +
	                  "KW3108 : 'KW' '3108';\r\n" +
	                  "KW3109 : 'KW' '3109';\r\n" +
	                  "KW3110 : 'KW' '3110';\r\n" +
	                  "KW3111 : 'KW' '3111';\r\n" +
	                  "KW3112 : 'KW' '3112';\r\n" +
	                  "KW3113 : 'KW' '3113';\r\n" +
	                  "KW3114 : 'KW' '3114';\r\n" +
	                  "KW3115 : 'KW' '3115';\r\n" +
	                  "KW3116 : 'KW' '3116';\r\n" +
	                  "KW3117 : 'KW' '3117';\r\n" +
	                  "KW3118 : 'KW' '3118';\r\n" +
	                  "KW3119 : 'KW' '3119';\r\n" +
	                  "KW3120 : 'KW' '3120';\r\n" +
	                  "KW3121 : 'KW' '3121';\r\n" +
	                  "KW3122 : 'KW' '3122';\r\n" +
	                  "KW3123 : 'KW' '3123';\r\n" +
	                  "KW3124 : 'KW' '3124';\r\n" +
	                  "KW3125 : 'KW' '3125';\r\n" +
	                  "KW3126 : 'KW' '3126';\r\n" +
	                  "KW3127 : 'KW' '3127';\r\n" +
	                  "KW3128 : 'KW' '3128';\r\n" +
	                  "KW3129 : 'KW' '3129';\r\n" +
	                  "KW3130 : 'KW' '3130';\r\n" +
	                  "KW3131 : 'KW' '3131';\r\n" +
	                  "KW3132 : 'KW' '3132';\r\n" +
	                  "KW3133 : 'KW' '3133';\r\n" +
	                  "KW3134 : 'KW' '3134';\r\n" +
	                  "KW3135 : 'KW' '3135';\r\n" +
	                  "KW3136 : 'KW' '3136';\r\n" +
	                  "KW3137 : 'KW' '3137';\r\n" +
	                  "KW3138 : 'KW' '3138';\r\n" +
	                  "KW3139 : 'KW' '3139';\r\n" +
	                  "KW3140 : 'KW' '3140';\r\n" +
	                  "KW3141 : 'KW' '3141';\r\n" +
	                  "KW3142 : 'KW' '3142';\r\n" +
	                  "KW3143 : 'KW' '3143';\r\n" +
	                  "KW3144 : 'KW' '3144';\r\n" +
	                  "KW3145 : 'KW' '3145';\r\n" +
	                  "KW3146 : 'KW' '3146';\r\n" +
	                  "KW3147 : 'KW' '3147';\r\n" +
	                  "KW3148 : 'KW' '3148';\r\n" +
	                  "KW3149 : 'KW' '3149';\r\n" +
	                  "KW3150 : 'KW' '3150';\r\n" +
	                  "KW3151 : 'KW' '3151';\r\n" +
	                  "KW3152 : 'KW' '3152';\r\n" +
	                  "KW3153 : 'KW' '3153';\r\n" +
	                  "KW3154 : 'KW' '3154';\r\n" +
	                  "KW3155 : 'KW' '3155';\r\n" +
	                  "KW3156 : 'KW' '3156';\r\n" +
	                  "KW3157 : 'KW' '3157';\r\n" +
	                  "KW3158 : 'KW' '3158';\r\n" +
	                  "KW3159 : 'KW' '3159';\r\n" +
	                  "KW3160 : 'KW' '3160';\r\n" +
	                  "KW3161 : 'KW' '3161';\r\n" +
	                  "KW3162 : 'KW' '3162';\r\n" +
	                  "KW3163 : 'KW' '3163';\r\n" +
	                  "KW3164 : 'KW' '3164';\r\n" +
	                  "KW3165 : 'KW' '3165';\r\n" +
	                  "KW3166 : 'KW' '3166';\r\n" +
	                  "KW3167 : 'KW' '3167';\r\n" +
	                  "KW3168 : 'KW' '3168';\r\n" +
	                  "KW3169 : 'KW' '3169';\r\n" +
	                  "KW3170 : 'KW' '3170';\r\n" +
	                  "KW3171 : 'KW' '3171';\r\n" +
	                  "KW3172 : 'KW' '3172';\r\n" +
	                  "KW3173 : 'KW' '3173';\r\n" +
	                  "KW3174 : 'KW' '3174';\r\n" +
	                  "KW3175 : 'KW' '3175';\r\n" +
	                  "KW3176 : 'KW' '3176';\r\n" +
	                  "KW3177 : 'KW' '3177';\r\n" +
	                  "KW3178 : 'KW' '3178';\r\n" +
	                  "KW3179 : 'KW' '3179';\r\n" +
	                  "KW3180 : 'KW' '3180';\r\n" +
	                  "KW3181 : 'KW' '3181';\r\n" +
	                  "KW3182 : 'KW' '3182';\r\n" +
	                  "KW3183 : 'KW' '3183';\r\n" +
	                  "KW3184 : 'KW' '3184';\r\n" +
	                  "KW3185 : 'KW' '3185';\r\n" +
	                  "KW3186 : 'KW' '3186';\r\n" +
	                  "KW3187 : 'KW' '3187';\r\n" +
	                  "KW3188 : 'KW' '3188';\r\n" +
	                  "KW3189 : 'KW' '3189';\r\n" +
	                  "KW3190 : 'KW' '3190';\r\n" +
	                  "KW3191 : 'KW' '3191';\r\n" +
	                  "KW3192 : 'KW' '3192';\r\n" +
	                  "KW3193 : 'KW' '3193';\r\n" +
	                  "KW3194 : 'KW' '3194';\r\n" +
	                  "KW3195 : 'KW' '3195';\r\n" +
	                  "KW3196 : 'KW' '3196';\r\n" +
	                  "KW3197 : 'KW' '3197';\r\n" +
	                  "KW3198 : 'KW' '3198';\r\n" +
	                  "KW3199 : 'KW' '3199';\r\n" +
	                  "KW3200 : 'KW' '3200';\r\n" +
	                  "KW3201 : 'KW' '3201';\r\n" +
	                  "KW3202 : 'KW' '3202';\r\n" +
	                  "KW3203 : 'KW' '3203';\r\n" +
	                  "KW3204 : 'KW' '3204';\r\n" +
	                  "KW3205 : 'KW' '3205';\r\n" +
	                  "KW3206 : 'KW' '3206';\r\n" +
	                  "KW3207 : 'KW' '3207';\r\n" +
	                  "KW3208 : 'KW' '3208';\r\n" +
	                  "KW3209 : 'KW' '3209';\r\n" +
	                  "KW3210 : 'KW' '3210';\r\n" +
	                  "KW3211 : 'KW' '3211';\r\n" +
	                  "KW3212 : 'KW' '3212';\r\n" +
	                  "KW3213 : 'KW' '3213';\r\n" +
	                  "KW3214 : 'KW' '3214';\r\n" +
	                  "KW3215 : 'KW' '3215';\r\n" +
	                  "KW3216 : 'KW' '3216';\r\n" +
	                  "KW3217 : 'KW' '3217';\r\n" +
	                  "KW3218 : 'KW' '3218';\r\n" +
	                  "KW3219 : 'KW' '3219';\r\n" +
	                  "KW3220 : 'KW' '3220';\r\n" +
	                  "KW3221 : 'KW' '3221';\r\n" +
	                  "KW3222 : 'KW' '3222';\r\n" +
	                  "KW3223 : 'KW' '3223';\r\n" +
	                  "KW3224 : 'KW' '3224';\r\n" +
	                  "KW3225 : 'KW' '3225';\r\n" +
	                  "KW3226 : 'KW' '3226';\r\n" +
	                  "KW3227 : 'KW' '3227';\r\n" +
	                  "KW3228 : 'KW' '3228';\r\n" +
	                  "KW3229 : 'KW' '3229';\r\n" +
	                  "KW3230 : 'KW' '3230';\r\n" +
	                  "KW3231 : 'KW' '3231';\r\n" +
	                  "KW3232 : 'KW' '3232';\r\n" +
	                  "KW3233 : 'KW' '3233';\r\n" +
	                  "KW3234 : 'KW' '3234';\r\n" +
	                  "KW3235 : 'KW' '3235';\r\n" +
	                  "KW3236 : 'KW' '3236';\r\n" +
	                  "KW3237 : 'KW' '3237';\r\n" +
	                  "KW3238 : 'KW' '3238';\r\n" +
	                  "KW3239 : 'KW' '3239';\r\n" +
	                  "KW3240 : 'KW' '3240';\r\n" +
	                  "KW3241 : 'KW' '3241';\r\n" +
	                  "KW3242 : 'KW' '3242';\r\n" +
	                  "KW3243 : 'KW' '3243';\r\n" +
	                  "KW3244 : 'KW' '3244';\r\n" +
	                  "KW3245 : 'KW' '3245';\r\n" +
	                  "KW3246 : 'KW' '3246';\r\n" +
	                  "KW3247 : 'KW' '3247';\r\n" +
	                  "KW3248 : 'KW' '3248';\r\n" +
	                  "KW3249 : 'KW' '3249';\r\n" +
	                  "KW3250 : 'KW' '3250';\r\n" +
	                  "KW3251 : 'KW' '3251';\r\n" +
	                  "KW3252 : 'KW' '3252';\r\n" +
	                  "KW3253 : 'KW' '3253';\r\n" +
	                  "KW3254 : 'KW' '3254';\r\n" +
	                  "KW3255 : 'KW' '3255';\r\n" +
	                  "KW3256 : 'KW' '3256';\r\n" +
	                  "KW3257 : 'KW' '3257';\r\n" +
	                  "KW3258 : 'KW' '3258';\r\n" +
	                  "KW3259 : 'KW' '3259';\r\n" +
	                  "KW3260 : 'KW' '3260';\r\n" +
	                  "KW3261 : 'KW' '3261';\r\n" +
	                  "KW3262 : 'KW' '3262';\r\n" +
	                  "KW3263 : 'KW' '3263';\r\n" +
	                  "KW3264 : 'KW' '3264';\r\n" +
	                  "KW3265 : 'KW' '3265';\r\n" +
	                  "KW3266 : 'KW' '3266';\r\n" +
	                  "KW3267 : 'KW' '3267';\r\n" +
	                  "KW3268 : 'KW' '3268';\r\n" +
	                  "KW3269 : 'KW' '3269';\r\n" +
	                  "KW3270 : 'KW' '3270';\r\n" +
	                  "KW3271 : 'KW' '3271';\r\n" +
	                  "KW3272 : 'KW' '3272';\r\n" +
	                  "KW3273 : 'KW' '3273';\r\n" +
	                  "KW3274 : 'KW' '3274';\r\n" +
	                  "KW3275 : 'KW' '3275';\r\n" +
	                  "KW3276 : 'KW' '3276';\r\n" +
	                  "KW3277 : 'KW' '3277';\r\n" +
	                  "KW3278 : 'KW' '3278';\r\n" +
	                  "KW3279 : 'KW' '3279';\r\n" +
	                  "KW3280 : 'KW' '3280';\r\n" +
	                  "KW3281 : 'KW' '3281';\r\n" +
	                  "KW3282 : 'KW' '3282';\r\n" +
	                  "KW3283 : 'KW' '3283';\r\n" +
	                  "KW3284 : 'KW' '3284';\r\n" +
	                  "KW3285 : 'KW' '3285';\r\n" +
	                  "KW3286 : 'KW' '3286';\r\n" +
	                  "KW3287 : 'KW' '3287';\r\n" +
	                  "KW3288 : 'KW' '3288';\r\n" +
	                  "KW3289 : 'KW' '3289';\r\n" +
	                  "KW3290 : 'KW' '3290';\r\n" +
	                  "KW3291 : 'KW' '3291';\r\n" +
	                  "KW3292 : 'KW' '3292';\r\n" +
	                  "KW3293 : 'KW' '3293';\r\n" +
	                  "KW3294 : 'KW' '3294';\r\n" +
	                  "KW3295 : 'KW' '3295';\r\n" +
	                  "KW3296 : 'KW' '3296';\r\n" +
	                  "KW3297 : 'KW' '3297';\r\n" +
	                  "KW3298 : 'KW' '3298';\r\n" +
	                  "KW3299 : 'KW' '3299';\r\n" +
	                  "KW3300 : 'KW' '3300';\r\n" +
	                  "KW3301 : 'KW' '3301';\r\n" +
	                  "KW3302 : 'KW' '3302';\r\n" +
	                  "KW3303 : 'KW' '3303';\r\n" +
	                  "KW3304 : 'KW' '3304';\r\n" +
	                  "KW3305 : 'KW' '3305';\r\n" +
	                  "KW3306 : 'KW' '3306';\r\n" +
	                  "KW3307 : 'KW' '3307';\r\n" +
	                  "KW3308 : 'KW' '3308';\r\n" +
	                  "KW3309 : 'KW' '3309';\r\n" +
	                  "KW3310 : 'KW' '3310';\r\n" +
	                  "KW3311 : 'KW' '3311';\r\n" +
	                  "KW3312 : 'KW' '3312';\r\n" +
	                  "KW3313 : 'KW' '3313';\r\n" +
	                  "KW3314 : 'KW' '3314';\r\n" +
	                  "KW3315 : 'KW' '3315';\r\n" +
	                  "KW3316 : 'KW' '3316';\r\n" +
	                  "KW3317 : 'KW' '3317';\r\n" +
	                  "KW3318 : 'KW' '3318';\r\n" +
	                  "KW3319 : 'KW' '3319';\r\n" +
	                  "KW3320 : 'KW' '3320';\r\n" +
	                  "KW3321 : 'KW' '3321';\r\n" +
	                  "KW3322 : 'KW' '3322';\r\n" +
	                  "KW3323 : 'KW' '3323';\r\n" +
	                  "KW3324 : 'KW' '3324';\r\n" +
	                  "KW3325 : 'KW' '3325';\r\n" +
	                  "KW3326 : 'KW' '3326';\r\n" +
	                  "KW3327 : 'KW' '3327';\r\n" +
	                  "KW3328 : 'KW' '3328';\r\n" +
	                  "KW3329 : 'KW' '3329';\r\n" +
	                  "KW3330 : 'KW' '3330';\r\n" +
	                  "KW3331 : 'KW' '3331';\r\n" +
	                  "KW3332 : 'KW' '3332';\r\n" +
	                  "KW3333 : 'KW' '3333';\r\n" +
	                  "KW3334 : 'KW' '3334';\r\n" +
	                  "KW3335 : 'KW' '3335';\r\n" +
	                  "KW3336 : 'KW' '3336';\r\n" +
	                  "KW3337 : 'KW' '3337';\r\n" +
	                  "KW3338 : 'KW' '3338';\r\n" +
	                  "KW3339 : 'KW' '3339';\r\n" +
	                  "KW3340 : 'KW' '3340';\r\n" +
	                  "KW3341 : 'KW' '3341';\r\n" +
	                  "KW3342 : 'KW' '3342';\r\n" +
	                  "KW3343 : 'KW' '3343';\r\n" +
	                  "KW3344 : 'KW' '3344';\r\n" +
	                  "KW3345 : 'KW' '3345';\r\n" +
	                  "KW3346 : 'KW' '3346';\r\n" +
	                  "KW3347 : 'KW' '3347';\r\n" +
	                  "KW3348 : 'KW' '3348';\r\n" +
	                  "KW3349 : 'KW' '3349';\r\n" +
	                  "KW3350 : 'KW' '3350';\r\n" +
	                  "KW3351 : 'KW' '3351';\r\n" +
	                  "KW3352 : 'KW' '3352';\r\n" +
	                  "KW3353 : 'KW' '3353';\r\n" +
	                  "KW3354 : 'KW' '3354';\r\n" +
	                  "KW3355 : 'KW' '3355';\r\n" +
	                  "KW3356 : 'KW' '3356';\r\n" +
	                  "KW3357 : 'KW' '3357';\r\n" +
	                  "KW3358 : 'KW' '3358';\r\n" +
	                  "KW3359 : 'KW' '3359';\r\n" +
	                  "KW3360 : 'KW' '3360';\r\n" +
	                  "KW3361 : 'KW' '3361';\r\n" +
	                  "KW3362 : 'KW' '3362';\r\n" +
	                  "KW3363 : 'KW' '3363';\r\n" +
	                  "KW3364 : 'KW' '3364';\r\n" +
	                  "KW3365 : 'KW' '3365';\r\n" +
	                  "KW3366 : 'KW' '3366';\r\n" +
	                  "KW3367 : 'KW' '3367';\r\n" +
	                  "KW3368 : 'KW' '3368';\r\n" +
	                  "KW3369 : 'KW' '3369';\r\n" +
	                  "KW3370 : 'KW' '3370';\r\n" +
	                  "KW3371 : 'KW' '3371';\r\n" +
	                  "KW3372 : 'KW' '3372';\r\n" +
	                  "KW3373 : 'KW' '3373';\r\n" +
	                  "KW3374 : 'KW' '3374';\r\n" +
	                  "KW3375 : 'KW' '3375';\r\n" +
	                  "KW3376 : 'KW' '3376';\r\n" +
	                  "KW3377 : 'KW' '3377';\r\n" +
	                  "KW3378 : 'KW' '3378';\r\n" +
	                  "KW3379 : 'KW' '3379';\r\n" +
	                  "KW3380 : 'KW' '3380';\r\n" +
	                  "KW3381 : 'KW' '3381';\r\n" +
	                  "KW3382 : 'KW' '3382';\r\n" +
	                  "KW3383 : 'KW' '3383';\r\n" +
	                  "KW3384 : 'KW' '3384';\r\n" +
	                  "KW3385 : 'KW' '3385';\r\n" +
	                  "KW3386 : 'KW' '3386';\r\n" +
	                  "KW3387 : 'KW' '3387';\r\n" +
	                  "KW3388 : 'KW' '3388';\r\n" +
	                  "KW3389 : 'KW' '3389';\r\n" +
	                  "KW3390 : 'KW' '3390';\r\n" +
	                  "KW3391 : 'KW' '3391';\r\n" +
	                  "KW3392 : 'KW' '3392';\r\n" +
	                  "KW3393 : 'KW' '3393';\r\n" +
	                  "KW3394 : 'KW' '3394';\r\n" +
	                  "KW3395 : 'KW' '3395';\r\n" +
	                  "KW3396 : 'KW' '3396';\r\n" +
	                  "KW3397 : 'KW' '3397';\r\n" +
	                  "KW3398 : 'KW' '3398';\r\n" +
	                  "KW3399 : 'KW' '3399';\r\n" +
	                  "KW3400 : 'KW' '3400';\r\n" +
	                  "KW3401 : 'KW' '3401';\r\n" +
	                  "KW3402 : 'KW' '3402';\r\n" +
	                  "KW3403 : 'KW' '3403';\r\n" +
	                  "KW3404 : 'KW' '3404';\r\n" +
	                  "KW3405 : 'KW' '3405';\r\n" +
	                  "KW3406 : 'KW' '3406';\r\n" +
	                  "KW3407 : 'KW' '3407';\r\n" +
	                  "KW3408 : 'KW' '3408';\r\n" +
	                  "KW3409 : 'KW' '3409';\r\n" +
	                  "KW3410 : 'KW' '3410';\r\n" +
	                  "KW3411 : 'KW' '3411';\r\n" +
	                  "KW3412 : 'KW' '3412';\r\n" +
	                  "KW3413 : 'KW' '3413';\r\n" +
	                  "KW3414 : 'KW' '3414';\r\n" +
	                  "KW3415 : 'KW' '3415';\r\n" +
	                  "KW3416 : 'KW' '3416';\r\n" +
	                  "KW3417 : 'KW' '3417';\r\n" +
	                  "KW3418 : 'KW' '3418';\r\n" +
	                  "KW3419 : 'KW' '3419';\r\n" +
	                  "KW3420 : 'KW' '3420';\r\n" +
	                  "KW3421 : 'KW' '3421';\r\n" +
	                  "KW3422 : 'KW' '3422';\r\n" +
	                  "KW3423 : 'KW' '3423';\r\n" +
	                  "KW3424 : 'KW' '3424';\r\n" +
	                  "KW3425 : 'KW' '3425';\r\n" +
	                  "KW3426 : 'KW' '3426';\r\n" +
	                  "KW3427 : 'KW' '3427';\r\n" +
	                  "KW3428 : 'KW' '3428';\r\n" +
	                  "KW3429 : 'KW' '3429';\r\n" +
	                  "KW3430 : 'KW' '3430';\r\n" +
	                  "KW3431 : 'KW' '3431';\r\n" +
	                  "KW3432 : 'KW' '3432';\r\n" +
	                  "KW3433 : 'KW' '3433';\r\n" +
	                  "KW3434 : 'KW' '3434';\r\n" +
	                  "KW3435 : 'KW' '3435';\r\n" +
	                  "KW3436 : 'KW' '3436';\r\n" +
	                  "KW3437 : 'KW' '3437';\r\n" +
	                  "KW3438 : 'KW' '3438';\r\n" +
	                  "KW3439 : 'KW' '3439';\r\n" +
	                  "KW3440 : 'KW' '3440';\r\n" +
	                  "KW3441 : 'KW' '3441';\r\n" +
	                  "KW3442 : 'KW' '3442';\r\n" +
	                  "KW3443 : 'KW' '3443';\r\n" +
	                  "KW3444 : 'KW' '3444';\r\n" +
	                  "KW3445 : 'KW' '3445';\r\n" +
	                  "KW3446 : 'KW' '3446';\r\n" +
	                  "KW3447 : 'KW' '3447';\r\n" +
	                  "KW3448 : 'KW' '3448';\r\n" +
	                  "KW3449 : 'KW' '3449';\r\n" +
	                  "KW3450 : 'KW' '3450';\r\n" +
	                  "KW3451 : 'KW' '3451';\r\n" +
	                  "KW3452 : 'KW' '3452';\r\n" +
	                  "KW3453 : 'KW' '3453';\r\n" +
	                  "KW3454 : 'KW' '3454';\r\n" +
	                  "KW3455 : 'KW' '3455';\r\n" +
	                  "KW3456 : 'KW' '3456';\r\n" +
	                  "KW3457 : 'KW' '3457';\r\n" +
	                  "KW3458 : 'KW' '3458';\r\n" +
	                  "KW3459 : 'KW' '3459';\r\n" +
	                  "KW3460 : 'KW' '3460';\r\n" +
	                  "KW3461 : 'KW' '3461';\r\n" +
	                  "KW3462 : 'KW' '3462';\r\n" +
	                  "KW3463 : 'KW' '3463';\r\n" +
	                  "KW3464 : 'KW' '3464';\r\n" +
	                  "KW3465 : 'KW' '3465';\r\n" +
	                  "KW3466 : 'KW' '3466';\r\n" +
	                  "KW3467 : 'KW' '3467';\r\n" +
	                  "KW3468 : 'KW' '3468';\r\n" +
	                  "KW3469 : 'KW' '3469';\r\n" +
	                  "KW3470 : 'KW' '3470';\r\n" +
	                  "KW3471 : 'KW' '3471';\r\n" +
	                  "KW3472 : 'KW' '3472';\r\n" +
	                  "KW3473 : 'KW' '3473';\r\n" +
	                  "KW3474 : 'KW' '3474';\r\n" +
	                  "KW3475 : 'KW' '3475';\r\n" +
	                  "KW3476 : 'KW' '3476';\r\n" +
	                  "KW3477 : 'KW' '3477';\r\n" +
	                  "KW3478 : 'KW' '3478';\r\n" +
	                  "KW3479 : 'KW' '3479';\r\n" +
	                  "KW3480 : 'KW' '3480';\r\n" +
	                  "KW3481 : 'KW' '3481';\r\n" +
	                  "KW3482 : 'KW' '3482';\r\n" +
	                  "KW3483 : 'KW' '3483';\r\n" +
	                  "KW3484 : 'KW' '3484';\r\n" +
	                  "KW3485 : 'KW' '3485';\r\n" +
	                  "KW3486 : 'KW' '3486';\r\n" +
	                  "KW3487 : 'KW' '3487';\r\n" +
	                  "KW3488 : 'KW' '3488';\r\n" +
	                  "KW3489 : 'KW' '3489';\r\n" +
	                  "KW3490 : 'KW' '3490';\r\n" +
	                  "KW3491 : 'KW' '3491';\r\n" +
	                  "KW3492 : 'KW' '3492';\r\n" +
	                  "KW3493 : 'KW' '3493';\r\n" +
	                  "KW3494 : 'KW' '3494';\r\n" +
	                  "KW3495 : 'KW' '3495';\r\n" +
	                  "KW3496 : 'KW' '3496';\r\n" +
	                  "KW3497 : 'KW' '3497';\r\n" +
	                  "KW3498 : 'KW' '3498';\r\n" +
	                  "KW3499 : 'KW' '3499';\r\n" +
	                  "KW3500 : 'KW' '3500';\r\n" +
	                  "KW3501 : 'KW' '3501';\r\n" +
	                  "KW3502 : 'KW' '3502';\r\n" +
	                  "KW3503 : 'KW' '3503';\r\n" +
	                  "KW3504 : 'KW' '3504';\r\n" +
	                  "KW3505 : 'KW' '3505';\r\n" +
	                  "KW3506 : 'KW' '3506';\r\n" +
	                  "KW3507 : 'KW' '3507';\r\n" +
	                  "KW3508 : 'KW' '3508';\r\n" +
	                  "KW3509 : 'KW' '3509';\r\n" +
	                  "KW3510 : 'KW' '3510';\r\n" +
	                  "KW3511 : 'KW' '3511';\r\n" +
	                  "KW3512 : 'KW' '3512';\r\n" +
	                  "KW3513 : 'KW' '3513';\r\n" +
	                  "KW3514 : 'KW' '3514';\r\n" +
	                  "KW3515 : 'KW' '3515';\r\n" +
	                  "KW3516 : 'KW' '3516';\r\n" +
	                  "KW3517 : 'KW' '3517';\r\n" +
	                  "KW3518 : 'KW' '3518';\r\n" +
	                  "KW3519 : 'KW' '3519';\r\n" +
	                  "KW3520 : 'KW' '3520';\r\n" +
	                  "KW3521 : 'KW' '3521';\r\n" +
	                  "KW3522 : 'KW' '3522';\r\n" +
	                  "KW3523 : 'KW' '3523';\r\n" +
	                  "KW3524 : 'KW' '3524';\r\n" +
	                  "KW3525 : 'KW' '3525';\r\n" +
	                  "KW3526 : 'KW' '3526';\r\n" +
	                  "KW3527 : 'KW' '3527';\r\n" +
	                  "KW3528 : 'KW' '3528';\r\n" +
	                  "KW3529 : 'KW' '3529';\r\n" +
	                  "KW3530 : 'KW' '3530';\r\n" +
	                  "KW3531 : 'KW' '3531';\r\n" +
	                  "KW3532 : 'KW' '3532';\r\n" +
	                  "KW3533 : 'KW' '3533';\r\n" +
	                  "KW3534 : 'KW' '3534';\r\n" +
	                  "KW3535 : 'KW' '3535';\r\n" +
	                  "KW3536 : 'KW' '3536';\r\n" +
	                  "KW3537 : 'KW' '3537';\r\n" +
	                  "KW3538 : 'KW' '3538';\r\n" +
	                  "KW3539 : 'KW' '3539';\r\n" +
	                  "KW3540 : 'KW' '3540';\r\n" +
	                  "KW3541 : 'KW' '3541';\r\n" +
	                  "KW3542 : 'KW' '3542';\r\n" +
	                  "KW3543 : 'KW' '3543';\r\n" +
	                  "KW3544 : 'KW' '3544';\r\n" +
	                  "KW3545 : 'KW' '3545';\r\n" +
	                  "KW3546 : 'KW' '3546';\r\n" +
	                  "KW3547 : 'KW' '3547';\r\n" +
	                  "KW3548 : 'KW' '3548';\r\n" +
	                  "KW3549 : 'KW' '3549';\r\n" +
	                  "KW3550 : 'KW' '3550';\r\n" +
	                  "KW3551 : 'KW' '3551';\r\n" +
	                  "KW3552 : 'KW' '3552';\r\n" +
	                  "KW3553 : 'KW' '3553';\r\n" +
	                  "KW3554 : 'KW' '3554';\r\n" +
	                  "KW3555 : 'KW' '3555';\r\n" +
	                  "KW3556 : 'KW' '3556';\r\n" +
	                  "KW3557 : 'KW' '3557';\r\n" +
	                  "KW3558 : 'KW' '3558';\r\n" +
	                  "KW3559 : 'KW' '3559';\r\n" +
	                  "KW3560 : 'KW' '3560';\r\n" +
	                  "KW3561 : 'KW' '3561';\r\n" +
	                  "KW3562 : 'KW' '3562';\r\n" +
	                  "KW3563 : 'KW' '3563';\r\n" +
	                  "KW3564 : 'KW' '3564';\r\n" +
	                  "KW3565 : 'KW' '3565';\r\n" +
	                  "KW3566 : 'KW' '3566';\r\n" +
	                  "KW3567 : 'KW' '3567';\r\n" +
	                  "KW3568 : 'KW' '3568';\r\n" +
	                  "KW3569 : 'KW' '3569';\r\n" +
	                  "KW3570 : 'KW' '3570';\r\n" +
	                  "KW3571 : 'KW' '3571';\r\n" +
	                  "KW3572 : 'KW' '3572';\r\n" +
	                  "KW3573 : 'KW' '3573';\r\n" +
	                  "KW3574 : 'KW' '3574';\r\n" +
	                  "KW3575 : 'KW' '3575';\r\n" +
	                  "KW3576 : 'KW' '3576';\r\n" +
	                  "KW3577 : 'KW' '3577';\r\n" +
	                  "KW3578 : 'KW' '3578';\r\n" +
	                  "KW3579 : 'KW' '3579';\r\n" +
	                  "KW3580 : 'KW' '3580';\r\n" +
	                  "KW3581 : 'KW' '3581';\r\n" +
	                  "KW3582 : 'KW' '3582';\r\n" +
	                  "KW3583 : 'KW' '3583';\r\n" +
	                  "KW3584 : 'KW' '3584';\r\n" +
	                  "KW3585 : 'KW' '3585';\r\n" +
	                  "KW3586 : 'KW' '3586';\r\n" +
	                  "KW3587 : 'KW' '3587';\r\n" +
	                  "KW3588 : 'KW' '3588';\r\n" +
	                  "KW3589 : 'KW' '3589';\r\n" +
	                  "KW3590 : 'KW' '3590';\r\n" +
	                  "KW3591 : 'KW' '3591';\r\n" +
	                  "KW3592 : 'KW' '3592';\r\n" +
	                  "KW3593 : 'KW' '3593';\r\n" +
	                  "KW3594 : 'KW' '3594';\r\n" +
	                  "KW3595 : 'KW' '3595';\r\n" +
	                  "KW3596 : 'KW' '3596';\r\n" +
	                  "KW3597 : 'KW' '3597';\r\n" +
	                  "KW3598 : 'KW' '3598';\r\n" +
	                  "KW3599 : 'KW' '3599';\r\n" +
	                  "KW3600 : 'KW' '3600';\r\n" +
	                  "KW3601 : 'KW' '3601';\r\n" +
	                  "KW3602 : 'KW' '3602';\r\n" +
	                  "KW3603 : 'KW' '3603';\r\n" +
	                  "KW3604 : 'KW' '3604';\r\n" +
	                  "KW3605 : 'KW' '3605';\r\n" +
	                  "KW3606 : 'KW' '3606';\r\n" +
	                  "KW3607 : 'KW' '3607';\r\n" +
	                  "KW3608 : 'KW' '3608';\r\n" +
	                  "KW3609 : 'KW' '3609';\r\n" +
	                  "KW3610 : 'KW' '3610';\r\n" +
	                  "KW3611 : 'KW' '3611';\r\n" +
	                  "KW3612 : 'KW' '3612';\r\n" +
	                  "KW3613 : 'KW' '3613';\r\n" +
	                  "KW3614 : 'KW' '3614';\r\n" +
	                  "KW3615 : 'KW' '3615';\r\n" +
	                  "KW3616 : 'KW' '3616';\r\n" +
	                  "KW3617 : 'KW' '3617';\r\n" +
	                  "KW3618 : 'KW' '3618';\r\n" +
	                  "KW3619 : 'KW' '3619';\r\n" +
	                  "KW3620 : 'KW' '3620';\r\n" +
	                  "KW3621 : 'KW' '3621';\r\n" +
	                  "KW3622 : 'KW' '3622';\r\n" +
	                  "KW3623 : 'KW' '3623';\r\n" +
	                  "KW3624 : 'KW' '3624';\r\n" +
	                  "KW3625 : 'KW' '3625';\r\n" +
	                  "KW3626 : 'KW' '3626';\r\n" +
	                  "KW3627 : 'KW' '3627';\r\n" +
	                  "KW3628 : 'KW' '3628';\r\n" +
	                  "KW3629 : 'KW' '3629';\r\n" +
	                  "KW3630 : 'KW' '3630';\r\n" +
	                  "KW3631 : 'KW' '3631';\r\n" +
	                  "KW3632 : 'KW' '3632';\r\n" +
	                  "KW3633 : 'KW' '3633';\r\n" +
	                  "KW3634 : 'KW' '3634';\r\n" +
	                  "KW3635 : 'KW' '3635';\r\n" +
	                  "KW3636 : 'KW' '3636';\r\n" +
	                  "KW3637 : 'KW' '3637';\r\n" +
	                  "KW3638 : 'KW' '3638';\r\n" +
	                  "KW3639 : 'KW' '3639';\r\n" +
	                  "KW3640 : 'KW' '3640';\r\n" +
	                  "KW3641 : 'KW' '3641';\r\n" +
	                  "KW3642 : 'KW' '3642';\r\n" +
	                  "KW3643 : 'KW' '3643';\r\n" +
	                  "KW3644 : 'KW' '3644';\r\n" +
	                  "KW3645 : 'KW' '3645';\r\n" +
	                  "KW3646 : 'KW' '3646';\r\n" +
	                  "KW3647 : 'KW' '3647';\r\n" +
	                  "KW3648 : 'KW' '3648';\r\n" +
	                  "KW3649 : 'KW' '3649';\r\n" +
	                  "KW3650 : 'KW' '3650';\r\n" +
	                  "KW3651 : 'KW' '3651';\r\n" +
	                  "KW3652 : 'KW' '3652';\r\n" +
	                  "KW3653 : 'KW' '3653';\r\n" +
	                  "KW3654 : 'KW' '3654';\r\n" +
	                  "KW3655 : 'KW' '3655';\r\n" +
	                  "KW3656 : 'KW' '3656';\r\n" +
	                  "KW3657 : 'KW' '3657';\r\n" +
	                  "KW3658 : 'KW' '3658';\r\n" +
	                  "KW3659 : 'KW' '3659';\r\n" +
	                  "KW3660 : 'KW' '3660';\r\n" +
	                  "KW3661 : 'KW' '3661';\r\n" +
	                  "KW3662 : 'KW' '3662';\r\n" +
	                  "KW3663 : 'KW' '3663';\r\n" +
	                  "KW3664 : 'KW' '3664';\r\n" +
	                  "KW3665 : 'KW' '3665';\r\n" +
	                  "KW3666 : 'KW' '3666';\r\n" +
	                  "KW3667 : 'KW' '3667';\r\n" +
	                  "KW3668 : 'KW' '3668';\r\n" +
	                  "KW3669 : 'KW' '3669';\r\n" +
	                  "KW3670 : 'KW' '3670';\r\n" +
	                  "KW3671 : 'KW' '3671';\r\n" +
	                  "KW3672 : 'KW' '3672';\r\n" +
	                  "KW3673 : 'KW' '3673';\r\n" +
	                  "KW3674 : 'KW' '3674';\r\n" +
	                  "KW3675 : 'KW' '3675';\r\n" +
	                  "KW3676 : 'KW' '3676';\r\n" +
	                  "KW3677 : 'KW' '3677';\r\n" +
	                  "KW3678 : 'KW' '3678';\r\n" +
	                  "KW3679 : 'KW' '3679';\r\n" +
	                  "KW3680 : 'KW' '3680';\r\n" +
	                  "KW3681 : 'KW' '3681';\r\n" +
	                  "KW3682 : 'KW' '3682';\r\n" +
	                  "KW3683 : 'KW' '3683';\r\n" +
	                  "KW3684 : 'KW' '3684';\r\n" +
	                  "KW3685 : 'KW' '3685';\r\n" +
	                  "KW3686 : 'KW' '3686';\r\n" +
	                  "KW3687 : 'KW' '3687';\r\n" +
	                  "KW3688 : 'KW' '3688';\r\n" +
	                  "KW3689 : 'KW' '3689';\r\n" +
	                  "KW3690 : 'KW' '3690';\r\n" +
	                  "KW3691 : 'KW' '3691';\r\n" +
	                  "KW3692 : 'KW' '3692';\r\n" +
	                  "KW3693 : 'KW' '3693';\r\n" +
	                  "KW3694 : 'KW' '3694';\r\n" +
	                  "KW3695 : 'KW' '3695';\r\n" +
	                  "KW3696 : 'KW' '3696';\r\n" +
	                  "KW3697 : 'KW' '3697';\r\n" +
	                  "KW3698 : 'KW' '3698';\r\n" +
	                  "KW3699 : 'KW' '3699';\r\n" +
	                  "KW3700 : 'KW' '3700';\r\n" +
	                  "KW3701 : 'KW' '3701';\r\n" +
	                  "KW3702 : 'KW' '3702';\r\n" +
	                  "KW3703 : 'KW' '3703';\r\n" +
	                  "KW3704 : 'KW' '3704';\r\n" +
	                  "KW3705 : 'KW' '3705';\r\n" +
	                  "KW3706 : 'KW' '3706';\r\n" +
	                  "KW3707 : 'KW' '3707';\r\n" +
	                  "KW3708 : 'KW' '3708';\r\n" +
	                  "KW3709 : 'KW' '3709';\r\n" +
	                  "KW3710 : 'KW' '3710';\r\n" +
	                  "KW3711 : 'KW' '3711';\r\n" +
	                  "KW3712 : 'KW' '3712';\r\n" +
	                  "KW3713 : 'KW' '3713';\r\n" +
	                  "KW3714 : 'KW' '3714';\r\n" +
	                  "KW3715 : 'KW' '3715';\r\n" +
	                  "KW3716 : 'KW' '3716';\r\n" +
	                  "KW3717 : 'KW' '3717';\r\n" +
	                  "KW3718 : 'KW' '3718';\r\n" +
	                  "KW3719 : 'KW' '3719';\r\n" +
	                  "KW3720 : 'KW' '3720';\r\n" +
	                  "KW3721 : 'KW' '3721';\r\n" +
	                  "KW3722 : 'KW' '3722';\r\n" +
	                  "KW3723 : 'KW' '3723';\r\n" +
	                  "KW3724 : 'KW' '3724';\r\n" +
	                  "KW3725 : 'KW' '3725';\r\n" +
	                  "KW3726 : 'KW' '3726';\r\n" +
	                  "KW3727 : 'KW' '3727';\r\n" +
	                  "KW3728 : 'KW' '3728';\r\n" +
	                  "KW3729 : 'KW' '3729';\r\n" +
	                  "KW3730 : 'KW' '3730';\r\n" +
	                  "KW3731 : 'KW' '3731';\r\n" +
	                  "KW3732 : 'KW' '3732';\r\n" +
	                  "KW3733 : 'KW' '3733';\r\n" +
	                  "KW3734 : 'KW' '3734';\r\n" +
	                  "KW3735 : 'KW' '3735';\r\n" +
	                  "KW3736 : 'KW' '3736';\r\n" +
	                  "KW3737 : 'KW' '3737';\r\n" +
	                  "KW3738 : 'KW' '3738';\r\n" +
	                  "KW3739 : 'KW' '3739';\r\n" +
	                  "KW3740 : 'KW' '3740';\r\n" +
	                  "KW3741 : 'KW' '3741';\r\n" +
	                  "KW3742 : 'KW' '3742';\r\n" +
	                  "KW3743 : 'KW' '3743';\r\n" +
	                  "KW3744 : 'KW' '3744';\r\n" +
	                  "KW3745 : 'KW' '3745';\r\n" +
	                  "KW3746 : 'KW' '3746';\r\n" +
	                  "KW3747 : 'KW' '3747';\r\n" +
	                  "KW3748 : 'KW' '3748';\r\n" +
	                  "KW3749 : 'KW' '3749';\r\n" +
	                  "KW3750 : 'KW' '3750';\r\n" +
	                  "KW3751 : 'KW' '3751';\r\n" +
	                  "KW3752 : 'KW' '3752';\r\n" +
	                  "KW3753 : 'KW' '3753';\r\n" +
	                  "KW3754 : 'KW' '3754';\r\n" +
	                  "KW3755 : 'KW' '3755';\r\n" +
	                  "KW3756 : 'KW' '3756';\r\n" +
	                  "KW3757 : 'KW' '3757';\r\n" +
	                  "KW3758 : 'KW' '3758';\r\n" +
	                  "KW3759 : 'KW' '3759';\r\n" +
	                  "KW3760 : 'KW' '3760';\r\n" +
	                  "KW3761 : 'KW' '3761';\r\n" +
	                  "KW3762 : 'KW' '3762';\r\n" +
	                  "KW3763 : 'KW' '3763';\r\n" +
	                  "KW3764 : 'KW' '3764';\r\n" +
	                  "KW3765 : 'KW' '3765';\r\n" +
	                  "KW3766 : 'KW' '3766';\r\n" +
	                  "KW3767 : 'KW' '3767';\r\n" +
	                  "KW3768 : 'KW' '3768';\r\n" +
	                  "KW3769 : 'KW' '3769';\r\n" +
	                  "KW3770 : 'KW' '3770';\r\n" +
	                  "KW3771 : 'KW' '3771';\r\n" +
	                  "KW3772 : 'KW' '3772';\r\n" +
	                  "KW3773 : 'KW' '3773';\r\n" +
	                  "KW3774 : 'KW' '3774';\r\n" +
	                  "KW3775 : 'KW' '3775';\r\n" +
	                  "KW3776 : 'KW' '3776';\r\n" +
	                  "KW3777 : 'KW' '3777';\r\n" +
	                  "KW3778 : 'KW' '3778';\r\n" +
	                  "KW3779 : 'KW' '3779';\r\n" +
	                  "KW3780 : 'KW' '3780';\r\n" +
	                  "KW3781 : 'KW' '3781';\r\n" +
	                  "KW3782 : 'KW' '3782';\r\n" +
	                  "KW3783 : 'KW' '3783';\r\n" +
	                  "KW3784 : 'KW' '3784';\r\n" +
	                  "KW3785 : 'KW' '3785';\r\n" +
	                  "KW3786 : 'KW' '3786';\r\n" +
	                  "KW3787 : 'KW' '3787';\r\n" +
	                  "KW3788 : 'KW' '3788';\r\n" +
	                  "KW3789 : 'KW' '3789';\r\n" +
	                  "KW3790 : 'KW' '3790';\r\n" +
	                  "KW3791 : 'KW' '3791';\r\n" +
	                  "KW3792 : 'KW' '3792';\r\n" +
	                  "KW3793 : 'KW' '3793';\r\n" +
	                  "KW3794 : 'KW' '3794';\r\n" +
	                  "KW3795 : 'KW' '3795';\r\n" +
	                  "KW3796 : 'KW' '3796';\r\n" +
	                  "KW3797 : 'KW' '3797';\r\n" +
	                  "KW3798 : 'KW' '3798';\r\n" +
	                  "KW3799 : 'KW' '3799';\r\n" +
	                  "KW3800 : 'KW' '3800';\r\n" +
	                  "KW3801 : 'KW' '3801';\r\n" +
	                  "KW3802 : 'KW' '3802';\r\n" +
	                  "KW3803 : 'KW' '3803';\r\n" +
	                  "KW3804 : 'KW' '3804';\r\n" +
	                  "KW3805 : 'KW' '3805';\r\n" +
	                  "KW3806 : 'KW' '3806';\r\n" +
	                  "KW3807 : 'KW' '3807';\r\n" +
	                  "KW3808 : 'KW' '3808';\r\n" +
	                  "KW3809 : 'KW' '3809';\r\n" +
	                  "KW3810 : 'KW' '3810';\r\n" +
	                  "KW3811 : 'KW' '3811';\r\n" +
	                  "KW3812 : 'KW' '3812';\r\n" +
	                  "KW3813 : 'KW' '3813';\r\n" +
	                  "KW3814 : 'KW' '3814';\r\n" +
	                  "KW3815 : 'KW' '3815';\r\n" +
	                  "KW3816 : 'KW' '3816';\r\n" +
	                  "KW3817 : 'KW' '3817';\r\n" +
	                  "KW3818 : 'KW' '3818';\r\n" +
	                  "KW3819 : 'KW' '3819';\r\n" +
	                  "KW3820 : 'KW' '3820';\r\n" +
	                  "KW3821 : 'KW' '3821';\r\n" +
	                  "KW3822 : 'KW' '3822';\r\n" +
	                  "KW3823 : 'KW' '3823';\r\n" +
	                  "KW3824 : 'KW' '3824';\r\n" +
	                  "KW3825 : 'KW' '3825';\r\n" +
	                  "KW3826 : 'KW' '3826';\r\n" +
	                  "KW3827 : 'KW' '3827';\r\n" +
	                  "KW3828 : 'KW' '3828';\r\n" +
	                  "KW3829 : 'KW' '3829';\r\n" +
	                  "KW3830 : 'KW' '3830';\r\n" +
	                  "KW3831 : 'KW' '3831';\r\n" +
	                  "KW3832 : 'KW' '3832';\r\n" +
	                  "KW3833 : 'KW' '3833';\r\n" +
	                  "KW3834 : 'KW' '3834';\r\n" +
	                  "KW3835 : 'KW' '3835';\r\n" +
	                  "KW3836 : 'KW' '3836';\r\n" +
	                  "KW3837 : 'KW' '3837';\r\n" +
	                  "KW3838 : 'KW' '3838';\r\n" +
	                  "KW3839 : 'KW' '3839';\r\n" +
	                  "KW3840 : 'KW' '3840';\r\n" +
	                  "KW3841 : 'KW' '3841';\r\n" +
	                  "KW3842 : 'KW' '3842';\r\n" +
	                  "KW3843 : 'KW' '3843';\r\n" +
	                  "KW3844 : 'KW' '3844';\r\n" +
	                  "KW3845 : 'KW' '3845';\r\n" +
	                  "KW3846 : 'KW' '3846';\r\n" +
	                  "KW3847 : 'KW' '3847';\r\n" +
	                  "KW3848 : 'KW' '3848';\r\n" +
	                  "KW3849 : 'KW' '3849';\r\n" +
	                  "KW3850 : 'KW' '3850';\r\n" +
	                  "KW3851 : 'KW' '3851';\r\n" +
	                  "KW3852 : 'KW' '3852';\r\n" +
	                  "KW3853 : 'KW' '3853';\r\n" +
	                  "KW3854 : 'KW' '3854';\r\n" +
	                  "KW3855 : 'KW' '3855';\r\n" +
	                  "KW3856 : 'KW' '3856';\r\n" +
	                  "KW3857 : 'KW' '3857';\r\n" +
	                  "KW3858 : 'KW' '3858';\r\n" +
	                  "KW3859 : 'KW' '3859';\r\n" +
	                  "KW3860 : 'KW' '3860';\r\n" +
	                  "KW3861 : 'KW' '3861';\r\n" +
	                  "KW3862 : 'KW' '3862';\r\n" +
	                  "KW3863 : 'KW' '3863';\r\n" +
	                  "KW3864 : 'KW' '3864';\r\n" +
	                  "KW3865 : 'KW' '3865';\r\n" +
	                  "KW3866 : 'KW' '3866';\r\n" +
	                  "KW3867 : 'KW' '3867';\r\n" +
	                  "KW3868 : 'KW' '3868';\r\n" +
	                  "KW3869 : 'KW' '3869';\r\n" +
	                  "KW3870 : 'KW' '3870';\r\n" +
	                  "KW3871 : 'KW' '3871';\r\n" +
	                  "KW3872 : 'KW' '3872';\r\n" +
	                  "KW3873 : 'KW' '3873';\r\n" +
	                  "KW3874 : 'KW' '3874';\r\n" +
	                  "KW3875 : 'KW' '3875';\r\n" +
	                  "KW3876 : 'KW' '3876';\r\n" +
	                  "KW3877 : 'KW' '3877';\r\n" +
	                  "KW3878 : 'KW' '3878';\r\n" +
	                  "KW3879 : 'KW' '3879';\r\n" +
	                  "KW3880 : 'KW' '3880';\r\n" +
	                  "KW3881 : 'KW' '3881';\r\n" +
	                  "KW3882 : 'KW' '3882';\r\n" +
	                  "KW3883 : 'KW' '3883';\r\n" +
	                  "KW3884 : 'KW' '3884';\r\n" +
	                  "KW3885 : 'KW' '3885';\r\n" +
	                  "KW3886 : 'KW' '3886';\r\n" +
	                  "KW3887 : 'KW' '3887';\r\n" +
	                  "KW3888 : 'KW' '3888';\r\n" +
	                  "KW3889 : 'KW' '3889';\r\n" +
	                  "KW3890 : 'KW' '3890';\r\n" +
	                  "KW3891 : 'KW' '3891';\r\n" +
	                  "KW3892 : 'KW' '3892';\r\n" +
	                  "KW3893 : 'KW' '3893';\r\n" +
	                  "KW3894 : 'KW' '3894';\r\n" +
	                  "KW3895 : 'KW' '3895';\r\n" +
	                  "KW3896 : 'KW' '3896';\r\n" +
	                  "KW3897 : 'KW' '3897';\r\n" +
	                  "KW3898 : 'KW' '3898';\r\n" +
	                  "KW3899 : 'KW' '3899';\r\n" +
	                  "KW3900 : 'KW' '3900';\r\n" +
	                  "KW3901 : 'KW' '3901';\r\n" +
	                  "KW3902 : 'KW' '3902';\r\n" +
	                  "KW3903 : 'KW' '3903';\r\n" +
	                  "KW3904 : 'KW' '3904';\r\n" +
	                  "KW3905 : 'KW' '3905';\r\n" +
	                  "KW3906 : 'KW' '3906';\r\n" +
	                  "KW3907 : 'KW' '3907';\r\n" +
	                  "KW3908 : 'KW' '3908';\r\n" +
	                  "KW3909 : 'KW' '3909';\r\n" +
	                  "KW3910 : 'KW' '3910';\r\n" +
	                  "KW3911 : 'KW' '3911';\r\n" +
	                  "KW3912 : 'KW' '3912';\r\n" +
	                  "KW3913 : 'KW' '3913';\r\n" +
	                  "KW3914 : 'KW' '3914';\r\n" +
	                  "KW3915 : 'KW' '3915';\r\n" +
	                  "KW3916 : 'KW' '3916';\r\n" +
	                  "KW3917 : 'KW' '3917';\r\n" +
	                  "KW3918 : 'KW' '3918';\r\n" +
	                  "KW3919 : 'KW' '3919';\r\n" +
	                  "KW3920 : 'KW' '3920';\r\n" +
	                  "KW3921 : 'KW' '3921';\r\n" +
	                  "KW3922 : 'KW' '3922';\r\n" +
	                  "KW3923 : 'KW' '3923';\r\n" +
	                  "KW3924 : 'KW' '3924';\r\n" +
	                  "KW3925 : 'KW' '3925';\r\n" +
	                  "KW3926 : 'KW' '3926';\r\n" +
	                  "KW3927 : 'KW' '3927';\r\n" +
	                  "KW3928 : 'KW' '3928';\r\n" +
	                  "KW3929 : 'KW' '3929';\r\n" +
	                  "KW3930 : 'KW' '3930';\r\n" +
	                  "KW3931 : 'KW' '3931';\r\n" +
	                  "KW3932 : 'KW' '3932';\r\n" +
	                  "KW3933 : 'KW' '3933';\r\n" +
	                  "KW3934 : 'KW' '3934';\r\n" +
	                  "KW3935 : 'KW' '3935';\r\n" +
	                  "KW3936 : 'KW' '3936';\r\n" +
	                  "KW3937 : 'KW' '3937';\r\n" +
	                  "KW3938 : 'KW' '3938';\r\n" +
	                  "KW3939 : 'KW' '3939';\r\n" +
	                  "KW3940 : 'KW' '3940';\r\n" +
	                  "KW3941 : 'KW' '3941';\r\n" +
	                  "KW3942 : 'KW' '3942';\r\n" +
	                  "KW3943 : 'KW' '3943';\r\n" +
	                  "KW3944 : 'KW' '3944';\r\n" +
	                  "KW3945 : 'KW' '3945';\r\n" +
	                  "KW3946 : 'KW' '3946';\r\n" +
	                  "KW3947 : 'KW' '3947';\r\n" +
	                  "KW3948 : 'KW' '3948';\r\n" +
	                  "KW3949 : 'KW' '3949';\r\n" +
	                  "KW3950 : 'KW' '3950';\r\n" +
	                  "KW3951 : 'KW' '3951';\r\n" +
	                  "KW3952 : 'KW' '3952';\r\n" +
	                  "KW3953 : 'KW' '3953';\r\n" +
	                  "KW3954 : 'KW' '3954';\r\n" +
	                  "KW3955 : 'KW' '3955';\r\n" +
	                  "KW3956 : 'KW' '3956';\r\n" +
	                  "KW3957 : 'KW' '3957';\r\n" +
	                  "KW3958 : 'KW' '3958';\r\n" +
	                  "KW3959 : 'KW' '3959';\r\n" +
	                  "KW3960 : 'KW' '3960';\r\n" +
	                  "KW3961 : 'KW' '3961';\r\n" +
	                  "KW3962 : 'KW' '3962';\r\n" +
	                  "KW3963 : 'KW' '3963';\r\n" +
	                  "KW3964 : 'KW' '3964';\r\n" +
	                  "KW3965 : 'KW' '3965';\r\n" +
	                  "KW3966 : 'KW' '3966';\r\n" +
	                  "KW3967 : 'KW' '3967';\r\n" +
	                  "KW3968 : 'KW' '3968';\r\n" +
	                  "KW3969 : 'KW' '3969';\r\n" +
	                  "KW3970 : 'KW' '3970';\r\n" +
	                  "KW3971 : 'KW' '3971';\r\n" +
	                  "KW3972 : 'KW' '3972';\r\n" +
	                  "KW3973 : 'KW' '3973';\r\n" +
	                  "KW3974 : 'KW' '3974';\r\n" +
	                  "KW3975 : 'KW' '3975';\r\n" +
	                  "KW3976 : 'KW' '3976';\r\n" +
	                  "KW3977 : 'KW' '3977';\r\n" +
	                  "KW3978 : 'KW' '3978';\r\n" +
	                  "KW3979 : 'KW' '3979';\r\n" +
	                  "KW3980 : 'KW' '3980';\r\n" +
	                  "KW3981 : 'KW' '3981';\r\n" +
	                  "KW3982 : 'KW' '3982';\r\n" +
	                  "KW3983 : 'KW' '3983';\r\n" +
	                  "KW3984 : 'KW' '3984';\r\n" +
	                  "KW3985 : 'KW' '3985';\r\n" +
	                  "KW3986 : 'KW' '3986';\r\n" +
	                  "KW3987 : 'KW' '3987';\r\n" +
	                  "KW3988 : 'KW' '3988';\r\n" +
	                  "KW3989 : 'KW' '3989';\r\n" +
	                  "KW3990 : 'KW' '3990';\r\n" +
	                  "KW3991 : 'KW' '3991';\r\n" +
	                  "KW3992 : 'KW' '3992';\r\n" +
	                  "KW3993 : 'KW' '3993';\r\n" +
	                  "KW3994 : 'KW' '3994';\r\n" +
	                  "KW3995 : 'KW' '3995';\r\n" +
	                  "KW3996 : 'KW' '3996';\r\n" +
	                  "KW3997 : 'KW' '3997';\r\n" +
	                  "KW3998 : 'KW' '3998';\r\n" +
	                  "KW3999 : 'KW' '3999';\r";
		String found = execLexer("L.g4", grammar, "L", "KW400", false);
		assertEquals("[@0,0:4='KW400',<402>,1:0]\n" + 
	              "[@1,5:4='<EOF>',<-1>,1:5]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testZeroLengthToken() throws Exception {
		String grammar = "lexer grammar L;\r\n" +
	                  "BeginString\r\n" +
	                  "	:	'\\'' -> more, pushMode(StringMode)\r\n" +
	                  "	;\r\n" +
	                  "mode StringMode;\r\n" +
	                  "	StringMode_X : 'x' -> more;\r\n" +
	                  "	StringMode_Done : -> more, mode(EndStringMode);\r\n" +
	                  "mode EndStringMode;	\r\n" +
	                  "	EndString : '\\'' -> popMode;\r";
		String found = execLexer("L.g4", grammar, "L", "'xxx'", false);
		assertEquals("[@0,0:4=''xxx'',<1>,1:0]\n" + 
	              "[@1,5:4='<EOF>',<-1>,1:5]\n", found);
		assertNull(this.stderrDuringParse);
	}


}