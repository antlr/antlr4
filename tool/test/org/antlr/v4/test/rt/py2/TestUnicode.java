package org.antlr.v4.test.rt.py2;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestUnicode extends BasePython2Test {

	@Test
	public void testUnicode() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("grammar L;\n");
		sb.append("stm : ID ;\n");
		sb.append("ID: 'a';\n");
		String grammar = sb.toString();
		String found = execParser("L.g4", grammar, "LParser", "LLexer", "LListener", "LVisitor", "stm", "a\n", false);
		assertEquals("", found);
		assertEquals("line 1:1 token recognition error at: '\\n'\n", this.stderrDuringParse);
	}

}
