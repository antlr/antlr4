package org.antlr.v4.test;

import org.junit.Test;

public class TestSemPredEvalLexer extends BaseTest {

	@Test public void testDisableRule() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"E1 : 'enum' {false}? ;\n" +
			"E2 : 'enum' {true}? ;\n" +  // winner not E1 or ID
			"ID : 'a'..'z'+ ;\n"+
			"WS : (' '|'\\n') {skip();} ;";
		String found = execLexer("L.g4", grammar, "L", "enum abc", true);
		String expecting =
			"[@0,0:3='enum',<2>,1:0]\n" +
			"[@1,5:7='abc',<3>,1:5]\n" +
			"[@2,8:7='<EOF>',<-1>,1:8]\n" +
			"s0-' '->:s4=>4\n" +
			"s0-'a'->:s5=>3\n" +
			"s0-'e'->:s1=>3\n" +
			":s1=>3-'n'->:s2=>3\n" +
			":s2=>3-'u'->:s3=>3\n" +
			":s5=>3-'b'->:s5=>3\n" +
			":s5=>3-'c'->:s5=>3\n";
		assertEquals(expecting, found);
	}

	@Test public void testIDvsEnum() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"ENUM : 'enum' {false}? ;\n" +
			"ID : 'a'..'z'+ ;\n"+
			"WS : (' '|'\\n') {skip();} ;";
		String found = execLexer("L.g4", grammar, "L", "enum abc enum", true);
		String expecting =
			"[@0,0:3='enum',<2>,1:0]\n" +
			"[@1,5:7='abc',<2>,1:5]\n" +
			"[@2,9:12='enum',<2>,1:9]\n" +
			"[@3,13:12='<EOF>',<-1>,1:13]\n" +
			"s0-' '->:s5=>3\n" +
			"s0-'a'->:s4=>2\n" +
			"s0-'e'->:s1=>2\n" +
			":s1=>2-'n'->:s2=>2\n" +
			":s2=>2-'u'->:s3=>2\n" +
			":s4=>2-'b'->:s4=>2\n" +
			":s4=>2-'c'->:s4=>2\n"; // no 'm'-> transition...conflicts with pred
		assertEquals(expecting, found);
	}

	@Test public void testIDnotEnum() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"ENUM : [a-z]+ {false}? ;\n" +
			"ID   : [a-z]+ ;\n"+
			"WS : (' '|'\\n') {skip();} ;";
		String found = execLexer("L.g4", grammar, "L", "enum abc enum", true);
		String expecting =
			"[@0,0:3='enum',<2>,1:0]\n" +
			"[@1,5:7='abc',<2>,1:5]\n" +
			"[@2,9:12='enum',<2>,1:9]\n" +
			"[@3,13:12='<EOF>',<-1>,1:13]\n" +
			"s0-' '->:s2=>3\n"; // no DFA for enum/id. all paths lead to pred.
		assertEquals(expecting, found);
	}

	@Test public void testIndent() throws Exception {
		String grammar =
			"lexer grammar L;\n"+
			"ID : [a-z]+ ;\n"+
			"INDENT : [ \\t]+ {_tokenStartCharPositionInLine==0}? ;"+
			"NL     : '\\n' ;"+
			"WS     : [ \\t]+ ;";
		String found = execLexer("L.g4", grammar, "L", "abc\n  def  \n", true);
		String expecting =
			"[@0,0:2='abc',<1>,1:0]\n" +		// ID
			"[@1,3:3='\\n',<3>,1:3]\n" +  		// NL
			"[@2,4:5='  ',<2>,2:0]\n" +			// INDENT
			"[@3,6:8='def',<1>,2:2]\n" +		// ID
			"[@4,9:10='  ',<4>,2:5]\n" +		// WS
			"[@5,11:11='\\n',<3>,2:7]\n" +
			"[@6,12:11='<EOF>',<-1>,3:8]\n" +
			"s0-'\n" +
			"'->:s2=>3\n" +
			"s0-'a'->:s1=>1\n" +
			"s0-'d'->:s1=>1\n" +
			":s1=>1-'b'->:s1=>1\n" +
			":s1=>1-'c'->:s1=>1\n" +
			":s1=>1-'e'->:s1=>1\n" +
			":s1=>1-'f'->:s1=>1\n";
		assertEquals(expecting, found);
	}
}
