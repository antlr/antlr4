package org.antlr.v4.test;

import org.junit.Ignore;
import org.junit.Test;

public class TestSemPredEvalLexer extends BaseTest {

	@Test public void testDisableRule() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"E1 : {false}? 'enum' ;\n" +
			"E2 : {true}? 'enum' ;\n" +  // winner not E1 or ID
			"ID : 'a'..'z'+ ;\n"+
			"WS : (' '|'\\n') {skip();} ;";
		String found = execLexer("L.g4", grammar, "L", "enum abc", true);
		String expecting =
			"[@0,0:3='enum',<2>,1:0]\n" +
			"[@1,5:7='abc',<3>,1:5]\n" +
			"[@2,8:7='<EOF>',<-1>,1:8]\n"; // no dfa since preds on left edge
		assertEquals(expecting, found);
	}

	@Test public void testDisableRuleAfterMatch() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"E1 : 'enum' {false}? ;\n" +
			"E2 : 'enum' {true}? ;\n" +  // winner not E1 or ID
			"ID : 'a'..'z'+ ;\n"+
			"WS : (' '|'\\n') {skip();} ;";
		String found = execLexer("L.g4", grammar, "L", "enum abc enum", true);
		String expecting =
			"[@0,0:3='enum',<2>,1:0]\n" +
			"[@1,5:7='abc',<3>,1:5]\n" +
			"[@2,9:12='enum',<2>,1:9]\n" +
			"[@3,13:12='<EOF>',<-1>,1:13]\n" +
			"s0-' '->:s4=>4\n" +
			"s0-'a'->:s5=>3\n" +
			"s0-'e'->:s1=>3\n" +
			":s1=>3-'n'->:s2=>3\n" +
			":s2=>3-'u'->:s3=>3\n" +
			":s5=>3-'b'->:s5=>3\n" +
			":s5=>3-'c'->:s5=>3\n";
		// didn't even created DFA 2nd time; old target of 'u' has "pred" flag set
		assertEquals(expecting, found);
	}

	@Ignore
	public void testMatchNChar() throws Exception { // can't do locals yet
		String grammar =
			"lexer grammar L;\n"+
			"B : {int n=0;} ({n<=2}? DIGIT {n++})+ ;\n" +
			"fragment DIGIT : '0'..'9' ;\n"+
			"WS : (' '|'\\n') {skip();} ;";
		String found = execLexer("L.g4", grammar, "L", "1234 56", true);
		String expecting =
			"[@0,0:3='enum',<4>,1:0]\n" +
			"[@1,5:7='abc',<5>,1:5]\n" +
			"[@2,8:8='<EOF>',<-1>,1:8]\n"; // no dfa since preds on left edge
		assertEquals(expecting, found);
	}

}
