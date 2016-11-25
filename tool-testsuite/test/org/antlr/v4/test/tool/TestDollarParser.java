package org.antlr.v4.test.tool;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestDollarParser extends BaseJavaToolTest {
	@Before
	@Override
	public void testSetUp() throws Exception {
		super.testSetUp();
	}

	@Test
	public void testSimpleCall() throws Exception {
		String grammar = "grammar T;\n" +
	                  "a : ID  { System.out.println( $parser.getSourceName() ); }\n" +
	                  "  ;\n" +
	                  "ID : 'a'..'z'+ ;\n";
		String found = execParser("T.g4", grammar, "TParser", "TLexer",
		                          null, null, "a", "x", true);
		assertTrue(found.indexOf(this.getClass().getSimpleName())>=0);
		assertNull(this.stderrDuringParse);
	}

}
