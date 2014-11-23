package org.antlr.v4.test.rt.js.explorer;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestParseTrees extends BaseTest {

	@Test
	public void testTokenAndRuleContextString() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s\r\n" +
	                  "@init {\r\n" +
	                  "this.buildParseTrees = true;\r\n" +
	                  "}\r\n" +
	                  "@after {\r\n" +
	                  "document.getElementById('output').value += $r.ctx.toStringTree(null, this) + '\\n';\r\n" +
	                  "}\r\n" +
	                  "  : r=a ;\r\n" +
	                  "a : 'x' { \r\n" +
	                  "document.getElementById('output').value += antlr4.Utils.arrayToString(this.getRuleInvocationStack()) + '\\n';\r\n" +
	                  "} ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "x", false);
		assertEquals("[a, s]\n(a x)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testToken2() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s\r\n" +
	                  "@init {\r\n" +
	                  "this.buildParseTrees = true;\r\n" +
	                  "}\r\n" +
	                  "@after {\r\n" +
	                  "document.getElementById('output').value += $r.ctx.toStringTree(null, this) + '\\n';\r\n" +
	                  "}\r\n" +
	                  "  : r=a ;\r\n" +
	                  "a : 'x' 'y'\r\n" +
	                  "  ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "xy", false);
		assertEquals("(a x y)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void test2Alts() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s\r\n" +
	                  "@init {\r\n" +
	                  "this.buildParseTrees = true;\r\n" +
	                  "}\r\n" +
	                  "@after {\r\n" +
	                  "document.getElementById('output').value += $r.ctx.toStringTree(null, this) + '\\n';\r\n" +
	                  "}\r\n" +
	                  "  : r=a ;\r\n" +
	                  "a : 'x' | 'y'\r\n" +
	                  "  ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "y", false);
		assertEquals("(a y)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void test2AltLoop() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s\r\n" +
	                  "@init {\r\n" +
	                  "this.buildParseTrees = true;\r\n" +
	                  "}\r\n" +
	                  "@after {\r\n" +
	                  "document.getElementById('output').value += $r.ctx.toStringTree(null, this) + '\\n';\r\n" +
	                  "}\r\n" +
	                  "  : r=a ;\r\n" +
	                  "a : ('x' | 'y')* 'z'\r\n" +
	                  "  ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "xyyxyxz", false);
		assertEquals("(a x y y x y x z)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testRuleRef() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s\r\n" +
	                  "@init {\r\n" +
	                  "this.buildParseTrees = true;\r\n" +
	                  "}\r\n" +
	                  "@after {\r\n" +
	                  "document.getElementById('output').value += $r.ctx.toStringTree(null, this) + '\\n';\r\n" +
	                  "}\r\n" +
	                  "  : r=a ;\r\n" +
	                  "a : b 'x'\r\n" +
	                  "  ;\r\n" +
	                  "b : 'y' \r\n" +
	                  "  ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "yx", false);
		assertEquals("(a (b y) x)\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testExtraToken() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s\r\n" +
	                  "@init {\r\n" +
	                  "this.buildParseTrees = true;\r\n" +
	                  "}\r\n" +
	                  "@after {\r\n" +
	                  "document.getElementById('output').value += $r.ctx.toStringTree(null, this) + '\\n';\r\n" +
	                  "}\r\n" +
	                  "  : r=a ;\r\n" +
	                  "a : 'x' 'y'\r\n" +
	                  "  ;\r\n" +
	                  "Z : 'z' \r\n" +
	                  "  ;\r\n" +
	                  "\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "xzy", false);
		assertEquals("(a x z y)\n", found);
		assertEquals("line 1:1 extraneous input 'z' expecting 'y'\n", this.stderrDuringParse);
	}

	@Test
	public void testNoViableAlt() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s\r\n" +
	                  "@init {\r\n" +
	                  "this.buildParseTrees = true;\r\n" +
	                  "}\r\n" +
	                  "@after {\r\n" +
	                  "document.getElementById('output').value += $r.ctx.toStringTree(null, this) + '\\n';\r\n" +
	                  "}\r\n" +
	                  "  : r=a ;\r\n" +
	                  "a : 'x' | 'y'\r\n" +
	                  "  ;\r\n" +
	                  "Z : 'z' \r\n" +
	                  "  ;\r\n" +
	                  " ";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "z", false);
		assertEquals("(a z)\n", found);
		assertEquals("line 1:0 mismatched input 'z' expecting {'x', 'y'}\n", this.stderrDuringParse);
	}

	@Test
	public void testSync() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "s\r\n" +
	                  "@init {\r\n" +
	                  "this.buildParseTrees = true;\r\n" +
	                  "}\r\n" +
	                  "@after {\r\n" +
	                  "document.getElementById('output').value += $r.ctx.toStringTree(null, this) + '\\n';\r\n" +
	                  "}\r\n" +
	                  "  : r=a ;\r\n" +
	                  "a : 'x' 'y'* '!'\r\n" +
	                  "  ;\r\n" +
	                  "Z : 'z' \r\n" +
	                  "  ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "xzyy!", false);
		assertEquals("(a x z y y !)\n", found);
		assertEquals("line 1:1 extraneous input 'z' expecting {'y', '!'}\n", this.stderrDuringParse);
	}


}