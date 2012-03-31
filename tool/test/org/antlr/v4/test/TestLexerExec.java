package org.antlr.v4.test;

import org.junit.Test;

public class TestLexerExec extends BaseTest {
    @Test public void testQuoteTranslation() throws Exception {
   		String grammar =
   			"lexer grammar L;\n"+
   			"QUOTE : '\"' ;\n"; // make sure this compiles
   		String found = execLexer("L.g4", grammar, "L", "\"");
   		String expecting =
   			"[@0,0:0='\"',<3>,1:0]\n" +
            "[@1,1:0='<EOF>',<-1>,1:1]\n";
   		assertEquals(expecting, found);
   	}

    @Test public void testRefToRuleDoesNotSetTokenNorEmitAnother() throws Exception {
   		String grammar =
   			"lexer grammar L;\n"+
   			"A : '-' I ;\n" +
   			"I : '0'..'9'+ ;\n"+
   			"WS : (' '|'\\n') {skip();} ;";
   		String found = execLexer("L.g4", grammar, "L", "34 -21 3");
   		String expecting =
   			"[@0,0:1='34',<4>,1:0]\n" +
   			"[@1,3:5='-21',<3>,1:3]\n" +
   			"[@2,7:7='3',<4>,1:7]\n" +
   			"[@3,8:7='<EOF>',<-1>,1:8]\n"; // EOF has no length so range is 8:7 not 8:8
   		assertEquals(expecting, found);
   	}

