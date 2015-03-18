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


}