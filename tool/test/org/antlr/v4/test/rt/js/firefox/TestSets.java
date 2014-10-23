package org.antlr.v4.test.rt.js.firefox;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestSets extends BaseTest {

	@Test
	public void testSeqDoesNotBecomeSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : C {document.getElementById('output').value += this._input.getText() + '\\n';} ;\n" +
	                  "fragment A : '1' | '2';\n" +
	                  "fragment B : '3' '4';\n" +
	                  "C : A | B;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "34");
		assertEquals("34\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testParserSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : t=('x'|'y') {document.getElementById('output').value += $t.text + '\\n';} ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "x");
		assertEquals("x\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testParserNotSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : t=~('x'|'y') 'z' {document.getElementById('output').value += $t.text + '\\n';} ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "zz");
		assertEquals("z\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testParserNotToken() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : ~'x' 'z' {document.getElementById('output').value += this._input.getText() + '\\n';} ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "zz");
		assertEquals("zz\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testParserNotTokenWithLabel() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : t=~'x' 'z' {document.getElementById('output').value += $t.text + '\\n';} ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "zz");
		assertEquals("z\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testRuleAsSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a @after {document.getElementById('output').value += this._input.getText() + '\\n';} : 'a' | 'b' |'c' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "b");
		assertEquals("b\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testNotChar() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : A {document.getElementById('output').value += $A.text + '\\n';} ;\n" +
	                  "a : ~'b' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "x");
		assertEquals("x\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testOptionalSingleElement() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : A? 'c' {document.getElementById('output').value += this._input.getText() + '\\n';} ;\n" +
	                  "a : 'b' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "bc");
		assertEquals("bc\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testOptionalLexerSingleElement() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : A {document.getElementById('output').value += this._input.getText() + '\\n';} ;\n" +
	                  "a : 'b'? 'c' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "bc");
		assertEquals("bc\n", found);
		assertNull(this.stderrDuringParse);
	}

	String testStarLexerSingleElement(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : A {document.getElementById('output').value += this._input.getText() + '\\n';} ;\n" +
	                  "a : 'b'* 'c' ;";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", input);
	}

	@Test
	public void testStarLexerSingleElement_1() throws Exception {
		String found = testStarLexerSingleElement("bbbbc");
		assertEquals("bbbbc\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testStarLexerSingleElement_2() throws Exception {
		String found = testStarLexerSingleElement("c");
		assertEquals("c\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testPlusLexerSingleElement() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : A {document.getElementById('output').value += this._input.getText() + '\\n';} ;\n" +
	                  "a : 'b'+ 'c' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "bbbbc");
		assertEquals("bbbbc\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testOptionalSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : ('a'|'b')? 'c' {document.getElementById('output').value += this._input.getText() + '\\n';} ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "ac");
		assertEquals("ac\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testStarSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : ('a'|'b')* 'c' {document.getElementById('output').value += this._input.getText() + '\\n';} ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "abaac");
		assertEquals("abaac\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testPlusSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : ('a'|'b')+ 'c' {document.getElementById('output').value += this._input.getText() + '\\n';} ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "abaac");
		assertEquals("abaac\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testLexerOptionalSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : A {document.getElementById('output').value += this._input.getText() + '\\n';} ;\n" +
	                  "a : ('a'|'b')? 'c' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "ac");
		assertEquals("ac\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testLexerStarSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : A {document.getElementById('output').value += this._input.getText() + '\\n';} ;\n" +
	                  "a : ('a'|'b')* 'c' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "abaac");
		assertEquals("abaac\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testLexerPlusSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : A {document.getElementById('output').value += this._input.getText() + '\\n';} ;\n" +
	                  "a : ('a'|'b')+ 'c' ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "abaac");
		assertEquals("abaac\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testNotCharSet() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : A {document.getElementById('output').value += $A.text + '\\n';} ;\n" +
	                  "a : ~('b'|'c') ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "x");
		assertEquals("x\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testNotCharSetWithLabel() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : A {document.getElementById('output').value += $A.text + '\\n';} ;\n" +
	                  "a : h=~('b'|'c') ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "x");
		assertEquals("x\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testNotCharSetWithRuleRef3() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : A {document.getElementById('output').value += $A.text + '\\n';} ;\n" +
	                  "a : ('a'|B) ;  // this doesn't collapse to set but works\n" +
	                  "fragment\n" +
	                  "B : ~('a'|'c') ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "x");
		assertEquals("x\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testCharSetLiteral() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : (A {document.getElementById('output').value += $A.text + '\\n';})+ ;\n" +
	                  "a : [AaBb] ;\n" +
	                  "WS : (' '|'\\n')+ -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "A a B b");
		assertEquals("A\na\nB\nb\n", found);
		assertNull(this.stderrDuringParse);
	}


}