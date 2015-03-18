package org.antlr.v4.test.rt.py3;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestParams extends BasePython3Test {

	@Test
	public void testParamNoType() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a[rows]: 'a' {print(self._input.getText())} ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a(None)", "a", false);
		assertEquals("a\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testParamList() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a[list rows]: 'a' {print(self._input.getText())} # r1 | 'A' {print(self._input.getText())} # r2;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a(None)", "a", false);
		assertEquals("a\n", found);
		assertNull(this.stderrDuringParse);
	}
	
	@Test
	public void testParamInit() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a locals[i=23]: 'a' {print(str($i))} ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "a", "a", false);
		assertEquals("23\n", found);
		assertNull(this.stderrDuringParse);
	}


}