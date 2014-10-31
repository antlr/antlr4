package org.antlr.v4.test.rt.py3;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestListeners extends BasePython3Test {

	@Test
	public void testBasic() throws Exception {
		String grammar = "grammar T;\n" +
	                  "@parser::header {\n" +
	                  "}\n" +
	                  "\n" +
	                  "@parser::members {\n" +
	                  "class LeafListener(TListener):\n" +
	                  "    def visitTerminal(self, node):\n" +
	                  "        print(node.symbol.text)\n" +
	                  "\n" +
	                  "}\n" +
	                  "\n" +
	                  "s\n" +
	                  "@after {\n" +
	                  "print($ctx.r.toStringTree(recog=self))\n" +
	                  "walker = ParseTreeWalker()\n" +
	                  "walker.walk(TParser.LeafListener(), $ctx.r)\n" +
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
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "1 2", false);
		assertEquals("(a 1 2)\n1\n2\n", found);
		assertNull(this.stderrDuringParse);
	}

	String testTokenGetters(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "@parser::header {\n" +
	                  "}\n" +
	                  "\n" +
	                  "@parser::members {\n" +
	                  "class LeafListener(TListener):\n" +
	                  "    def exitA(self, ctx):\n" +
	                  "        if ctx.getChildCount()==2:\n" +
	                  "            print(ctx.INT(0).symbol.text + ' ' + ctx.INT(1).symbol.text + ' ' + str_list(ctx.INT()))\n" +
	                  "        else:\n" +
	                  "            print(str(ctx.ID().symbol))\n" +
	                  "\n" +
	                  "}\n" +
	                  "\n" +
	                  "s\n" +
	                  "@after {\n" +
	                  "print($ctx.r.toStringTree(recog=self))\n" +
	                  "walker = ParseTreeWalker()\n" +
	                  "walker.walk(TParser.LeafListener(), $ctx.r)\n" +
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
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", input, false);
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
	                  "class LeafListener(TListener):\n" +
	                  "    def exitA(self, ctx):\n" +
	                  "        if ctx.getChildCount()==2:\n" +
	                  "            print(ctx.b(0).start.text + ' ' + ctx.b(1).start.text + ' ' + ctx.b()[0].start.text)\n" +
	                  "        else:\n" +
	                  "            print(ctx.b(0).start.text)\n" +
	                  "\n" +
	                  "}\n" +
	                  "\n" +
	                  "s\n" +
	                  "@after {\n" +
	                  "print($ctx.r.toStringTree(recog=self))\n" +
	                  "walker = ParseTreeWalker()\n" +
	                  "walker.walk(TParser.LeafListener(), $ctx.r)\n" +
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
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", input, false);
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
	                  "class LeafListener(TListener):\n" +
	                  "    def exitE(self, ctx):\n" +
	                  "        if ctx.getChildCount()==3:\n" +
	                  "            print(ctx.e(0).start.text + ' ' + ctx.e(1).start.text + ' ' + ctx.e()[0].start.text)\n" +
	                  "        else:\n" +
	                  "            print(ctx.INT().symbol.text)\n" +
	                  "\n" +
	                  "}\n" +
	                  "\n" +
	                  "s\n" +
	                  "@after {\n" +
	                  "print($ctx.r.toStringTree(recog=self))\n" +
	                  "walker = ParseTreeWalker()\n" +
	                  "walker.walk(TParser.LeafListener(), $ctx.r)\n" +
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
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "1+2*3", false);
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
	                  "class LeafListener(TListener):\n" +
	                  "    def exitCall(self, ctx):\n" +
	                  "        print(ctx.e().start.text + ' ' + str(ctx.eList()))\n" +
	                  "    def exitInt(self, ctx):\n" +
	                  "        print(ctx.INT().symbol.text)\n" +
	                  "\n" +
	                  "}\n" +
	                  "\n" +
	                  "s\n" +
	                  "@after {\n" +
	                  "print($ctx.r.toStringTree(recog=self))\n" +
	                  "walker = ParseTreeWalker()\n" +
	                  "walker.walk(TParser.LeafListener(), $ctx.r)\n" +
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
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "1(2,3)", false);
		assertEquals("(e (e 1) ( (eList (e 2) , (e 3)) ))\n1\n2\n3\n1 [13 6]\n", found);
		assertNull(this.stderrDuringParse);
	}


}