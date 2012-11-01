package org.antlr.v4.test;

import org.junit.Test;

public class TestListeners extends BaseTest {
	@Test public void testBasic() throws Exception {
		String grammar =
			"grammar T;\n" +
			"@header {import org.antlr.v4.runtime.tree.*;}\n"+
			"@members {\n" +
			"public static class LeafListener extends TBaseListener {\n" +
			"    public void visitTerminal(TerminalNode<? extends Token> node) {\n" +
			"      System.out.println(node.getSymbol().getText());\n" +
			"    }\n" +
			"  }}\n" +
			"s\n" +
			"@after {" +
			"  System.out.println($r.ctx.toStringTree(this));" +
			"  ParseTreeWalker walker = new ParseTreeWalker();\n" +
			"  walker.walk(new LeafListener(), $r.ctx);" +
			"}\n" +
			"  : r=a ;\n" +
			"a : INT INT" +
			"  | ID" +
			"  ;\n" +
			"MULT: '*' ;\n" +
			"ADD : '+' ;\n" +
			"INT : [0-9]+ ;\n" +
			"ID  : [a-z]+ ;\n" +
			"WS : [ \\t\\n]+ -> skip ;\n";
		String result = execParser("T.g4", grammar, "TParser", "TLexer", "s", "1 2", false);
		String expecting = "(a 1 2)\n" +
						   "1\n" +
						   "2\n";
		assertEquals(expecting, result);
	}

	@Test public void testTokenGetters() throws Exception {
		String grammar =
			"grammar T;\n" +
			"@members {\n" +
			"public static class LeafListener extends TBaseListener {\n" +
			"    public void exitA(TParser.AContext ctx) {\n" +
			"      if (ctx.getChildCount()==2) System.out.printf(\"%s %s %s\",ctx.INT(0).getSymbol().getText(),ctx.INT(1).getSymbol().getText(),ctx.INT());\n" +
			"      else System.out.println(ctx.ID().getSymbol());\n" +
			"    }\n" +
			"  }}\n" +
			"s\n" +
			"@after {" +
			"  System.out.println($r.ctx.toStringTree(this));" +
			"  ParseTreeWalker walker = new ParseTreeWalker();\n" +
			"  walker.walk(new LeafListener(), $r.ctx);" +
			"}\n" +
			"  : r=a ;\n" +
			"a : INT INT" +
			"  | ID" +
			"  ;\n" +
			"MULT: '*' ;\n" +
			"ADD : '+' ;\n" +
			"INT : [0-9]+ ;\n" +
			"ID  : [a-z]+ ;\n" +
			"WS : [ \\t\\n]+ -> skip ;\n";
		String result = execParser("T.g4", grammar, "TParser", "TLexer", "s", "1 2", false);
		String expecting =
			"(a 1 2)\n" +
			"1 2 [1, 2]\n";
		assertEquals(expecting, result);

		result = execParser("T.g4", grammar, "TParser", "TLexer", "s", "abc", false);
		expecting = "(a abc)\n" +
					"[@0,0:2='abc',<4>,1:0]\n";
		assertEquals(expecting, result);
	}

	@Test public void testRuleGetters() throws Exception {
		String grammar =
			"grammar T;\n" +
			"@members {\n" +
			"public static class LeafListener extends TBaseListener {\n" +
			"    public void exitA(TParser.AContext ctx) {\n" +
			"      if (ctx.getChildCount()==2) {\n" +
			"        System.out.printf(\"%s %s %s\",ctx.b(0).start.getText(),\n" +
			"                          ctx.b(1).start.getText(),ctx.b().get(0).start.getText());\n" +
			"      }\n" +
			"      else System.out.println(ctx.b(0).start.getText());\n" +
			"    }\n" +
			"  }}\n" +
			"s\n" +
			"@after {" +
			"  System.out.println($r.ctx.toStringTree(this));" +
			"  ParseTreeWalker walker = new ParseTreeWalker();\n" +
			"  walker.walk(new LeafListener(), $r.ctx);" +
			"}\n" +
			"  : r=a ;\n" +
			"a : b b" +		// forces list
			"  | b" +		// a list still
			"  ;\n" +
			"b : ID | INT ;\n" +
			"MULT: '*' ;\n" +
			"ADD : '+' ;\n" +
			"INT : [0-9]+ ;\n" +
			"ID  : [a-z]+ ;\n" +
			"WS : [ \\t\\n]+ -> skip ;\n";
		String result = execParser("T.g4", grammar, "TParser", "TLexer", "s", "1 2", false);
		String expecting = "(a (b 1) (b 2))\n" +
						   "1 2 1\n";
		assertEquals(expecting, result);

		result = execParser("T.g4", grammar, "TParser", "TLexer", "s", "abc", false);
		expecting = "(a (b abc))\n" +
					"abc\n";
		assertEquals(expecting, result);
	}

