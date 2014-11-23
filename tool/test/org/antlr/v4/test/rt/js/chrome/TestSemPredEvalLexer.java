package org.antlr.v4.test.rt.js.chrome;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestSemPredEvalLexer extends BaseTest {

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testDisableRule() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("lexer grammar L;\n");
		sb.append("E1 : 'enum' { false }? ;\n");
		sb.append("E2 : 'enum' { true }? ;  // winner not E1 or ID\n");
		sb.append("ID : 'a'..'z'+ ;\n");
		sb.append("WS : (' '|'\\n') -> skip;\n");
		String grammar = sb.toString();
		String found = execLexer("L.g4", grammar, "L", "enum abc", true);
		assertEquals("[@0,0:3='enum',<2>,1:0]\n" + 
	              "[@1,5:7='abc',<3>,1:5]\n" + 
	              "[@2,8:7='<EOF>',<-1>,1:8]\n" + 
	              "s0-' '->:s5=>4\n" + 
	              "s0-'a'->:s6=>3\n" + 
	              "s0-'e'->:s1=>3\n" + 
	              ":s1=>3-'n'->:s2=>3\n" + 
	              ":s2=>3-'u'->:s3=>3\n" + 
	              ":s6=>3-'b'->:s6=>3\n" + 
	              ":s6=>3-'c'->:s6=>3\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testIDvsEnum() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("lexer grammar L;\n");
		sb.append("ENUM : 'enum' { false }? ;\n");
		sb.append("ID : 'a'..'z'+ ;\n");
		sb.append("WS : (' '|'\\n') -> skip;\n");
		String grammar = sb.toString();
		String found = execLexer("L.g4", grammar, "L", "enum abc enum", true);
		assertEquals("[@0,0:3='enum',<2>,1:0]\n" + 
	              "[@1,5:7='abc',<2>,1:5]\n" + 
	              "[@2,9:12='enum',<2>,1:9]\n" + 
	              "[@3,13:12='<EOF>',<-1>,1:13]\n" + 
	              "s0-' '->:s5=>3\n" + 
	              "s0-'a'->:s4=>2\n" + 
	              "s0-'e'->:s1=>2\n" + 
	              ":s1=>2-'n'->:s2=>2\n" + 
	              ":s2=>2-'u'->:s3=>2\n" + 
	              ":s4=>2-'b'->:s4=>2\n" + 
	              ":s4=>2-'c'->:s4=>2\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testIDnotEnum() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("lexer grammar L;\n");
		sb.append("ENUM : [a-z]+  { false }? ;\n");
		sb.append("ID : [a-z]+  ;\n");
		sb.append("WS : (' '|'\\n') -> skip;\n");
		String grammar = sb.toString();
		String found = execLexer("L.g4", grammar, "L", "enum abc enum", true);
		assertEquals("[@0,0:3='enum',<2>,1:0]\n" + 
	              "[@1,5:7='abc',<2>,1:5]\n" + 
	              "[@2,9:12='enum',<2>,1:9]\n" + 
	              "[@3,13:12='<EOF>',<-1>,1:13]\n" + 
	              "s0-' '->:s2=>3\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testEnumNotID() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("lexer grammar L;\n");
		sb.append("ENUM : [a-z]+  { this.text===\"enum\" }? ;\n");
		sb.append("ID : [a-z]+  ;\n");
		sb.append("WS : (' '|'\\n') -> skip;\n");
		String grammar = sb.toString();
		String found = execLexer("L.g4", grammar, "L", "enum abc enum", true);
		assertEquals("[@0,0:3='enum',<1>,1:0]\n" + 
	              "[@1,5:7='abc',<2>,1:5]\n" + 
	              "[@2,9:12='enum',<1>,1:9]\n" + 
	              "[@3,13:12='<EOF>',<-1>,1:13]\n" + 
	              "s0-' '->:s3=>3\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testIndent() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("lexer grammar L;\n");
		sb.append("ID : [a-z]+  ;\n");
		sb.append("INDENT : [ \\t]+ { this._tokenStartColumn===0 }?\n");
		sb.append("         { document.getElementById('output').value += \"INDENT\" + '\\n'; }  ;\n");
		sb.append("NL : '\\n';\n");
		sb.append("WS : [ \\t]+ ;\n");
		String grammar = sb.toString();
		String found = execLexer("L.g4", grammar, "L", "abc\n  def  \n", true);
		assertEquals("INDENT\n" + 
	              "[@0,0:2='abc',<1>,1:0]\n" + 
	              "[@1,3:3='\\n',<3>,1:3]\n" + 
	              "[@2,4:5='  ',<2>,2:0]\n" + 
	              "[@3,6:8='def',<1>,2:2]\n" + 
	              "[@4,9:10='  ',<4>,2:5]\n" + 
	              "[@5,11:11='\\n',<3>,2:7]\n" + 
	              "[@6,12:11='<EOF>',<-1>,3:0]\n" + 
	              "s0-'\n" + 
	              "'->:s2=>3\n" + 
	              "s0-'a'->:s1=>1\n" + 
	              "s0-'d'->:s1=>1\n" + 
	              ":s1=>1-'b'->:s1=>1\n" + 
	              ":s1=>1-'c'->:s1=>1\n" + 
	              ":s1=>1-'e'->:s1=>1\n" + 
	              ":s1=>1-'f'->:s1=>1\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testLexerInputPositionSensitivePredicates() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("lexer grammar L;\n");
		sb.append("WORD1 : ID1+ { document.getElementById('output').value += this.text + '\\n'; } ;\n");
		sb.append("WORD2 : ID2+ { document.getElementById('output').value += this.text + '\\n'; } ;\n");
		sb.append("fragment ID1 : { this.column < 2 }? [a-zA-Z];\n");
		sb.append("fragment ID2 : { this.column >= 2 }? [a-zA-Z];\n");
		sb.append("WS : (' '|'\\n') -> skip;\n");
		String grammar = sb.toString();
		String found = execLexer("L.g4", grammar, "L", "a cde\nabcde\n", true);
		assertEquals("a\n" + 
	              "cde\n" + 
	              "ab\n" + 
	              "cde\n" + 
	              "[@0,0:0='a',<1>,1:0]\n" + 
	              "[@1,2:4='cde',<2>,1:2]\n" + 
	              "[@2,6:7='ab',<1>,2:0]\n" + 
	              "[@3,8:10='cde',<2>,2:2]\n" + 
	              "[@4,12:11='<EOF>',<-1>,3:0]\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testPredicatedKeywords() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("lexer grammar L;\n");
		sb.append("ENUM : [a-z]+ { this.text===\"enum\" }? { document.getElementById('output').value += \"enum!\" + '\\n'; } ;\n");
		sb.append("ID   : [a-z]+ { document.getElementById('output').value += \"ID \" + this.text + '\\n'; } ;\n");
		sb.append("WS   : [ \\n] -> skip ;\n");
		String grammar = sb.toString();
		String found = execLexer("L.g4", grammar, "L", "enum enu a", false);
		assertEquals("enum!\n" + 
	              "ID enu\n" + 
	              "ID a\n" + 
	              "[@0,0:3='enum',<1>,1:0]\n" + 
	              "[@1,5:7='enu',<2>,1:5]\n" + 
	              "[@2,9:9='a',<2>,1:9]\n" + 
	              "[@3,10:9='<EOF>',<-1>,1:10]\n", found);
		assertNull(this.stderrDuringParse);
	}


}