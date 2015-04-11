package org.antlr.v4.test.rt.java;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;


import org.antlr.v4.test.AntlrTestcase;

public class TestSets extends AntlrTestcase {

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testSeqDoesNotBecomeSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : C {System.out.println(this._input.getText());} ;\n" +
	                  "fragment A : '1' | '2';\n" +
	                  "fragment B : '3' '4';\n" +
	                  "C : A | B;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "34", false);
		assertEquals("34\n", found);
		assertThat(stderrDuringParse(), isEmptyOrNullString());
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testParserSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : t=('x'|'y') {System.out.println($t.text);} ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "x", false);
		assertEquals("x\n", found);
		assertThat(stderrDuringParse(), isEmptyOrNullString());
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testParserNotSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : t=~('x'|'y') 'z' {System.out.println($t.text);} ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "zz", false);
		assertEquals("z\n", found);
		assertThat(stderrDuringParse(), isEmptyOrNullString());
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testParserNotToken() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : ~'x' 'z' {System.out.println(this._input.getText());} ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "zz", false);
		assertEquals("zz\n", found);
		assertThat(stderrDuringParse(), isEmptyOrNullString());
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testParserNotTokenWithLabel() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : t=~'x' 'z' {System.out.println($t.text);} ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "zz", false);
		assertEquals("z\n", found);
		assertThat(stderrDuringParse(), isEmptyOrNullString());
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testRuleAsSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a @after {System.out.println(this._input.getText());} : 'a' | 'b' |'c' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "b", false);
		assertEquals("b\n", found);
		assertThat(stderrDuringParse(), isEmptyOrNullString());
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testNotChar() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : A {System.out.println($A.text);} ;\n" +
	                  "A : ~'b' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "x", false);
		assertEquals("x\n", found);
		assertThat(stderrDuringParse(), isEmptyOrNullString());
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testOptionalSingleElement() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : A? 'c' {System.out.println(this._input.getText());} ;\n" +
	                  "A : 'b' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "bc", false);
		assertEquals("bc\n", found);
		assertThat(stderrDuringParse(), isEmptyOrNullString());
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testOptionalLexerSingleElement() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : A {System.out.println(this._input.getText());} ;\n" +
	                  "A : 'b'? 'c' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "bc", false);
		assertEquals("bc\n", found);
		assertThat(stderrDuringParse(), isEmptyOrNullString());
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	String testStarLexerSingleElement(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : A {System.out.println(this._input.getText());} ;\n" +
	                  "A : 'b'* 'c' ;";
		return execParser("T.g4", grammar, "TParser", "TLexer", "a", input, false);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testStarLexerSingleElement_1() throws Exception {
		String found = testStarLexerSingleElement("bbbbc");
		assertEquals("bbbbc\n", found);
		assertThat(stderrDuringParse(), isEmptyOrNullString());
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testStarLexerSingleElement_2() throws Exception {
		String found = testStarLexerSingleElement("c");
		assertEquals("c\n", found);
		assertThat(stderrDuringParse(), isEmptyOrNullString());
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testPlusLexerSingleElement() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : A {System.out.println(this._input.getText());} ;\n" +
	                  "A : 'b'+ 'c' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "bbbbc", false);
		assertEquals("bbbbc\n", found);
		assertThat(stderrDuringParse(), isEmptyOrNullString());
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testOptionalSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : ('a'|'b')? 'c' {System.out.println(this._input.getText());} ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "ac", false);
		assertEquals("ac\n", found);
		assertThat(stderrDuringParse(), isEmptyOrNullString());
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testStarSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : ('a'|'b')* 'c' {System.out.println(this._input.getText());} ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "abaac", false);
		assertEquals("abaac\n", found);
		assertThat(stderrDuringParse(), isEmptyOrNullString());
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testPlusSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : ('a'|'b')+ 'c' {System.out.println(this._input.getText());} ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "abaac", false);
		assertEquals("abaac\n", found);
		assertThat(stderrDuringParse(), isEmptyOrNullString());
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testLexerOptionalSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : A {System.out.println(this._input.getText());} ;\n" +
	                  "A : ('a'|'b')? 'c' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "ac", false);
		assertEquals("ac\n", found);
		assertThat(stderrDuringParse(), isEmptyOrNullString());
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testLexerStarSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : A {System.out.println(this._input.getText());} ;\n" +
	                  "A : ('a'|'b')* 'c' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "abaac", false);
		assertEquals("abaac\n", found);
		assertThat(stderrDuringParse(), isEmptyOrNullString());
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testLexerPlusSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : A {System.out.println(this._input.getText());} ;\n" +
	                  "A : ('a'|'b')+ 'c' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "abaac", false);
		assertEquals("abaac\n", found);
		assertThat(stderrDuringParse(), isEmptyOrNullString());
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testNotCharSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : A {System.out.println($A.text);} ;\n" +
	                  "A : ~('b'|'c') ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "x", false);
		assertEquals("x\n", found);
		assertThat(stderrDuringParse(), isEmptyOrNullString());
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testNotCharSetWithLabel() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : A {System.out.println($A.text);} ;\n" +
	                  "A : h=~('b'|'c') ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "x", false);
		assertEquals("x\n", found);
		assertThat(stderrDuringParse(), isEmptyOrNullString());
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testNotCharSetWithRuleRef3() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : A {System.out.println($A.text);} ;\n" +
	                  "A : ('a'|B) ;  // this doesn't collapse to set but works\n" +
	                  "fragment\n" +
	                  "B : ~('a'|'c') ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "x", false);
		assertEquals("x\n", found);
		assertThat(stderrDuringParse(), isEmptyOrNullString());
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testCharSetLiteral() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : (A {System.out.println($A.text);})+ ;\n" +
	                  "A : [AaBb] ;\n" +
	                  "WS : (' '|'\\n')+ -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "A a B b", false);
		assertEquals("A\na\nB\nb\n", found);
		assertThat(stderrDuringParse(), isEmptyOrNullString());
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testComplementSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "parse : ~NEW_LINE;\n" +
	                  "NEW_LINE: '\\r'? '\\n';";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "parse", "a", false);
		assertEquals("", found);
		assertEquals("line 1:0 token recognition error at: 'a'\nline 1:1 missing {} at '<EOF>'\n", stderrDuringParse());
	}


}