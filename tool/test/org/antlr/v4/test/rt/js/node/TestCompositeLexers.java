package org.antlr.v4.test.rt.js.node;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestCompositeLexers extends BaseTest {

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testLexerDelegatorInvokesDelegateRule() throws Exception {
		String slave_S = "lexer grammar S;\n" +
	                  "A : 'a' {console.log(\"S.A\");};\n" +
	                  "C : 'c' ;";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave_S);

		StringBuilder sb = new StringBuilder();
		sb.append("lexer grammar M;\n");
		sb.append("import S;\n");
		sb.append("B : 'b';\n");
		sb.append("WS : (' '|'\\n') -> skip ;\n");
		String grammar = sb.toString();
		String found = execLexer("M.g4", grammar, "M", "abc", false);
		assertEquals("S.A\n" + 
	              "[@0,0:0='a',<3>,1:0]\n" + 
	              "[@1,1:1='b',<1>,1:1]\n" + 
	              "[@2,2:2='c',<4>,1:2]\n" + 
	              "[@3,3:2='<EOF>',<-1>,1:3]\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testLexerDelegatorRuleOverridesDelegate() throws Exception {
		String slave_S = "lexer grammar S;\n" +
	                  "A : 'a' {console.log(\"S.A\");};\n" +
	                  "B : 'b' {console.log(\"S.B\");};";
		mkdir(tmpdir);
		writeFile(tmpdir, "S.g4", slave_S);

		StringBuilder sb = new StringBuilder();
		sb.append("lexer grammar M;\n");
		sb.append("import S;\n");
		sb.append("A : 'a' B {console.log(\"M.A\");};\n");
		sb.append("WS : (' '|'\\n') -> skip ;\n");
		String grammar = sb.toString();
		String found = execLexer("M.g4", grammar, "M", "ab", false);
		assertEquals("M.A\n" + 
	              "[@0,0:1='ab',<1>,1:0]\n" + 
	              "[@1,2:1='<EOF>',<-1>,1:2]\n", found);
		assertNull(this.stderrDuringParse);
	}


}