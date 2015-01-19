package org.antlr.v4.test.rt.py2;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestTrace extends BasePython2Test {

	@Test
	public void testTrace() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("grammar L;\n");
		sb.append("stm : ID ;\n");
		sb.append("ID: 'a';\n");
		String grammar = sb.toString();
		String found = execParser("L.g4", grammar, "LParser", "LLexer", "LListener", "LVisitor", "stm", "a", false, true);
		String expected = "enter   stm, LT(1)=a\n" +
				"consume [@0,0:0='a',<1>,1:0] rule stm\n" +
				"exit    stm, LT(1)=<EOF>\n";
		assertEquals(expected, found);
		assertNull(this.stderrDuringParse);
	}



}