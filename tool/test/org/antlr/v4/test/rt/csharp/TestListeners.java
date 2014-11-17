package org.antlr.v4.test.rt.csharp;

import org.junit.Test;

public class TestListeners extends BaseTest {

	@Test
	public void testBasic() throws Exception {
		String grammar = "grammar T;\n" +
	                  "@parser::header {\n" +
	                  "}\n" +
	                  "\n" +
	                  "@parser::members {\n" +
	                  "public class LeafListener : TBaseListener {\n" +
	                  "	public override void VisitTerminal(ITerminalNode node) {\n" +
	                  "		Console.WriteLine(node.Symbol.Text);\n" +
	                  "	}\n" +
	                  "}\n" +
	                  "}\n" +
	                  "\n" +
	                  "s\n" +
	                  "@after {\n" +
	                  "Console.WriteLine($ctx.r.ToStringTree(this));\n" +
	                  "ParseTreeWalker walker = new ParseTreeWalker();\n" +
	                  "walker.Walk(new LeafListener(), $ctx.r);\n" +
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

	String testTokenGetters(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "@parser::header {\n" +
	                  "}\n" +
	                  "\n" +
	                  "@parser::members {\n" +
	                  "public class LeafListener : TBaseListener {\n" +
	                  "	public override void ExitA(TParser.AContext ctx) {\n" +
	                  "		if (ctx.ChildCount==2) \n" +
	                  "		{\n" +
	                  "			StringBuilder sb = new StringBuilder (\"[\");\n" +
	                  "			foreach (ITerminalNode node in ctx.INT ()) {\n" +
	                  "				sb.Append (node.ToString ());\n" +
	                  "				sb.Append (\", \");\n" +
	                  "			}\n" +
	                  "			sb.Length = sb.Length - 2;\n" +
	                  "			sb.Append (\"]\");\n" +
	                  "			Console.Write (\"{0} {1} {2}\", ctx.INT (0).Symbol.Text,\n" +
	                  "				ctx.INT (1).Symbol.Text, sb.ToString());\n" +
	                  "		}\n" +
	                  "		else\n" +
	                  "			Console.WriteLine(ctx.ID().Symbol);\n" +
	                  "	}\n" +
	                  "}\n" +
	                  "}\n" +
	                  "\n" +
	                  "s\n" +
	                  "@after {\n" +
	                  "Console.WriteLine($ctx.r.ToStringTree(this));\n" +
	                  "ParseTreeWalker walker = new ParseTreeWalker();\n" +
	                  "walker.Walk(new LeafListener(), $ctx.r);\n" +
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

	@Test
	public void testTokenGetters_1() throws Exception {
		String found = testTokenGetters("1 2");
		assertEquals("(a 1 2)\n1 2 [1, 2]\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testTokenGetters_2() throws Exception {
		String found = testTokenGetters("abc");
		assertEquals("(a abc)\n[@0,0:2='abc',<4>,1:0]\n", found);
		assertNull(this.stderrDuringParse);
	}

	String testRuleGetters(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "@parser::header {\n" +
	                  "}\n" +
	                  "\n" +
	                  "@parser::members {\n" +
	                  "public class LeafListener : TBaseListener {\n" +
	                  "	public override void ExitA(TParser.AContext ctx) {\n" +
	                  "		if (ctx.ChildCount==2) {\n" +
	                  "			Console.Write(\"{0} {1} {2}\",ctx.b(0).Start.Text,\n" +
	                  "				ctx.b(1).Start.Text,ctx.b()[0].Start.Text);\n" +
	                  "		} else \n" +
	                  "			Console.WriteLine(ctx.b(0).Start.Text);\n" +
	                  "	}\n" +
	                  "}\n" +
	                  "}\n" +
	                  "\n" +
	                  "s\n" +
	                  "@after {\n" +
	                  "Console.WriteLine($ctx.r.ToStringTree(this));\n" +
	                  "ParseTreeWalker walker = new ParseTreeWalker();\n" +
	                  "walker.Walk(new LeafListener(), $ctx.r);\n" +
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

	@Test
	public void testRuleGetters_1() throws Exception {
		String found = testRuleGetters("1 2");
		assertEquals("(a (b 1) (b 2))\n1 2 1\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testRuleGetters_2() throws Exception {
		String found = testRuleGetters("abc");
		assertEquals("(a (b abc))\nabc\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testLR() throws Exception {
		String grammar = "grammar T;\n" +
	                  "@parser::header {\n" +
	                  "}\n" +
	                  "\n" +
	                  "@parser::members {\n" +
	                  "public class LeafListener : TBaseListener {\n" +
	                  "	public override void ExitE(TParser.EContext ctx) {\n" +
	                  "		if (ctx.ChildCount==3) {\n" +
	                  "			Console.Write(\"{0} {1} {2}\\n\",ctx.e(0).Start.Text,\n" +
	                  "				ctx.e(1).Start.Text, ctx.e()[0].Start.Text);\n" +
	                  "		} else \n" +
	                  "			Console.WriteLine(ctx.INT().Symbol.Text);\n" +
	                  "	}\n" +
	                  "}\n" +
	                  "}\n" +
	                  "\n" +
	                  "s\n" +
	                  "@after {\n" +
	                  "Console.WriteLine($ctx.r.ToStringTree(this));\n" +
	                  "ParseTreeWalker walker = new ParseTreeWalker();\n" +
	                  "walker.Walk(new LeafListener(), $ctx.r);\n" +
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

	@Test
	public void testLRWithLabels() throws Exception {
		String grammar = "grammar T;\n" +
	                  "@parser::header {\n" +
	                  "}\n" +
	                  "\n" +
	                  "@parser::members {\n" +
	                  "public class LeafListener : TBaseListener {\n" +
	                  "	public override void ExitCall(TParser.CallContext ctx) {\n" +
	                  "		Console.Write(\"{0} {1}\",ctx.e().Start.Text,ctx.eList());\n" +
	                  "	}\n" +
	                  "	public override void ExitInt(TParser.IntContext ctx) {\n" +
	                  "		Console.WriteLine(ctx.INT().Symbol.Text);\n" +
	                  "	}\n" +
	                  "}\n" +
	                  "}\n" +
	                  "\n" +
	                  "s\n" +
	                  "@after {\n" +
	                  "Console.WriteLine($ctx.r.ToStringTree(this));\n" +
	                  "ParseTreeWalker walker = new ParseTreeWalker();\n" +
	                  "walker.Walk(new LeafListener(), $ctx.r);\n" +
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