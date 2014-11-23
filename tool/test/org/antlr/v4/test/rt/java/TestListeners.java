package org.antlr.v4.test.rt.java;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestListeners extends BaseTest {

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testBasic() throws Exception {
		String grammar = "grammar T;\n" +
	                  "@parser::header {\n" +
	                  "}\n" +
	                  "\n" +
	                  "@parser::members {\n" +
	                  "public static class LeafListener extends TBaseListener {\n" +
	                  "	public void visitTerminal(TerminalNode node) {\n" +
	                  "		System.out.println(node.getSymbol().getText());\n" +
	                  "	}\n" +
	                  "}\n" +
	                  "}\n" +
	                  "\n" +
	                  "s\n" +
	                  "@after {\n" +
	                  "System.out.println($ctx.r.toStringTree(this));\n" +
	                  "ParseTreeWalker walker = new ParseTreeWalker();\n" +
	                  "walker.walk(new LeafListener(), $ctx.r);\n" +
	                  "}\n" +
	                  "  : r=a ;\n" +
	                  "a : INT INT\n" +
	                  "  | ID\n" +
	                  "  ;\n" +
	                  "MULT: '*' ;\n" +
	                  "ADD : '+' ;\n" +
	                  "INT : [0-9]+ ;\n" +
	                  "ID  : [a-z]+ ;\n" +
	                  "WS : [ \\t\\n]+ -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", "1 2", false);
		assertEquals("(a 1 2)\n1\n2\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	String testTokenGetters(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "@parser::header {\n" +
	                  "}\n" +
	                  "\n" +
	                  "@parser::members {\n" +
	                  "public static class LeafListener extends TBaseListener {\n" +
	                  "	public void exitA(TParser.AContext ctx) {\n" +
	                  "		if (ctx.getChildCount()==2) \n" +
	                  "			System.out.printf(\"%s %s %s\",ctx.INT(0).getSymbol().getText(),\n" +
	                  "				ctx.INT(1).getSymbol().getText(),ctx.INT());\n" +
	                  "		else\n" +
	                  "			System.out.println(ctx.ID().getSymbol());\n" +
	                  "	}\n" +
	                  "}\n" +
	                  "}\n" +
	                  "\n" +
	                  "s\n" +
	                  "@after {\n" +
	                  "System.out.println($ctx.r.toStringTree(this));\n" +
	                  "ParseTreeWalker walker = new ParseTreeWalker();\n" +
	                  "walker.walk(new LeafListener(), $ctx.r);\n" +
	                  "}\n" +
	                  "  : r=a ;\n" +
	                  "a : INT INT\n" +
	                  "  | ID\n" +
	                  "  ;\n" +
	                  "MULT: '*' ;\n" +
	                  "ADD : '+' ;\n" +
	                  "INT : [0-9]+ ;\n" +
	                  "ID  : [a-z]+ ;\n" +
	                  "WS : [ \\t\\n]+ -> skip ;";
		return execParser("T.g4", grammar, "TParser", "TLexer", "s", input, false);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testTokenGetters_1() throws Exception {
		String found = testTokenGetters("1 2");
		assertEquals("(a 1 2)\n1 2 [1, 2]\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testTokenGetters_2() throws Exception {
		String found = testTokenGetters("abc");
		assertEquals("(a abc)\n[@0,0:2='abc',<4>,1:0]\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	String testRuleGetters(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "@parser::header {\n" +
	                  "}\n" +
	                  "\n" +
	                  "@parser::members {\n" +
	                  "public static class LeafListener extends TBaseListener {\n" +
	                  "	public void exitA(TParser.AContext ctx) {\n" +
	                  "		if (ctx.getChildCount()==2) {\n" +
	                  "			System.out.printf(\"%s %s %s\",ctx.b(0).start.getText(),\n" +
	                  "				ctx.b(1).start.getText(),ctx.b().get(0).start.getText());\n" +
	                  "		} else \n" +
	                  "			System.out.println(ctx.b(0).start.getText());\n" +
	                  "	}\n" +
	                  "}\n" +
	                  "}\n" +
	                  "\n" +
	                  "s\n" +
	                  "@after {\n" +
	                  "System.out.println($ctx.r.toStringTree(this));\n" +
	                  "ParseTreeWalker walker = new ParseTreeWalker();\n" +
	                  "walker.walk(new LeafListener(), $ctx.r);\n" +
	                  "}\n" +
	                  "  : r=a ;\n" +
	                  "a : b b		// forces list\n" +
	                  "  | b		// a list still\n" +
	                  "  ;\n" +
	                  "b : ID | INT;\n" +
	                  "MULT: '*' ;\n" +
	                  "ADD : '+' ;\n" +
	                  "INT : [0-9]+ ;\n" +
	                  "ID  : [a-z]+ ;\n" +
	                  "WS : [ \\t\\n]+ -> skip ;";
		return execParser("T.g4", grammar, "TParser", "TLexer", "s", input, false);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testRuleGetters_1() throws Exception {
		String found = testRuleGetters("1 2");
		assertEquals("(a (b 1) (b 2))\n1 2 1\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testRuleGetters_2() throws Exception {
		String found = testRuleGetters("abc");
		assertEquals("(a (b abc))\nabc\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testLR() throws Exception {
		String grammar = "grammar T;\n" +
	                  "@parser::header {\n" +
	                  "}\n" +
	                  "\n" +
	                  "@parser::members {\n" +
	                  "public static class LeafListener extends TBaseListener {\n" +
	                  "	public void exitE(TParser.EContext ctx) {\n" +
	                  "		if (ctx.getChildCount()==3) {\n" +
	                  "			System.out.printf(\"%s %s %s\\n\",ctx.e(0).start.getText(),\n" +
	                  "				ctx.e(1).start.getText(), ctx.e().get(0).start.getText());\n" +
	                  "		} else \n" +
	                  "			System.out.println(ctx.INT().getSymbol().getText());\n" +
	                  "	}\n" +
	                  "}\n" +
	                  "}\n" +
	                  "\n" +
	                  "s\n" +
	                  "@after {\n" +
	                  "System.out.println($ctx.r.toStringTree(this));\n" +
	                  "ParseTreeWalker walker = new ParseTreeWalker();\n" +
	                  "walker.walk(new LeafListener(), $ctx.r);\n" +
	                  "}\n" +
	                  "	: r=e ;\n" +
	                  "e : e op='*' e\n" +
	                  "	| e op='+' e\n" +
	                  "	| INT\n" +
	                  "	;\n" +
	                  "MULT: '*' ;\n" +
	                  "ADD : '+' ;\n" +
	                  "INT : [0-9]+ ;\n" +
	                  "ID  : [a-z]+ ;\n" +
	                  "WS : [ \\t\\n]+ -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", "1+2*3", false);
		assertEquals("(e (e 1) + (e (e 2) * (e 3)))\n1\n2\n3\n2 3 2\n1 2 1\n", found);
		assertNull(this.stderrDuringParse);
	}

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testLRWithLabels() throws Exception {
		String grammar = "grammar T;\n" +
	                  "@parser::header {\n" +
	                  "}\n" +
	                  "\n" +
	                  "@parser::members {\n" +
	                  "public static class LeafListener extends TBaseListener {\n" +
	                  "	public void exitCall(TParser.CallContext ctx) {\n" +
	                  "		System.out.printf(\"%s %s\",ctx.e().start.getText(),ctx.eList());\n" +
	                  "	}\n" +
	                  "	public void exitInt(TParser.IntContext ctx) {\n" +
	                  "		System.out.println(ctx.INT().getSymbol().getText());\n" +
	                  "	}\n" +
	                  "}\n" +
	                  "}\n" +
	                  "\n" +
	                  "s\n" +
	                  "@after {\n" +
	                  "System.out.println($ctx.r.toStringTree(this));\n" +
	                  "ParseTreeWalker walker = new ParseTreeWalker();\n" +
	                  "walker.walk(new LeafListener(), $ctx.r);\n" +
	                  "}\n" +
	                  "  : r=e ;\n" +
	                  "e : e '(' eList ')' # Call\n" +
	                  "  | INT             # Int\n" +
	                  "  ;\n" +
	                  "eList : e (',' e)* ;\n" +
	                  "MULT: '*' ;\n" +
	                  "ADD : '+' ;\n" +
	                  "INT : [0-9]+ ;\n" +
	                  "ID  : [a-z]+ ;\n" +
	                  "WS : [ \\t\\n]+ -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "s", "1(2,3)", false);
		assertEquals("(e (e 1) ( (eList (e 2) , (e 3)) ))\n1\n2\n3\n1 [13 6]\n", found);
		assertNull(this.stderrDuringParse);
	}


}