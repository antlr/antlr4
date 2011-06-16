package org.antlr.v4.test;

import org.junit.Test;

public class TestLexerExec extends BaseTest {
	@Test public void testRefToRuleDoesNotSetTokenNorEmitAnother() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"A : '-' I ;\n" +
			"I : '0'..'9'+ ;\n"+
			"WS : (' '|'\\n') {skip();} ;";
		String found = execLexer("L.g", grammar, "L", "34 -21 3");
		String expecting =
			"[@0,0:1='34',<4>,1:0]\n" +
			"[@1,3:5='-21',<3>,1:3]\n" +
			"[@2,7:7='3',<4>,1:7]\n" +
			"[@3,8:8='<EOF>',<-1>,1:8]\n";
		assertEquals(expecting, found);
	}

	@Test public void testActionExecutedInDFA() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"I : '0'..'9'+ {System.out.println(\"I\");} ;\n"+
			"WS : (' '|'\\n') {skip();} ;";
		String found = execLexer("L.g", grammar, "L", "34 34");
		String expecting =
			"I\n" +
			"I\n" +
			"[@0,0:1='34',<3>,1:0]\n" +
			"[@1,3:4='34',<3>,1:3]\n" +
			"[@2,5:5='<EOF>',<-1>,1:5]\n";
		assertEquals(expecting, found);
	}

	@Test public void testLexerMode() throws Exception {
		String grammar =
			"lexer grammar L;\n" +
			"STRING_START : '\"' {pushMode(STRING_MODE); more();} ;\n" +
			"WS : ' '|'\n' {skip();} ;\n"+
			"mode STRING_MODE;\n"+
			"STRING : '\"' {popMode();} ;\n"+
			"ANY : . {more();} ;\n";
		String found = execLexer("L.g", grammar, "L", "\"abc\" \"ab\"");
		String expecting =
			"[@0,0:4='\"abc\"',<5>,1:0]\n" +
			"[@1,6:9='\"ab\"',<5>,1:6]\n" +
			"[@2,10:10='<EOF>',<-1>,1:10]\n";
		assertEquals(expecting, found);
	}

	@Test public void testKeywordID() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"KEND : 'end' ;\n" + // has priority
			"ID : 'a'..'z'+ ;\n" +
			"WS : (' '|'\n')+ ;";
		String found = execLexer("L.g", grammar, "L", "end eend ending a");
		String expecting =
			"[@0,0:2='end',<3>,1:0]\n" +
			"[@1,3:3=' ',<5>,1:3]\n" +
			"[@2,4:7='eend',<4>,1:4]\n" +
			"[@3,8:8=' ',<5>,1:8]\n" +
			"[@4,9:14='ending',<4>,1:9]\n" +
			"[@5,15:15=' ',<5>,1:15]\n" +
			"[@6,16:16='a',<4>,1:16]\n" +
			"[@7,17:17='<EOF>',<-1>,1:17]\n";
		assertEquals(expecting, found);
	}
}