	@Test public void testActionExecutedInDFA() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"I : '0'..'9'+ {System.out.println(\"I\");} ;\n"+
			"WS : (' '|'\\n') {skip();} ;";
		String found = execLexer("L.g4", grammar, "L", "34 34");
		String expecting =
			"I\n" +
			"I\n" +
			"[@0,0:1='34',<3>,1:0]\n" +
			"[@1,3:4='34',<3>,1:3]\n" +
			"[@2,5:4='<EOF>',<-1>,1:5]\n";
		assertEquals(expecting, found);
	}

	@Test public void testSkipCommand() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"I : '0'..'9'+ {System.out.println(\"I\");} ;\n"+
			"WS : (' '|'\\n') -> skip ;";
		String found = execLexer("L.g4", grammar, "L", "34 34");
		String expecting =
			"I\n" +
			"I\n" +
			"[@0,0:1='34',<3>,1:0]\n" +
			"[@1,3:4='34',<3>,1:3]\n" +
			"[@2,5:4='<EOF>',<-1>,1:5]\n";
		assertEquals(expecting, found);
	}

	@Test public void testMoreCommand() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"I : '0'..'9'+ {System.out.println(\"I\");} ;\n"+
			"WS : '#' -> more ;";
		String found = execLexer("L.g4", grammar, "L", "34#10");
		String expecting =
			"I\n" +
			"I\n" +
			"[@0,0:1='34',<3>,1:0]\n" +
			"[@1,2:4='#10',<3>,1:2]\n" +
			"[@2,5:4='<EOF>',<-1>,1:5]\n";
		assertEquals(expecting, found);
	}

	@Test public void testTypeCommand() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"I : '0'..'9'+ {System.out.println(\"I\");} ;\n"+
			"HASH : '#' -> type(HASH) ;";
		String found = execLexer("L.g4", grammar, "L", "34#");
		String expecting =
			"I\n" +
			"[@0,0:1='34',<3>,1:0]\n" +
			"[@1,2:2='#',<4>,1:2]\n" +
			"[@2,3:2='<EOF>',<-1>,1:3]\n";
		assertEquals(expecting, found);
	}

	@Test public void testCombinedCommand() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"I : '0'..'9'+ {System.out.println(\"I\");} ;\n"+
			"HASH : '#' -> type(HASH), skip, more  ;";
		String found = execLexer("L.g4", grammar, "L", "34#11");
		String expecting =
			"I\n" +
			"I\n" +
			"[@0,0:1='34',<3>,1:0]\n" +
			"[@1,2:4='#11',<3>,1:2]\n" +
			"[@2,5:4='<EOF>',<-1>,1:5]\n";
		assertEquals(expecting, found);
	}

	@Test public void testLexerMode() throws Exception {
		String grammar =
			"lexer grammar L;\n" +
			"STRING_START : '\"' {pushMode(STRING_MODE); more();} ;\n" +
			"WS : (' '|'\n') {skip();} ;\n"+
			"mode STRING_MODE;\n"+
			"STRING : '\"' {popMode();} ;\n"+
			"ANY : . {more();} ;\n";
		String found = execLexer("L.g4", grammar, "L", "\"abc\" \"ab\"");
		String expecting =
			"[@0,0:4='\"abc\"',<5>,1:0]\n" +
			"[@1,6:9='\"ab\"',<5>,1:6]\n" +
			"[@2,10:9='<EOF>',<-1>,1:10]\n";
		assertEquals(expecting, found);
	}

	@Test public void testLexerPushPopModeAction() throws Exception {
		String grammar =
			"lexer grammar L;\n" +
			"STRING_START : '\"' -> pushMode(STRING_MODE), more ;\n" +
			"WS : (' '|'\n') -> skip ;\n"+
			"mode STRING_MODE;\n"+
			"STRING : '\"' -> popMode ;\n"+
			"ANY : . -> more ;\n";
		String found = execLexer("L.g4", grammar, "L", "\"abc\" \"ab\"");
		String expecting =
			"[@0,0:4='\"abc\"',<5>,1:0]\n" +
			"[@1,6:9='\"ab\"',<5>,1:6]\n" +
			"[@2,10:9='<EOF>',<-1>,1:10]\n";
		assertEquals(expecting, found);
	}

	@Test public void testLexerModeAction() throws Exception {
		String grammar =
			"lexer grammar L;\n" +
			"STRING_START : '\"' -> mode(STRING_MODE), more ;\n" +
			"WS : (' '|'\n') -> skip ;\n"+
			"mode STRING_MODE;\n"+
			"STRING : '\"' -> mode(DEFAULT_MODE) ;\n"+
			"ANY : . -> more ;\n";
		String found = execLexer("L.g4", grammar, "L", "\"abc\" \"ab\"");
		String expecting =
			"[@0,0:4='\"abc\"',<5>,1:0]\n" +
			"[@1,6:9='\"ab\"',<5>,1:6]\n" +
			"[@2,10:9='<EOF>',<-1>,1:10]\n";
		assertEquals(expecting, found);
	}

	@Test public void testKeywordID() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"KEND : 'end' ;\n" + // has priority
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\n')+ ;";
		String found = execLexer("L.g4", grammar, "L", "end eend ending a");
		String expecting =
			"[@0,0:2='end',<3>,1:0]\n" +
			"[@1,3:3=' ',<5>,1:3]\n" +
			"[@2,4:7='eend',<4>,1:4]\n" +
			"[@3,8:8=' ',<5>,1:8]\n" +
			"[@4,9:14='ending',<4>,1:9]\n" +
			"[@5,15:15=' ',<5>,1:15]\n" +
			"[@6,16:16='a',<4>,1:16]\n" +
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
			"WS : (' '|'\n')+ ;";
		String found = execLexer("L.g4", grammar, "L", "x 0 1 a.b a.l");
		String expecting =
			"[@0,0:0='x',<7>,1:0]\n" +
			"[@1,1:1=' ',<8>,1:1]\n" +
			"[@2,2:2='0',<4>,1:2]\n" +
			"[@3,3:3=' ',<8>,1:3]\n" +
			"[@4,4:4='1',<4>,1:4]\n" +
			"[@5,5:5=' ',<8>,1:5]\n" +
			"[@6,6:6='a',<7>,1:6]\n" +
			"[@7,7:7='.',<6>,1:7]\n" +
			"[@8,8:8='b',<7>,1:8]\n" +
			"[@9,9:9=' ',<8>,1:9]\n" +
			"[@10,10:10='a',<7>,1:10]\n" +
			"[@11,11:11='.',<6>,1:11]\n" +
			"[@12,12:12='l',<7>,1:12]\n" +
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
			"[@0,0:-1='<EOF>',<3>,1:0]\n" +
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
			"[@0,0:0='a',<3>,1:0]\n" +
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
			"[@0,0:1='34',<3>,1:0]\n" +
			"[@1,5:6='34',<3>,2:1]\n" +
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
			"[@0,0:1='34',<3>,1:0]\n" +
			"[@1,5:6='34',<3>,2:1]\n" +
			"[@2,7:6='<EOF>',<-1>,2:3]\n";
		assertEquals(expecting, found);
	}

	@Test public void testCharSetNot() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"I : ~[ab \n] ~[ \ncd]* {System.out.println(\"I\");} ;\n"+
			"WS : [ \\n\\u000D]+ -> skip ;";
		String found = execLexer("L.g4", grammar, "L", "xaf");
		String expecting =
			"I\n" +
			"[@0,0:2='xaf',<3>,1:0]\n" +
			"[@1,3:2='<EOF>',<-1>,1:3]\n";
		assertEquals(expecting, found);
	}

	@Test public void testCharSetInSet() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"I : (~[ab \n]|'a') {System.out.println(\"I\");} ;\n"+
			"WS : [ \\n\\u000D]+ -> skip ;";
		String found = execLexer("L.g4", grammar, "L", "a x");
		String expecting =
			"I\n" +
			"I\n" +
			"[@0,0:0='a',<3>,1:0]\n" +
			"[@1,2:2='x',<3>,1:2]\n" +
			"[@2,3:2='<EOF>',<-1>,1:3]\n";
		assertEquals(expecting, found);
	}

	@Test public void testCharSetRange() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"I : [0-9]+ {System.out.println(\"I\");} ;\n"+
			"ID : [a-zA-Z] [a-zA-Z0-9]* {System.out.println(\"ID\");} ;\n"+
			"WS : [ \\n\\u0009\r]+ -> skip ;";
		String found = execLexer("L.g4", grammar, "L", "34\r 34 a2 abc \n   ");
		String expecting =
			"I\n" +
			"I\n" +
			"ID\n" +
			"ID\n" +
			"[@0,0:1='34',<3>,1:0]\n" +
			"[@1,4:5='34',<3>,1:4]\n" +
			"[@2,7:8='a2',<4>,1:7]\n" +
			"[@3,10:12='abc',<4>,1:10]\n" +
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
			"[@0,0:1='00',<3>,1:0]\n" +
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
			"[@0,0:1='34',<3>,1:0]\n" +
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
			"[@0,0:0='-',<3>,1:0]\n" +
			"[@1,2:2=']',<3>,1:2]\n" +
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
			"[@0,0:0='9',<3>,1:0]\n" +
			"[@1,1:0='<EOF>',<-1>,1:1]\n";
		assertEquals(expecting, found);
	}

	@Test public void testCharSetWithQuote() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"A : [\"a-z]+ {System.out.println(\"A\");} ;\n"+
			"WS : [ \n\t]+ -> skip ;";
		String found = execLexer("L.g4", grammar, "L", "b\"a");
		String expecting =
			"A\n" +
			"[@0,0:2='b\"a',<3>,1:0]\n" +
			"[@1,3:2='<EOF>',<-1>,1:3]\n";
		assertEquals(expecting, found);
	}

	@Test public void testCharSetWithQuote2() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"A : [\"\\\\ab]+ {System.out.println(\"A\");} ;\n"+
			"WS : [ \n\t]+ -> skip ;";
		String found = execLexer("L.g4", grammar, "L", "b\"\\a");
		String expecting =
			"A\n" +
			"[@0,0:3='b\"\\a',<3>,1:0]\n" +
			"[@1,4:3='<EOF>',<-1>,1:4]\n";
		assertEquals(expecting, found);
	}

}