	@Test public void testLR() throws Exception {
		String grammar =
			"grammar T;\n" +
			"@members {\n" +
			"public static class LeafListener extends TBaseListener {\n" +
			"    public void exitE(TParser.EContext ctx) {\n" +
			"      if (ctx.getChildCount()==3) {\n" +
			"        System.out.printf(\"%s %s %s\\n\",ctx.e(0).start.getText(),\n" +
			"                          ctx.e(1).start.getText()," +
			"                          ctx.e().get(0).start.getText());\n" +
			"      }\n" +
			"      else System.out.println(ctx.INT().getSymbol().getText());\n" +
			"    }\n" +
			"  }" +
			"}\n" +
			"s\n" +
			"@after {" +
			"  System.out.println($r.ctx.toStringTree(this));" +
			"  ParseTreeWalker walker = new ParseTreeWalker();\n" +
			"  walker.walk(new LeafListener(), $r.ctx);" +
			"}\n" +
			"  : r=e ;\n" +
			"e : e op='*' e\n" +
			"  | e op='+' e\n" +
			"  | INT\n" +
			"  ;\n" +
			"MULT: '*' ;\n" +
			"ADD : '+' ;\n" +
			"INT : [0-9]+ ;\n" +
			"WS : [ \\t\\n]+ -> skip ;\n";
		String result = execParser("T.g4", grammar, "TParser", "TLexer", "s", "1+2*3", false);
		String expecting =
			"(e (e 1) + (e (e 2) * (e 3)))\n" +
			"1\n" +
			"2\n" +
			"3\n" +
			"2 3 2\n" +
			"1 2 1\n";
		assertEquals(expecting, result);
	}

	@Test public void testLRWithLabels() throws Exception {
		String grammar =
			"grammar T;\n" +
			"@members {\n" +
			"  public static class LeafListener extends TBaseListener {\n" +
			"    public void exitCall(TParser.CallContext ctx) {\n" +
			"      System.out.printf(\"%s %s\",ctx.e().start.getText(),\n" +
			"                ctx.eList());\n" +
			"    }\n" +
			"    public void exitInt(TParser.IntContext ctx) {\n" +
			"      System.out.println(ctx.INT().getSymbol().getText());\n" +
			"    }\n" +
			"  }\n" +
			"}\n" +
			"s\n" +
			"@after {" +
			"  System.out.println($r.ctx.toStringTree(this));" +
			"  ParseTreeWalker walker = new ParseTreeWalker();\n" +
			"  walker.walk(new LeafListener(), $r.ctx);" +
			"}\n" +
			"  : r=e ;\n" +
			"e : e '(' eList ')' # Call\n" +
			"  | INT             # Int\n" +
			"  ;     \n" +
			"eList : e (',' e)* ;\n" +
			"MULT: '*' ;\n" +
			"ADD : '+' ;\n" +
			"INT : [0-9]+ ;\n" +
			"WS : [ \\t\\n]+ -> skip ;\n";
		String result = execParser("T.g4", grammar, "TParser", "TLexer", "s", "1(2,3)", false);
		String expecting =
			"(e (e 1) ( (eList (e 2) , (e 3)) ))\n" +
			"1\n" +
			"2\n" +
			"3\n" +
			"1 [13 6]\n";
		assertEquals(expecting, result);
	}
}
