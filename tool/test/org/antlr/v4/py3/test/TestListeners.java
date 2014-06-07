/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.py3.test;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestListeners extends BasePython3Test {
	
	@Test public void testBasic() throws Exception {
		String grammar =
			"grammar T;\n" +
		    "@parser::members {\n" +
			"class LeafListener(TListener):\n" +
			"    def visitTerminal(self, node):\n" +
			"        print(node.symbol.text)\n" +
		    "}\n" +
		    "\n" +
			"s\n" +
			"@after {" +
			"print($r.ctx.toStringTree(recog=self))\n" +
			"walker = ParseTreeWalker()\n" +
			"walker.walk(TParser.LeafListener(), $r.ctx)\n" +
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
		String result = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "1 2", false);
		String expecting = "(a 1 2)\n" +
						   "1\n" +
						   "2\n";
		assertEquals(expecting, result);
	}

	public String testTokenGetters(String input) throws Exception {
		String grammar =
			"grammar T;\n" +
		    "@parser::members {\n" +
			"class LeafListener(TListener):\n" +
			"    def exitA(self, ctx):\n" +
			"        if ctx.getChildCount()==2:\n" +
			"            print(ctx.INT(0).symbol.text + ' ' + ctx.INT(1).symbol.text + ' ' + str_list(ctx.INT()))\n" +
			"        else:\n" +
			"            print(str(ctx.ID().symbol))\n" +
		    "}\n" +
		    "\n" +
			"\n" +
			"s\n" +
			"@after {" +
			"print($r.ctx.toStringTree(recog=self))\n" +
			"walker = ParseTreeWalker()\n" +
			"walker.walk(TParser.LeafListener(), $r.ctx)\n" +
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
		return execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", input, false);
	}
	
	@Test public void testTokenGetters1() throws Exception {
		String result = testTokenGetters("1 2");
		String expecting =
			"(a 1 2)\n" +
			"1 2 [1, 2]\n";
		assertEquals(expecting, result);
	}
	
	@Test public void testTokenGetters2() throws Exception {
		String result = testTokenGetters("abc");
		String expecting = "(a abc)\n" +
					"[@0,0:2='abc',<4>,1:0]\n";
		assertEquals(expecting, result);
	}

	@Test public void testRuleGetters() throws Exception {
		String grammar =
			"grammar T;\n" +
		    "@parser::members {\n" +
			"class LeafListener(TListener):\n" +
			"    def exitA(self, ctx):\n" +
			"        if ctx.getChildCount()==2:\n" +
			"            print(ctx.b(0).start.text + ' ' + ctx.b(1).start.text + ' ' + ctx.b()[0].start.text)\n" +
			"        else:\n" +
			"            print(ctx.b(0).start.text)\n" +
		    "}\n" +
			"s\n" +
			"@after {" +
			"print($r.ctx.toStringTree(recog=self))\n" +
			"walker = ParseTreeWalker()\n" +
			"walker.walk(TParser.LeafListener(), $r.ctx)\n" +
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
		String result = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "1 2", false);
		String expecting = "(a (b 1) (b 2))\n" +
						   "1 2 1\n";
		assertEquals(expecting, result);

		result = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "abc", false);
		expecting = "(a (b abc))\n" +
					"abc\n";
		assertEquals(expecting, result);
	}

	@Test public void testLR() throws Exception {
		String grammar =
			"grammar T;\n" +
		    "@parser::members {\n" +
			"class LeafListener(TListener):\n" +
			"    def exitE(self, ctx):\n" +
			"        if ctx.getChildCount()==3:\n" +
			"            print(ctx.e(0).start.text + ' ' + ctx.e(1).start.text + ' ' + ctx.e()[0].start.text)\n" +
			"        else:\n" +
			"            print(ctx.INT().symbol.text)\n" +
		    "}\n" +
			"s\n" +
			"@after {" +
			"print($r.ctx.toStringTree(recog=self))\n" +
			"walker = ParseTreeWalker()\n" +
			"walker.walk(TParser.LeafListener(), $r.ctx)\n" +
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
		String result = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "1+2*3", false);
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
		    "@parser::members {\n" +
			"class LeafListener(TListener):\n" +
			"    def exitCall(self, ctx):\n" +
			"        print(ctx.e().start.text + ' ' + str(ctx.eList()))\n" +
			"    def exitInt(self, ctx):\n" +
			"        print(ctx.INT().symbol.text)\n" +
		    "}\n" +
			"s\n" +
			"@after {" +
			"print($r.ctx.toStringTree(recog=self))\n" +
			"walker = ParseTreeWalker()\n" +
			"walker.walk(TParser.LeafListener(), $r.ctx)\n" +
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
		String result = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "1(2,3)", false);
		String expecting =
			"(e (e 1) ( (eList (e 2) , (e 3)) ))\n" +
			"1\n" +
			"2\n" +
			"3\n" +
			"1 [13 6]\n";
		assertEquals(expecting, result);
	}
}
