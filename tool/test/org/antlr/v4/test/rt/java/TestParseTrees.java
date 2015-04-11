package org.antlr.v4.test.rt.java;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;


import org.antlr.v4.test.AntlrTestcase;

public class TestParseTrees extends AntlrTestcase {

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testTokenAndRuleContextString() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s\n" +
	                  "@init {\n" +
	                  "setBuildParseTree(true);\n" +
	                  "}\n" +
	                  "@after {\n" +
	                  "System.out.println($r.ctx.toStringTree(this));\n" +
	                  "}\n" +
	                  "  : r=a ;\n" +
	                  "a : 'x' { \n" +
	                  "System.out.println(getRuleInvocationStack());\n" +
	                  "} ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", "x", false);
		assertEquals("[a, s]\n(a x)\n", found);
		assertThat(stderrDuringParse(), isEmptyOrNullString());
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testToken2() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s\n" +
	                  "@init {\n" +
	                  "setBuildParseTree(true);\n" +
	                  "}\n" +
	                  "@after {\n" +
	                  "System.out.println($r.ctx.toStringTree(this));\n" +
	                  "}\n" +
	                  "  : r=a ;\n" +
	                  "a : 'x' 'y'\n" +
	                  "  ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", "xy", false);
		assertEquals("(a x y)\n", found);
		assertThat(stderrDuringParse(), isEmptyOrNullString());
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void test2Alts() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s\n" +
	                  "@init {\n" +
	                  "setBuildParseTree(true);\n" +
	                  "}\n" +
	                  "@after {\n" +
	                  "System.out.println($r.ctx.toStringTree(this));\n" +
	                  "}\n" +
	                  "  : r=a ;\n" +
	                  "a : 'x' | 'y'\n" +
	                  "  ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", "y", false);
		assertEquals("(a y)\n", found);
		assertThat(stderrDuringParse(), isEmptyOrNullString());
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void test2AltLoop() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s\n" +
	                  "@init {\n" +
	                  "setBuildParseTree(true);\n" +
	                  "}\n" +
	                  "@after {\n" +
	                  "System.out.println($r.ctx.toStringTree(this));\n" +
	                  "}\n" +
	                  "  : r=a ;\n" +
	                  "a : ('x' | 'y')* 'z'\n" +
	                  "  ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", "xyyxyxz", false);
		assertEquals("(a x y y x y x z)\n", found);
		assertThat(stderrDuringParse(), isEmptyOrNullString());
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testRuleRef() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s\n" +
	                  "@init {\n" +
	                  "setBuildParseTree(true);\n" +
	                  "}\n" +
	                  "@after {\n" +
	                  "System.out.println($r.ctx.toStringTree(this));\n" +
	                  "}\n" +
	                  "  : r=a ;\n" +
	                  "a : b 'x'\n" +
	                  "  ;\n" +
	                  "b : 'y' \n" +
	                  "  ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", "yx", false);
		assertEquals("(a (b y) x)\n", found);
		assertThat(stderrDuringParse(), isEmptyOrNullString());
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testExtraToken() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s\n" +
	                  "@init {\n" +
	                  "setBuildParseTree(true);\n" +
	                  "}\n" +
	                  "@after {\n" +
	                  "System.out.println($r.ctx.toStringTree(this));\n" +
	                  "}\n" +
	                  "  : r=a ;\n" +
	                  "a : 'x' 'y'\n" +
	                  "  ;\n" +
	                  "Z : 'z' \n" +
	                  "  ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", "xzy", false);
		assertEquals("(a x z y)\n", found);
		assertEquals("line 1:1 extraneous input 'z' expecting 'y'\n", stderrDuringParse());
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testNoViableAlt() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s\n" +
	                  "@init {\n" +
	                  "setBuildParseTree(true);\n" +
	                  "}\n" +
	                  "@after {\n" +
	                  "System.out.println($r.ctx.toStringTree(this));\n" +
	                  "}\n" +
	                  "  : r=a ;\n" +
	                  "a : 'x' | 'y'\n" +
	                  "  ;\n" +
	                  "Z : 'z' \n" +
	                  "  ;\n" +
	                  " ";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", "z", false);
		assertEquals("(a z)\n", found);
		assertEquals("line 1:0 mismatched input 'z' expecting {'x', 'y'}\n", stderrDuringParse());
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testSync() throws Exception {
		String grammar = "grammar T;\n" +
	                  "s\n" +
	                  "@init {\n" +
	                  "setBuildParseTree(true);\n" +
	                  "}\n" +
	                  "@after {\n" +
	                  "System.out.println($r.ctx.toStringTree(this));\n" +
	                  "}\n" +
	                  "  : r=a ;\n" +
	                  "a : 'x' 'y'* '!'\n" +
	                  "  ;\n" +
	                  "Z : 'z' \n" +
	                  "  ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", "xzyy!", false);
		assertEquals("(a x z y y !)\n", found);
		assertEquals("line 1:1 extraneous input 'z' expecting {'y', '!'}\n", stderrDuringParse());
	}


}