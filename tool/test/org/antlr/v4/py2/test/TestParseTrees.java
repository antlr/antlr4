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

package org.antlr.v4.py2.test;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestParseTrees extends BasePython2Test {
	@Test public void testTokenAndRuleContextString() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s\n" +
			"@init {" +
			"self._buildParseTrees = True" +
			"}\n" +
			"@after {" +
			"print($r.ctx.toStringTree(recog=self))" +
			"}\n" +
			"  :r=a ;\n" +
			"a : 'x' {" + 
			"print(str_list(self.getRuleInvocationStack()))" +
			"} ;\n";
		String result = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "x", false);
		String expecting = "[a, s]\n(a x)\n";
		assertEquals(expecting, result);
	}

	@Test public void testToken2() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s\n" +
			"@init {" +
			"self._buildParseTrees = True" +
			"}\n" +
			"@after {" +
			"print($r.ctx.toStringTree(recog=self))" +
			"}\n" +
			"  :r=a ;\n" +
			"a : 'x' 'y'\n" +
			"  ;\n";
		String result = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "xy", false);
		String expecting = "(a x y)\n";
		assertEquals(expecting, result);
	}

	@Test public void test2Alts() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s\n" +
			"@init {" +
			"self._buildParseTrees = True" +
			"}\n" +
			"@after {" +
			"print($r.ctx.toStringTree(recog=self))" +
			"}\n" +
			"  :r=a ;\n" +
			"a : 'x' | 'y'\n" +
			"  ;\n";
		String result = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "y", false);
		String expecting = "(a y)\n";
		assertEquals(expecting, result);
	}

	@Test public void test2AltLoop() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s\n" +
			"@init {" +
			"self._buildParseTrees = True" +
			"}\n" +
			"@after {" +
			"print($r.ctx.toStringTree(recog=self))" +
			"}\n" +
			"  :r=a ;\n" +
			"a : ('x' | 'y')* 'z'\n" +
			"  ;\n";
		String result = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "xyyxyxz", false);
		String expecting = "(a x y y x y x z)\n";
		assertEquals(expecting, result);
	}

	@Test public void testRuleRef() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s\n" +
			"@init {" +
			"self._buildParseTrees = True" +
			"}\n" +
			"@after {" +
			"print($r.ctx.toStringTree(recog=self))" +
			"}\n" +
			"  : r=a ;\n" +
			"a : b 'x'\n" +
			"  ;\n" +
			"b : 'y' ;\n";
		String result = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "yx", false);
		String expecting = "(a (b y) x)\n";
		assertEquals(expecting, result);
	}

	// ERRORS

	@Test public void testExtraToken() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s\n" +
			"@init {" +
			"self._buildParseTrees = True" +
			"}\n" +
			"@after {" +
			"print($r.ctx.toStringTree(recog=self))" +
			"}\n" +
			"  : r=a ;\n" +
			"a : 'x' 'y'\n" +
			"  ;\n" +
			"Z : 'z'; \n";
		String result = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "xzy", false);
		String expecting = "(a x z y)\n"; // ERRORs not shown. z is colored red in tree view
		assertEquals(expecting, result);
	}

	@Test public void testNoViableAlt() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s\n" +
			"@init {" +
			"self.buildParseTrees = True" +
			"}\n" +
			"@after {" +
			"print($r.ctx.toStringTree(recog=self))" +
			"}\n" +
			"  : r=a ;\n" +
			"a : 'x' | 'y'\n" +
			"  ;\n" +
			"Z : 'z'; \n";
		String result = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "z", false);
		String expecting = "(a z)\n";
		assertEquals(expecting, result);
	}

	@Test public void testSync() throws Exception {
		String grammar =
			"grammar T;\n" +
			"s\n" +
			"@init {" +
			"self._buildParseTrees = True" +
			"}\n" +
			"@after {" +
			"print($r.ctx.toStringTree(recog=self))" +
			"}\n" +
			"  : r=a ;\n" +
			"a : 'x' 'y'* '!'\n" +
			"  ;\n" +
			"Z : 'z'; \n";
		String result = execParser("T.g4", grammar, "TParser", "TLexer", "TListener", "TVisitor", "s", "xzyy!", false);
		String expecting = "(a x z y y !)\n";
		assertEquals(expecting, result);
	}
}
