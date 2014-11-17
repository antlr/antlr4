package org.antlr.v4.test.rt.csharp;

import org.junit.Test;

public class TestParseTrees extends BaseTest {

	@Test
	public void testTokenAndRuleContextString() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s\n" +
	                  "@init {\n" +
	                  "this.BuildParseTree = true;\n" +
	                  "}\n" +
	                  "@after {\n" +
	                  "Console.WriteLine($r.ctx.ToStringTree(this));\n" +
	                  "}\n" +
	                  "  : r=a ;\n" +
	                  "a : 'x' { \n" +
	                  "Console.WriteLine(GetRuleInvocationStackAsString());\n" +
	                  "} ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", "x", false);
		assertEquals("[a, s]\n(a x)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testToken2() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s\n" +
	                  "@init {\n" +
	                  "this.BuildParseTree = true;\n" +
	                  "}\n" +
	                  "@after {\n" +
	                  "Console.WriteLine($r.ctx.ToStringTree(this));\n" +
	                  "}\n" +
	                  "  : r=a ;\n" +
	                  "a : 'x' 'y'\n" +
	                  "  ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", "xy", false);
		assertEquals("(a x y)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void test2Alts() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s\n" +
	                  "@init {\n" +
	                  "this.BuildParseTree = true;\n" +
	                  "}\n" +
	                  "@after {\n" +
	                  "Console.WriteLine($r.ctx.ToStringTree(this));\n" +
	                  "}\n" +
	                  "  : r=a ;\n" +
	                  "a : 'x' | 'y'\n" +
	                  "  ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", "y", false);
		assertEquals("(a y)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void test2AltLoop() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s\n" +
	                  "@init {\n" +
	                  "this.BuildParseTree = true;\n" +
	                  "}\n" +
	                  "@after {\n" +
	                  "Console.WriteLine($r.ctx.ToStringTree(this));\n" +
	                  "}\n" +
	                  "  : r=a ;\n" +
	                  "a : ('x' | 'y')* 'z'\n" +
	                  "  ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", "xyyxyxz", false);
		assertEquals("(a x y y x y x z)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testRuleRef() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s\n" +
	                  "@init {\n" +
	                  "this.BuildParseTree = true;\n" +
	                  "}\n" +
	                  "@after {\n" +
	                  "Console.WriteLine($r.ctx.ToStringTree(this));\n" +
	                  "}\n" +
	                  "  : r=a ;\n" +
	                  "a : b 'x'\n" +
	                  "  ;\n" +
	                  "b : 'y' \n" +
	                  "  ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", "yx", false);
		assertEquals("(a (b y) x)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testExtraToken() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s\n" +
	                  "@init {\n" +
	                  "this.BuildParseTree = true;\n" +
	                  "}\n" +
	                  "@after {\n" +
	                  "Console.WriteLine($r.ctx.ToStringTree(this));\n" +
	                  "}\n" +
	                  "  : r=a ;\n" +
	                  "a : 'x' 'y'\n" +
	                  "  ;\n" +
	                  "Z : 'z' \n" +
	                  "  ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", "xzy", false);
		assertEquals("(a x z y)\n", found);
		assertEquals("line 1:1 extraneous input 'z' expecting 'y'\n", this.stderrDuringParse);
	}

	@Test
	public void testNoViableAlt() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s\n" +
	                  "@init {\n" +
	                  "this.BuildParseTree = true;\n" +
	                  "}\n" +
	                  "@after {\n" +
	                  "Console.WriteLine($r.ctx.ToStringTree(this));\n" +
	                  "}\n" +
	                  "  : r=a ;\n" +
	                  "a : 'x' | 'y'\n" +
	                  "  ;\n" +
	                  "Z : 'z' \n" +
	                  "  ;\n" +
	                  " ";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", "z", false);
		assertEquals("(a z)\n", found);
		assertEquals("line 1:0 mismatched input 'z' expecting {'x', 'y'}\n", this.stderrDuringParse);
	}

	@Test
	public void testSync() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s\n" +
	                  "@init {\n" +
	                  "this.BuildParseTree = true;\n" +
	                  "}\n" +
	                  "@after {\n" +
	                  "Console.WriteLine($r.ctx.ToStringTree(this));\n" +
	                  "}\n" +
	                  "  : r=a ;\n" +
	                  "a : 'x' 'y'* '!'\n" +
	                  "  ;\n" +
	                  "Z : 'z' \n" +
	                  "  ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", "xzyy!", false);
		assertEquals("(a x z y y !)\n", found);
		assertEquals("line 1:1 extraneous input 'z' expecting {'y', '!'}\n", this.stderrDuringParse);
	}


}