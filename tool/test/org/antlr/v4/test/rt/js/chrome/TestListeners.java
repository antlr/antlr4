package org.antlr.v4.test.rt.js.chrome;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestListeners extends BaseTest {

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testBasic() throws Exception {
		String grammar = "grammar T;\n" +
	                  "@parser::header {\n" +
	                  "var TListener = require('./TListener').TListener;\n" +
	                  "}\n" +
	                  "\n" +
	                  "@parser::members {\n" +
	                  "this.LeafListener = function() {\n" +
	                  "    this.visitTerminal = function(node) {\n" +
	                  "    	document.getElementById('output').value += node.symbol.text + '\\n';\n" +
	                  "    };\n" +
	                  "    return this;\n" +
	                  "};\n" +
	                  "this.LeafListener.prototype = Object.create(TListener.prototype);\n" +
	                  "this.LeafListener.prototype.constructor = this.LeafListener;\n" +
	                  "\n" +
	                  "}\n" +
	                  "\n" +
	                  "s\n" +
	                  "@after {\n" +
	                  "document.getElementById('output').value += $ctx.r.toStringTree(null, this) + '\\n';\n" +
	                  "var walker = new antlr4.tree.ParseTreeWalker();\n" +
	                  "walker.walk(new this.LeafListener(), $ctx.r);\n" +
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

	/* this file and method are generated, any edit will be overwritten by the next generation */
	String testTokenGetters(String input) throws Exception {
		String grammar = "grammar T;\n" +
	                  "@parser::header {\n" +
	                  "var TListener = require('./TListener').TListener;\n" +
	                  "}\n" +
	                  "\n" +
	                  "@parser::members {\n" +
	                  "this.LeafListener = function() {\n" +
	                  "    this.exitA = function(ctx) {\n" +
	                  "    	var str;\n" +
	                  "        if(ctx.getChildCount()===2) {\n" +
	                  "            str = ctx.INT(0).symbol.text + ' ' + ctx.INT(1).symbol.text + ' ' + antlr4.Utils.arrayToString(ctx.INT());\n" +
	                  "        } else {\n" +
	                  "            str = ctx.ID().symbol.toString();\n" +
	                  "        }\n" +
	                  "    	document.getElementById('output').value += str + '\\n';\n" +
	                  "    };\n" +
	                  "    return this;\n" +
	                  "};\n" +
	                  "this.LeafListener.prototype = Object.create(TListener.prototype);\n" +
	                  "this.LeafListener.prototype.constructor = this.LeafListener;\n" +
	                  "\n" +
	                  "}\n" +
	                  "\n" +
	                  "s\n" +
	                  "@after {\n" +
	                  "document.getElementById('output').value += $ctx.r.toStringTree(null, this) + '\\n';\n" +
	                  "var walker = new antlr4.tree.ParseTreeWalker();\n" +
	                  "walker.walk(new this.LeafListener(), $ctx.r);\n" +
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
	                  "var TListener = require('./TListener').TListener;\n" +
	                  "}\n" +
	                  "\n" +
	                  "@parser::members {\n" +
	                  "this.LeafListener = function() {\n" +
	                  "    this.exitA = function(ctx) {\n" +
	                  "    	var str;\n" +
	                  "        if(ctx.getChildCount()===2) {\n" +
	                  "            str = ctx.b(0).start.text + ' ' + ctx.b(1).start.text + ' ' + ctx.b()[0].start.text;\n" +
	                  "        } else {\n" +
	                  "            str = ctx.b(0).start.text;\n" +
	                  "        }\n" +
	                  "    	document.getElementById('output').value += str + '\\n';\n" +
	                  "    };\n" +
	                  "    return this;\n" +
	                  "};\n" +
	                  "this.LeafListener.prototype = Object.create(TListener.prototype);\n" +
	                  "this.LeafListener.prototype.constructor = this.LeafListener;\n" +
	                  "\n" +
	                  "}\n" +
	                  "\n" +
	                  "s\n" +
	                  "@after {\n" +
	                  "document.getElementById('output').value += $ctx.r.toStringTree(null, this) + '\\n';\n" +
	                  "var walker = new antlr4.tree.ParseTreeWalker();\n" +
	                  "walker.walk(new this.LeafListener(), $ctx.r);\n" +
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
	                  "var TListener = require('./TListener').TListener;\n" +
	                  "}\n" +
	                  "\n" +
	                  "@parser::members {\n" +
	                  "this.LeafListener = function() {\n" +
	                  "    this.exitE = function(ctx) {\n" +
	                  "    	var str;\n" +
	                  "        if(ctx.getChildCount()===3) {\n" +
	                  "            str = ctx.e(0).start.text + ' ' + ctx.e(1).start.text + ' ' + ctx.e()[0].start.text;\n" +
	                  "        } else {\n" +
	                  "            str = ctx.INT().symbol.text;\n" +
	                  "        }\n" +
	                  "    	document.getElementById('output').value += str + '\\n';\n" +
	                  "    };\n" +
	                  "    return this;\n" +
	                  "};\n" +
	                  "this.LeafListener.prototype = Object.create(TListener.prototype);\n" +
	                  "this.LeafListener.prototype.constructor = this.LeafListener;\n" +
	                  "\n" +
	                  "}\n" +
	                  "\n" +
	                  "s\n" +
	                  "@after {\n" +
	                  "document.getElementById('output').value += $ctx.r.toStringTree(null, this) + '\\n';\n" +
	                  "var walker = new antlr4.tree.ParseTreeWalker();\n" +
	                  "walker.walk(new this.LeafListener(), $ctx.r);\n" +
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

	/* this file and method are generated, any edit will be overwritten by the next generation */
	@Test
	public void testLRWithLabels() throws Exception {
		String grammar = "grammar T;\n" +
	                  "@parser::header {\n" +
	                  "var TListener = require('./TListener').TListener;\n" +
	                  "}\n" +
	                  "\n" +
	                  "@parser::members {\n" +
	                  "this.LeafListener = function() {\n" +
	                  "    this.exitCall = function(ctx) {\n" +
	                  "    	var str = ctx.e().start.text + ' ' + ctx.eList();\n" +
	                  "    	document.getElementById('output').value += str + '\\n';\n" +
	                  "    };\n" +
	                  "    this.exitInt = function(ctx) {\n" +
	                  "        var str = ctx.INT().symbol.text;\n" +
	                  "        document.getElementById('output').value += str + '\\n';\n" +
	                  "    };\n" +
	                  "    return this;\n" +
	                  "};\n" +
	                  "this.LeafListener.prototype = Object.create(TListener.prototype);\n" +
	                  "this.LeafListener.prototype.constructor = this.LeafListener;\n" +
	                  "\n" +
	                  "}\n" +
	                  "\n" +
	                  "s\n" +
	                  "@after {\n" +
	                  "document.getElementById('output').value += $ctx.r.toStringTree(null, this) + '\\n';\n" +
	                  "var walker = new antlr4.tree.ParseTreeWalker();\n" +
	                  "walker.walk(new this.LeafListener(), $ctx.r);\n" +
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