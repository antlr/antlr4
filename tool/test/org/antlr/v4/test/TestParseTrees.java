package org.antlr.v4.test;

import org.junit.Test;

public class TestParseTrees extends BaseTest {
	@Test public void testToken() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a\n" +
			"@init {setBuildParseTrees(true);}\n" +
			"@after {System.out.println(_localctx.toStringTree(this));}\n" +
			"  : 'x'\n" +
			"  ;\n";
		String result = execParser("T.g", grammar, "TParser", "TLexer", "a", "x", false);
		String expecting = "(a x)\n";
		assertEquals(expecting, result);
	}

	@Test public void testToken2() throws Exception {
		String grammar =
			"grammar T;\n" +
			"a\n" +
			"@init {setBuildParseTrees(true);}\n" +
			"@after {System.out.println(_localctx.toStringTree(this));}\n" +
			"  : 'x' 'y'\n" +
			"  ;\n";
		String result = execParser("T.g", grammar, "TParser", "TLexer", "a", "xy", false);
		String expecting = "(a x y)\n";
		assertEquals(expecting, result);
	}
}
