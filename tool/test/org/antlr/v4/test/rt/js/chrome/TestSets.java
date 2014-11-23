package org.antlr.v4.test.rt.js.chrome;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestSets extends BaseTest {

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testSeqDoesNotBecomeSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : C {document.getElementById('output').value += this._input.getText() + '\\n';} ;\n" +
	                  "fragment A : '1' | '2';\n" +
	                  "fragment B : '3' '4';\n" +
	                  "C : A | B;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "34", false);
		assertEquals("34\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testParserSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : t=('x'|'y') {document.getElementById('output').value += $t.text + '\\n';} ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "x", false);
		assertEquals("x\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testParserNotSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : t=~('x'|'y') 'z' {document.getElementById('output').value += $t.text + '\\n';} ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "zz", false);
		assertEquals("z\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testParserNotToken() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : ~'x' 'z' {document.getElementById('output').value += this._input.getText() + '\\n';} ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "zz", false);
		assertEquals("zz\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testParserNotTokenWithLabel() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : t=~'x' 'z' {document.getElementById('output').value += $t.text + '\\n';} ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "zz", false);
		assertEquals("z\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testRuleAsSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a @after {document.getElementById('output').value += this._input.getText() + '\\n';} : 'a' | 'b' |'c' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "b", false);
		assertEquals("b\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testNotChar() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : A {document.getElementById('output').value += $A.text + '\\n';} ;\n" +
	                  "A : ~'b' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "x", false);
		assertEquals("x\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testOptionalSingleElement() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : A? 'c' {document.getElementById('output').value += this._input.getText() + '\\n';} ;\n" +
	                  "A : 'b' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "bc", false);
		assertEquals("bc\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testOptionalLexerSingleElement() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : A {document.getElementById('output').value += this._input.getText() + '\\n';} ;\n" +
	                  "A : 'b'? 'c' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "bc", false);
		assertEquals("bc\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	String testStarLexerSingleElement(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : A {document.getElementById('output').value += this._input.getText() + '\\n';} ;\n" +
	                  "A : 'b'* 'c' ;";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", input, false);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testStarLexerSingleElement_1() throws Exception {
		String found = testStarLexerSingleElement("bbbbc");
		assertEquals("bbbbc\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testStarLexerSingleElement_2() throws Exception {
		String found = testStarLexerSingleElement("c");
		assertEquals("c\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testPlusLexerSingleElement() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : A {document.getElementById('output').value += this._input.getText() + '\\n';} ;\n" +
	                  "A : 'b'+ 'c' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "bbbbc", false);
		assertEquals("bbbbc\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testOptionalSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : ('a'|'b')? 'c' {document.getElementById('output').value += this._input.getText() + '\\n';} ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "ac", false);
		assertEquals("ac\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testStarSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : ('a'|'b')* 'c' {document.getElementById('output').value += this._input.getText() + '\\n';} ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "abaac", false);
		assertEquals("abaac\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testPlusSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : ('a'|'b')+ 'c' {document.getElementById('output').value += this._input.getText() + '\\n';} ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "abaac", false);
		assertEquals("abaac\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testLexerOptionalSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : A {document.getElementById('output').value += this._input.getText() + '\\n';} ;\n" +
	                  "A : ('a'|'b')? 'c' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "ac", false);
		assertEquals("ac\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testLexerStarSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : A {document.getElementById('output').value += this._input.getText() + '\\n';} ;\n" +
	                  "A : ('a'|'b')* 'c' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "abaac", false);
		assertEquals("abaac\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testLexerPlusSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : A {document.getElementById('output').value += this._input.getText() + '\\n';} ;\n" +
	                  "A : ('a'|'b')+ 'c' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "abaac", false);
		assertEquals("abaac\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testNotCharSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : A {document.getElementById('output').value += $A.text + '\\n';} ;\n" +
	                  "A : ~('b'|'c') ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "x", false);
		assertEquals("x\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testNotCharSetWithLabel() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : A {document.getElementById('output').value += $A.text + '\\n';} ;\n" +
	                  "A : h=~('b'|'c') ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "x", false);
		assertEquals("x\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testNotCharSetWithRuleRef3() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : A {document.getElementById('output').value += $A.text + '\\n';} ;\n" +
	                  "A : ('a'|B) ;  // this doesn't collapse to set but works\n" +
	                  "fragment\n" +
	                  "B : ~('a'|'c') ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "x", false);
		assertEquals("x\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testCharSetLiteral() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : (A {document.getElementById('output').value += $A.text + '\\n';})+ ;\n" +
	                  "A : [AaBb] ;\n" +
	                  "WS : (' '|'\\n')+ -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "A a B b", false);
		assertEquals("A\na\nB\nb\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testComplementSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "parse : ~NEW_LINE;\n" +
	                  "NEW_LINE: '\\r'? '\\n';";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "parse", "a", false);
		assertEquals("", found);
		assertEquals("line 1:0 token recognition error at: 'a'\nline 1:1 missing {} at '<EOF>'\n", this.stderrDuringParse);
	}


}