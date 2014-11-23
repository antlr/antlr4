package org.antlr.v4.test.rt.js.explorer;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestListeners extends BaseTest {

	@Test
	public void testBasic() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "@parser::header {\r\n" +
	                  "var TListener = require('./TListener').TListener;\r\n" +
	                  "}\r\n" +
	                  "\r\n" +
	                  "@parser::members {\r\n" +
	                  "this.LeafListener = function() {\r\n" +
	                  "    this.visitTerminal = function(node) {\r\n" +
	                  "    	document.getElementById('output').value += node.symbol.text + '\\n';\r\n" +
	                  "    };\r\n" +
	                  "    return this;\r\n" +
	                  "};\r\n" +
	                  "this.LeafListener.prototype = Object.create(TListener.prototype);\r\n" +
	                  "this.LeafListener.prototype.constructor = this.LeafListener;\r\n" +
	                  "\r\n" +
	                  "}\r\n" +
	                  "\r\n" +
	                  "s\r\n" +
	                  "@after {\r\n" +
	                  "document.getElementById('output').value += $ctx.r.toStringTree(null, this) + '\\n';\r\n" +
	                  "var walker = new antlr4.tree.ParseTreeWalker();\r\n" +
	                  "walker.walk(new this.LeafListener(), $ctx.r);\r\n" +
	                  "}\r\n" +
	                  "  : r=a ;\r\n" +
	                  "a : INT INT\r\n" +
	                  "  | ID\r\n" +
	                  "  ;\r\n" +
	                  "MULT: '*' ;\r\n" +
	                  "ADD : '+' ;\r\n" +
	                  "INT : [0-9]+ ;\r\n" +
	                  "ID  : [a-z]+ ;\r\n" +
	                  "WS : [ \\t\\n]+ -> skip ;";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "1 2", false);
		assertEquals("(a 1 2)\n1\n2\n", found);
		assertNull(this.stderrDuringParse);
	}

	String testTokenGetters(String input) throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "@parser::header {\r\n" +
	                  "var TListener = require('./TListener').TListener;\r\n" +
	                  "}\r\n" +
	                  "\r\n" +
	                  "@parser::members {\r\n" +
	                  "this.LeafListener = function() {\r\n" +
	                  "    this.exitA = function(ctx) {\r\n" +
	                  "    	var str;\r\n" +
	                  "        if(ctx.getChildCount()===2) {\r\n" +
	                  "            str = ctx.INT(0).symbol.text + ' ' + ctx.INT(1).symbol.text + ' ' + antlr4.Utils.arrayToString(ctx.INT());\r\n" +
	                  "        } else {\r\n" +
	                  "            str = ctx.ID().symbol.toString();\r\n" +
	                  "        }\r\n" +
	                  "    	document.getElementById('output').value += str + '\\n';\r\n" +
	                  "    };\r\n" +
	                  "    return this;\r\n" +
	                  "};\r\n" +
	                  "this.LeafListener.prototype = Object.create(TListener.prototype);\r\n" +
	                  "this.LeafListener.prototype.constructor = this.LeafListener;\r\n" +
	                  "\r\n" +
	                  "}\r\n" +
	                  "\r\n" +
	                  "s\r\n" +
	                  "@after {\r\n" +
	                  "document.getElementById('output').value += $ctx.r.toStringTree(null, this) + '\\n';\r\n" +
	                  "var walker = new antlr4.tree.ParseTreeWalker();\r\n" +
	                  "walker.walk(new this.LeafListener(), $ctx.r);\r\n" +
	                  "}\r\n" +
	                  "  : r=a ;\r\n" +
	                  "a : INT INT\r\n" +
	                  "  | ID\r\n" +
	                  "  ;\r\n" +
	                  "MULT: '*' ;\r\n" +
	                  "ADD : '+' ;\r\n" +
	                  "INT : [0-9]+ ;\r\n" +
	                  "ID  : [a-z]+ ;\r\n" +
	                  "WS : [ \\t\\n]+ -> skip ;\r";
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
		String grammar = "grammar T;\r\n" +
	                  "@parser::header {\r\n" +
	                  "var TListener = require('./TListener').TListener;\r\n" +
	                  "}\r\n" +
	                  "\r\n" +
	                  "@parser::members {\r\n" +
	                  "this.LeafListener = function() {\r\n" +
	                  "    this.exitA = function(ctx) {\r\n" +
	                  "    	var str;\r\n" +
	                  "        if(ctx.getChildCount()===2) {\r\n" +
	                  "            str = ctx.b(0).start.text + ' ' + ctx.b(1).start.text + ' ' + ctx.b()[0].start.text;\r\n" +
	                  "        } else {\r\n" +
	                  "            str = ctx.b(0).start.text;\r\n" +
	                  "        }\r\n" +
	                  "    	document.getElementById('output').value += str + '\\n';\r\n" +
	                  "    };\r\n" +
	                  "    return this;\r\n" +
	                  "};\r\n" +
	                  "this.LeafListener.prototype = Object.create(TListener.prototype);\r\n" +
	                  "this.LeafListener.prototype.constructor = this.LeafListener;\r\n" +
	                  "\r\n" +
	                  "}\r\n" +
	                  "\r\n" +
	                  "s\r\n" +
	                  "@after {\r\n" +
	                  "document.getElementById('output').value += $ctx.r.toStringTree(null, this) + '\\n';\r\n" +
	                  "var walker = new antlr4.tree.ParseTreeWalker();\r\n" +
	                  "walker.walk(new this.LeafListener(), $ctx.r);\r\n" +
	                  "}\r\n" +
	                  "  : r=a ;\r\n" +
	                  "a : b b		// forces list\r\n" +
	                  "  | b		// a list still\r\n" +
	                  "  ;\r\n" +
	                  "b : ID | INT;\r\n" +
	                  "MULT: '*' ;\r\n" +
	                  "ADD : '+' ;\r\n" +
	                  "INT : [0-9]+ ;\r\n" +
	                  "ID  : [a-z]+ ;\r\n" +
	                  "WS : [ \\t\\n]+ -> skip ;\r";
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
		String grammar = "grammar T;\r\n" +
	                  "@parser::header {\r\n" +
	                  "var TListener = require('./TListener').TListener;\r\n" +
	                  "}\r\n" +
	                  "\r\n" +
	                  "@parser::members {\r\n" +
	                  "this.LeafListener = function() {\r\n" +
	                  "    this.exitE = function(ctx) {\r\n" +
	                  "    	var str;\r\n" +
	                  "        if(ctx.getChildCount()===3) {\r\n" +
	                  "            str = ctx.e(0).start.text + ' ' + ctx.e(1).start.text + ' ' + ctx.e()[0].start.text;\r\n" +
	                  "        } else {\r\n" +
	                  "            str = ctx.INT().symbol.text;\r\n" +
	                  "        }\r\n" +
	                  "    	document.getElementById('output').value += str + '\\n';\r\n" +
	                  "    };\r\n" +
	                  "    return this;\r\n" +
	                  "};\r\n" +
	                  "this.LeafListener.prototype = Object.create(TListener.prototype);\r\n" +
	                  "this.LeafListener.prototype.constructor = this.LeafListener;\r\n" +
	                  "\r\n" +
	                  "}\r\n" +
	                  "\r\n" +
	                  "s\r\n" +
	                  "@after {\r\n" +
	                  "document.getElementById('output').value += $ctx.r.toStringTree(null, this) + '\\n';\r\n" +
	                  "var walker = new antlr4.tree.ParseTreeWalker();\r\n" +
	                  "walker.walk(new this.LeafListener(), $ctx.r);\r\n" +
	                  "}\r\n" +
	                  "	: r=e ;\r\n" +
	                  "e : e op='*' e\r\n" +
	                  "	| e op='+' e\r\n" +
	                  "	| INT\r\n" +
	                  "	;\r\n" +
	                  "MULT: '*' ;\r\n" +
	                  "ADD : '+' ;\r\n" +
	                  "INT : [0-9]+ ;\r\n" +
	                  "ID  : [a-z]+ ;\r\n" +
	                  "WS : [ \\t\\n]+ -> skip ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "1+2*3", false);
		assertEquals("(e (e 1) + (e (e 2) * (e 3)))\n1\n2\n3\n2 3 2\n1 2 1\n", found);
		assertNull(this.stderrDuringParse);
	}

	@Test
	public void testLRWithLabels() throws Exception {
		String grammar = "grammar T;\r\n" +
	                  "@parser::header {\r\n" +
	                  "var TListener = require('./TListener').TListener;\r\n" +
	                  "}\r\n" +
	                  "\r\n" +
	                  "@parser::members {\r\n" +
	                  "this.LeafListener = function() {\r\n" +
	                  "    this.exitCall = function(ctx) {\r\n" +
	                  "    	var str = ctx.e().start.text + ' ' + ctx.eList();\r\n" +
	                  "    	document.getElementById('output').value += str + '\\n';\r\n" +
	                  "    };\r\n" +
	                  "    this.exitInt = function(ctx) {\r\n" +
	                  "        var str = ctx.INT().symbol.text;\r\n" +
	                  "        document.getElementById('output').value += str + '\\n';\r\n" +
	                  "    };\r\n" +
	                  "    return this;\r\n" +
	                  "};\r\n" +
	                  "this.LeafListener.prototype = Object.create(TListener.prototype);\r\n" +
	                  "this.LeafListener.prototype.constructor = this.LeafListener;\r\n" +
	                  "\r\n" +
	                  "}\r\n" +
	                  "\r\n" +
	                  "s\r\n" +
	                  "@after {\r\n" +
	                  "document.getElementById('output').value += $ctx.r.toStringTree(null, this) + '\\n';\r\n" +
	                  "var walker = new antlr4.tree.ParseTreeWalker();\r\n" +
	                  "walker.walk(new this.LeafListener(), $ctx.r);\r\n" +
	                  "}\r\n" +
	                  "  : r=e ;\r\n" +
	                  "e : e '(' eList ')' # Call\r\n" +
	                  "  | INT             # Int\r\n" +
	                  "  ;\r\n" +
	                  "eList : e (',' e)* ;\r\n" +
	                  "MULT: '*' ;\r\n" +
	                  "ADD : '+' ;\r\n" +
	                  "INT : [0-9]+ ;\r\n" +
	                  "ID  : [a-z]+ ;\r\n" +
	                  "WS : [ \\t\\n]+ -> skip ;\r";
		String found = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "1(2,3)", false);
		assertEquals("(e (e 1) ( (eList (e 2) , (e 3)) ))\n1\n2\n3\n1 [13 6]\n", found);
		assertNull(this.stderrDuringParse);
	}


}