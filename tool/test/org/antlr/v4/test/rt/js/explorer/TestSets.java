package org.antlr.v4.test.rt.js.explorer;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestSets extends BaseTest {

	@Test
	public void testSeqDoesNotBecomeSet() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : C {document.getElementById('output').value += this._input.getText() + '\\n';} ;\r\n" +
	                  "fragment A : '1' | '2';\r\n" +
	                  "fragment B : '3' '4';\r\n" +
	                  "C : A | B;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "34", false);
		assertEquals("34\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testParserSet() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : t=('x'|'y') {document.getElementById('output').value += $t.text + '\\n';} ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "x", false);
		assertEquals("x\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testParserNotSet() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : t=~('x'|'y') 'z' {document.getElementById('output').value += $t.text + '\\n';} ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "zz", false);
		assertEquals("z\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testParserNotToken() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : ~'x' 'z' {document.getElementById('output').value += this._input.getText() + '\\n';} ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "zz", false);
		assertEquals("zz\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testParserNotTokenWithLabel() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : t=~'x' 'z' {document.getElementById('output').value += $t.text + '\\n';} ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "zz", false);
		assertEquals("z\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testRuleAsSet() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a @after {document.getElementById('output').value += this._input.getText() + '\\n';} : 'a' | 'b' |'c' ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "b", false);
		assertEquals("b\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testNotChar() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : A {document.getElementById('output').value += $A.text + '\\n';} ;\r\n" +
	                  "A : ~'b' ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "x", false);
		assertEquals("x\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testOptionalSingleElement() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : A? 'c' {document.getElementById('output').value += this._input.getText() + '\\n';} ;\r\n" +
	                  "A : 'b' ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "bc", false);
		assertEquals("bc\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testOptionalLexerSingleElement() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : A {document.getElementById('output').value += this._input.getText() + '\\n';} ;\r\n" +
	                  "A : 'b'? 'c' ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "bc", false);
		assertEquals("bc\n", found);
		assertNull(this.stderrDuringParse);
	}

	String testStarLexerSingleElement(String input) throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : A {document.getElementById('output').value += this._input.getText() + '\\n';} ;\r\n" +
	                  "A : 'b'* 'c' ;\r";
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", input, false);
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
		String grammar = "grammar T;\r\n" +
	                  "a : A {document.getElementById('output').value += this._input.getText() + '\\n';} ;\r\n" +
	                  "A : 'b'+ 'c' ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "bbbbc", false);
		assertEquals("bbbbc\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testOptionalSet() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : ('a'|'b')? 'c' {document.getElementById('output').value += this._input.getText() + '\\n';} ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "ac", false);
		assertEquals("ac\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testStarSet() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : ('a'|'b')* 'c' {document.getElementById('output').value += this._input.getText() + '\\n';} ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "abaac", false);
		assertEquals("abaac\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testPlusSet() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : ('a'|'b')+ 'c' {document.getElementById('output').value += this._input.getText() + '\\n';} ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "abaac", false);
		assertEquals("abaac\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testLexerOptionalSet() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : A {document.getElementById('output').value += this._input.getText() + '\\n';} ;\r\n" +
	                  "A : ('a'|'b')? 'c' ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "ac", false);
		assertEquals("ac\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testLexerStarSet() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : A {document.getElementById('output').value += this._input.getText() + '\\n';} ;\r\n" +
	                  "A : ('a'|'b')* 'c' ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "abaac", false);
		assertEquals("abaac\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testLexerPlusSet() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : A {document.getElementById('output').value += this._input.getText() + '\\n';} ;\r\n" +
	                  "A : ('a'|'b')+ 'c' ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "abaac", false);
		assertEquals("abaac\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testNotCharSet() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : A {document.getElementById('output').value += $A.text + '\\n';} ;\r\n" +
	                  "A : ~('b'|'c') ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "x", false);
		assertEquals("x\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testNotCharSetWithLabel() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : A {document.getElementById('output').value += $A.text + '\\n';} ;\r\n" +
	                  "A : h=~('b'|'c') ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "x", false);
		assertEquals("x\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testNotCharSetWithRuleRef3() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : A {document.getElementById('output').value += $A.text + '\\n';} ;\r\n" +
	                  "A : ('a'|B) ;  // this doesn't collapse to set but works\r\n" +
	                  "fragment\r\n" +
	                  "B : ~('a'|'c') ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "x", false);
		assertEquals("x\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testCharSetLiteral() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "a : (A {document.getElementById('output').value += $A.text + '\\n';})+ ;\r\n" +
	                  "A : [AaBb] ;\r\n" +
	                  "WS : (' '|'\\n')+ -> skip ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "A a B b", false);
		assertEquals("A\na\nB\nb\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testComplementSet() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "parse : ~NEW_LINE;\r\n" +
	                  "NEW_LINE: '\\r'? '\\n';";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "parse", "a", false);
		assertEquals("", found);
		assertEquals("line 1:0 token recognition error at: 'a'\nline 1:1 missing {} at '<EOF>'\n", this.stderrDuringParse);
	}


}