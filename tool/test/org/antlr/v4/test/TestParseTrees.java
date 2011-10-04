package org.antlr.v4.test;

import org.junit.Test;

public class TestParseTrees extends BaseTest {
	@Test public void testToken() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s\n" +
			"@init {setBuildParseTrees(true);}\n" +
			"@after {System.out.println($r.toStringTree(this));}\n" +
			"  :r=a ;\n" +
			"a : 'x' ;\n";
		String result = execParser("T.g", grammar, "TParser", "TLexer", "s", "x", false);
		String expecting = "(a x)\n";
		assertEquals(expecting, result);
	}

	@Test public void testToken2() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s\n" +
			"@init {setBuildParseTrees(true);}\n" +
			"@after {System.out.println($r.toStringTree(this));}\n" +
			"  :r=a ;\n" +
			"a : 'x' 'y'\n" +
			"  ;\n";
		String result = execParser("T.g", grammar, "TParser", "TLexer", "s", "xy", false);
		String expecting = "(a x y)\n";
		assertEquals(expecting, result);
	}

	@Test public void test2Alts() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s\n" +
			"@init {setBuildParseTrees(true);}\n" +
			"@after {System.out.println($r.toStringTree(this));}\n" +
			"  :r=a ;\n" +
			"a : 'x' | 'y'\n" +
			"  ;\n";
		String result = execParser("T.g", grammar, "TParser", "TLexer", "s", "y", false);
		String expecting = "(a y)\n";
		assertEquals(expecting, result);
	}

	@Test public void test2AltLoop() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s\n" +
			"@init {setBuildParseTrees(true);}\n" +
			"@after {System.out.println($r.toStringTree(this));}\n" +
			"  :r=a ;\n" +
			"a : ('x' | 'y')* 'z'\n" +
			"  ;\n";
		String result = execParser("T.g", grammar, "TParser", "TLexer", "s", "xyyxyxz", false);
		String expecting = "(a x y y x y x z)\n";
		assertEquals(expecting, result);
	}

	@Test public void testRuleRef() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s\n" +
			"@init {setBuildParseTrees(true);}\n" +
			"@after {System.out.println($r.toStringTree(this));}\n" +
			"  : r=a ;\n" +
			"a : b 'x'\n" +
			"  ;\n" +
			"b : 'y' ;\n";
		String result = execParser("T.g", grammar, "TParser", "TLexer", "s", "yx", false);
		String expecting = "(a (b y) x)\n";
		assertEquals(expecting, result);
	}

	// ERRORS

	@Test public void testExtraToken() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s\n" +
			"@init {setBuildParseTrees(true);}\n" +
			"@after {System.out.println($r.toStringTree(this));}\n" +
			"  : r=a ;\n" +
			"a : 'x' 'y'\n" +
			"  ;\n" +
			"Z : 'z'; \n";
		String result = execParser("T.g", grammar, "TParser", "TLexer", "s", "xzy", false);
		String expecting = "(a x <ERROR:z> y)\n";
		assertEquals(expecting, result);
	}

	@Test public void testNoViableAlt() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s\n" +
			"@init {setBuildParseTrees(true);}\n" +
			"@after {System.out.println($r.toStringTree(this));}\n" +
			"  : r=a ;\n" +
			"a : 'x' | 'y'\n" +
			"  ;\n" +
			"Z : 'z'; \n";
		String result = execParser("T.g", grammar, "TParser", "TLexer", "s", "z", false);
		String expecting = "(a <ERROR:z>)\n";
		assertEquals(expecting, result);
	}
}
