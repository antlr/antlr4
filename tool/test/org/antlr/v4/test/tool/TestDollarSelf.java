package org.antlr.v4.test.tool;

import static org.junit.Assert.*;

import org.antlr.v4.test.rt.java.BaseTest;
import org.junit.Test;

public class TestDollarSelf extends BaseTest {

	@Test
	public void testSimpleCall() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : ID  { System.out.println( $self.getSourceName() ); }\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "a", "x", true);
		assertTrue(found.indexOf(this.getClass().getSimpleName())>=0);
		assertNull(this.stderrDuringParse);
	}

}
