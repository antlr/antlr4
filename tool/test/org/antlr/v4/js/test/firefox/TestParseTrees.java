package org.antlr.v4.js.test.firefox;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestParseTrees extends BaseTest {

	@Test
	public void testTokenAndRuleContextString() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s\n" +
	                  "@init {\n" +
	                  "this.buildParseTrees = true;\n" +
	                  "}\n" +
	                  "@after {\n" +
	                  "document.getElementById('output').value += $r.ctx.toStringTree(null, this); + '\\n';\n" +
	                  "}\n" +
	                  "  : r=a ;\n" +
	                  "a : 'x' { \n" +
	                  "document.getElementById('output').value += antlr4.Utils.arrayToString(this.getRuleInvocationStack()) + '\\n';\n" +
	                  "} ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "x");
		assertEquals("[a, s]\n(a x)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testToken2() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s\n" +
	                  "@init {\n" +
	                  "this.buildParseTrees = true;\n" +
	                  "}\n" +
	                  "@after {\n" +
	                  "document.getElementById('output').value += $r.ctx.toStringTree(null, this); + '\\n';\n" +
	                  "}\n" +
	                  "  : r=a ;\n" +
	                  "a : 'x' 'y'\n" +
	                  "  ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "xy");
		assertEquals("(a x y)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testtest2Alts() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s\n" +
	                  "@init {\n" +
	                  "this.buildParseTrees = true;\n" +
	                  "}\n" +
	                  "@after {\n" +
	                  "document.getElementById('output').value += $r.ctx.toStringTree(null, this); + '\\n';\n" +
	                  "}\n" +
	                  "  : r=a ;\n" +
	                  "a : 'x' | 'y'\n" +
	                  "  ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "y");
		assertEquals("(a y)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void test2AltLoop() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s\n" +
	                  "@init {\n" +
	                  "this.buildParseTrees = true;\n" +
	                  "}\n" +
	                  "@after {\n" +
	                  "document.getElementById('output').value += $r.ctx.toStringTree(null, this); + '\\n';\n" +
	                  "}\n" +
	                  "  : r=a ;\n" +
	                  "a : ('x' | 'y')* 'z'\n" +
	                  "  ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "xyyxyxz");
		assertEquals("(a x y y x y x z)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testRuleRef() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s\n" +
	                  "@init {\n" +
	                  "this.buildParseTrees = true;\n" +
	                  "}\n" +
	                  "@after {\n" +
	                  "document.getElementById('output').value += $r.ctx.toStringTree(null, this); + '\\n';\n" +
	                  "}\n" +
	                  "  : r=a ;\n" +
	                  "a : b 'x'\n" +
	                  "  ;\n" +
	                  "b : 'y' \n" +
	                  "  ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "yx");
		assertEquals("(a (b y) x)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testExtraToken() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s\n" +
	                  "@init {\n" +
	                  "this.buildParseTrees = true;\n" +
	                  "}\n" +
	                  "@after {\n" +
	                  "document.getElementById('output').value += $r.ctx.toStringTree(null, this); + '\\n';\n" +
	                  "}\n" +
	                  "  : r=a ;\n" +
	                  "a : 'x' 'y'\n" +
	                  "  ;\n" +
	                  "Z : 'z' \n" +
	                  "  ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "xzy");
		assertEquals("(a x z y)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testNoViableAlt() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s\n" +
	                  "@init {\n" +
	                  "this.buildParseTrees = true;\n" +
	                  "}\n" +
	                  "@after {\n" +
	                  "document.getElementById('output').value += $r.ctx.toStringTree(null, this); + '\\n';\n" +
	                  "}\n" +
	                  "  : r=a ;\n" +
	                  "a : 'x' | 'y'\n" +
	                  "  ;\n" +
	                  "Z : 'z' \n" +
	                  "  ;\n" +
	                  " ";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "z");
		assertEquals("(a z)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testSync() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s\n" +
	                  "@init {\n" +
	                  "this.buildParseTrees = true;\n" +
	                  "}\n" +
	                  "@after {\n" +
	                  "document.getElementById('output').value += $r.ctx.toStringTree(null, this); + '\\n';\n" +
	                  "}\n" +
	                  "  : r=a ;\n" +
	                  "a : 'x' 'y'* '!'\n" +
	                  "  ;\n" +
	                  "Z : 'z' \n" +
	                  "  ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "xzyy!");
		assertEquals("(a x z y y !)\n", found);
		assertNull(this.stderrDuringParse);
	}


}