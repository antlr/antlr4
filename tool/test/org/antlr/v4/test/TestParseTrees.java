package org.antlr.v4.test;

import org.junit.Test;

public class TestParseTrees extends BaseTest {
	@Test public void testTokenAndRuleContextString() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s\n" +
			"@init {setBuildParseTree(true);}\n" +
			"@after {System.out.println($r.ctx.toStringTree(this));}\n" +
			"  :r=a ;\n" +
			"a : 'x' {System.out.println(getRuleInvocationStack());} ;\n";
		String result = execParser("T.g4", grammar, "TParser", "TLexer", "s", "x", false);
		String expecting = "[a, s]\n(a x)\n";
		assertEquals(expecting, result);
	}

	@Test public void testToken2() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s\n" +
			"@init {setBuildParseTree(true);}\n" +
			"@after {System.out.println($r.ctx.toStringTree(this));}\n" +
			"  :r=a ;\n" +
			"a : 'x' 'y'\n" +
			"  ;\n";
		String result = execParser("T.g4", grammar, "TParser", "TLexer", "s", "xy", false);
		String expecting = "(a x y)\n";
		assertEquals(expecting, result);
	}

	@Test public void test2Alts() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s\n" +
			"@init {setBuildParseTree(true);}\n" +
			"@after {System.out.println($r.ctx.toStringTree(this));}\n" +
			"  :r=a ;\n" +
			"a : 'x' | 'y'\n" +
			"  ;\n";
		String result = execParser("T.g4", grammar, "TParser", "TLexer", "s", "y", false);
		String expecting = "(a y)\n";
		assertEquals(expecting, result);
	}

	@Test public void test2AltLoop() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s\n" +
			"@init {setBuildParseTree(true);}\n" +
			"@after {System.out.println($r.ctx.toStringTree(this));}\n" +
			"  :r=a ;\n" +
			"a : ('x' | 'y')* 'z'\n" +
			"  ;\n";
		String result = execParser("T.g4", grammar, "TParser", "TLexer", "s", "xyyxyxz", false);
		String expecting = "(a x y y x y x z)\n";
		assertEquals(expecting, result);
	}

	@Test public void testRuleRef() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s\n" +
			"@init {setBuildParseTree(true);}\n" +
			"@after {System.out.println($r.ctx.toStringTree(this));}\n" +
			"  : r=a ;\n" +
			"a : b 'x'\n" +
			"  ;\n" +
			"b : 'y' ;\n";
		String result = execParser("T.g4", grammar, "TParser", "TLexer", "s", "yx", false);
		String expecting = "(a (b y) x)\n";
		assertEquals(expecting, result);
	}

	// ERRORS

	@Test public void testExtraToken() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s\n" +
			"@init {setBuildParseTree(true);}\n" +
			"@after {System.out.println($r.ctx.toStringTree(this));}\n" +
			"  : r=a ;\n" +
			"a : 'x' 'y'\n" +
			"  ;\n" +
			"Z : 'z'; \n";
		String result = execParser("T.g4", grammar, "TParser", "TLexer", "s", "xzy", false);
		String expecting = "(a x z y)\n"; // ERRORs not shown. z is colored red in tree view
		assertEquals(expecting, result);
	}

	@Test public void testNoViableAlt() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s\n" +
			"@init {setBuildParseTree(true);}\n" +
			"@after {System.out.println($r.ctx.toStringTree(this));}\n" +
			"  : r=a ;\n" +
			"a : 'x' | 'y'\n" +
			"  ;\n" +
			"Z : 'z'; \n";
		String result = execParser("T.g4", grammar, "TParser", "TLexer", "s", "z", false);
		String expecting = "(a z)\n";
		assertEquals(expecting, result);
	}

	@Test public void testSync() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s\n" +
			"@init {setBuildParseTree(true);}\n" +
			"@after {System.out.println($r.ctx.toStringTree(this));}\n" +
			"  : r=a ;\n" +
			"a : 'x' 'y'* '!'\n" +
			"  ;\n" +
			"Z : 'z'; \n";
		String result = execParser("T.g4", grammar, "TParser", "TLexer", "s", "xzyy!", false);
		String expecting = "(a x z y y !)\n";
		assertEquals(expecting, result);
	}
}
