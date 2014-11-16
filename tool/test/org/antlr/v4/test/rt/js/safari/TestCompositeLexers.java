package org.antlr.v4.test.rt.js.safari;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestCompositeLexers extends BaseTest {

	@Test
	public void testLexerDelegatorInvokesDelegateRule() throws Exception {
		String slave_S = "lexer grammar S;\n" +
	                  "A : 'a' {document.getElementById('output').value += \"S.A\" + '\\n';};\n" +
	                  "C : 'c' ;";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave_S);

		String grammar = "lexer grammar M;\n" +
	                  "import S;\n" +
	                  "B : 'b';\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execLexer("M.g4", grammar, "M", "abc", false);
		assertEquals("S.A\n" + 
	              "[@0,0:0='a',<3>,1:0]\n" + 
	              "[@1,1:1='b',<1>,1:1]\n" + 
	              "[@2,2:2='c',<4>,1:2]\n" + 
	              "[@3,3:2='<EOF>',<-1>,1:3]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testLexerDelegatorRuleOverridesDelegate() throws Exception {
		String slave_S = "lexer grammar S;\n" +
	                  "A : 'a' {document.getElementById('output').value += \"S.A\" + '\\n';};\n" +
	                  "B : 'b' {document.getElementById('output').value += \"S.B\" + '\\n';};";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave_S);

		String grammar = "lexer grammar M;\n" +
	                  "import S;\n" +
	                  "A : 'a' B {document.getElementById('output').value += \"M.A\" + '\\n';};\n" +
	                  "WS : (' '|'\\n') -> skip ;";
		String found = execLexer("M.g4", grammar, "M", "ab", false);
		assertEquals("M.A\n" + 
	              "[@0,0:1='ab',<1>,1:0]\n" + 
	              "[@1,2:1='<EOF>',<-1>,1:2]\n", found);
		assertNull(this.stderrDuringParse);
	}


}