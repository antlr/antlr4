package org.antlr.v4.test.rt.js.explorer;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestCompositeLexers extends BaseTest {

	@Test
	public void testLexerDelegatorInvokesDelegateRule() throws Exception {
		String slave_S = "lexer grammar S;\r\n" +
	                  "A : 'a' {document.getElementById('output').value += \"S.A\" + '\\n';};\r\n" +
	                  "C : 'c' ;\r";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave_S);

		String grammar = "lexer grammar M;\r\n" +
	                  "import S;\r\n" +
	                  "B : 'b';\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
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
		String slave_S = "lexer grammar S;\r\n" +
	                  "A : 'a' {document.getElementById('output').value += \"S.A\" + '\\n';};\r\n" +
	                  "B : 'b' {document.getElementById('output').value += \"S.B\" + '\\n';};\r";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave_S);

		String grammar = "lexer grammar M;\r\n" +
	                  "import S;\r\n" +
	                  "A : 'a' B {document.getElementById('output').value += \"M.A\" + '\\n';};\r\n" +
	                  "WS : (' '|'\\n') -> skip ;\r";
		String found = execLexer("M.g4", grammar, "M", "ab", false);
		assertEquals("M.A\n" + 
	              "[@0,0:1='ab',<1>,1:0]\n" + 
	              "[@1,2:1='<EOF>',<-1>,1:2]\n", found);
		assertNull(this.stderrDuringParse);
	}


}