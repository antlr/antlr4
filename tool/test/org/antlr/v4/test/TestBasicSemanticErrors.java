/*
 [The "BSD license"]
  Copyright (c) 2011 Terence Parr
  All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions
  are met:

  1. Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.
  2. Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following disclaimer in the
     documentation and/or other materials provided with the distribution.
  3. The name of the author may not be used to endorse or promote products
     derived from this software without specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.test;

import org.junit.Test;

public class TestBasicSemanticErrors extends BaseTest {
    static String[] A = {
        // INPUT
        "grammar A;\n" +
        "\n" +
        "options {\n" +
        "        output=template;\n" +
        "}\n" +
        "\n" +
        "a : ID<Foo> -> ID ;\n" +
        "\n" +
        "b : A^ | ((B!|C)) -> C;",
        // YIELDS
        "error(68): A.g:7:7: alts with rewrites can't use heterogeneous types left of ->\n" +
		"error(78): A.g:9:4: AST operator with non-AST output option: ^\n" +
		"error(78): A.g:9:11: AST operator with non-AST output option: !\n" +
		"error(79): A.g:9:11: rule b alt 2 uses rewrite syntax and also an AST operator\n",

        // INPUT
        "tree grammar B;\n" +
        "options {\n" +
        "\tfilter=true;\n" +
        "\toutput=template;\n" +
        "}\n" +
        "\n" +
        "a : A;\n" +
        "\n" +
        "b : ^(. A) ;",
        // YIELDS
        "error(80): B.g:9:6: Wildcard invalid as root; wildcard can itself be a tree\n" +
		"error(81): B.g:1:5: option output=template conflicts with tree grammar filter mode\n"
    };

    static String[] U = {
        // INPUT
        "parser grammar U;\n" +
        "options { foo=bar; k=\"*\";}\n" +
        "tokens {\n" +
        "        f='fkj';\n" +
        "        S = 'a';\n" +
        "}\n" +
        "tokens { A; }\n" +
        "options { x=y; }\n" +
        "\n" +
        "a\n" +
        "options { blech=bar; greedy=true; }\n" +
        "        :       ID\n" +
        "        ;\n" +
        "b : ( options { ick=bar; greedy=true; } : ID )+ ;\n" +
        "c : ID<blue> ID<x=y> ;",
        // YIELDS
		"warning(47): U.g:2:10: illegal option foo\n" +
		"warning(47): U.g:2:19: illegal option k\n" +
		": U.g:4:8: token names must start with an uppercase letter: f\n" +
		": U.g:4:8: can't assign string value to token name f in non-combined grammar\n" +
		": U.g:5:8: can't assign string value to token name S in non-combined grammar\n" +
		"warning(47): U.g:8:10: illegal option x\n" +
		": U.g:8:0: repeated grammar prequel spec (option, token, or import); please merge\n" +
		": U.g:7:0: repeated grammar prequel spec (option, token, or import); please merge\n" +
		"warning(47): U.g:11:10: illegal option blech\n" +
		"warning(47): U.g:11:21: illegal option greedy\n" +
		"warning(47): U.g:14:16: illegal option ick\n" +
		"warning(47): U.g:15:16: illegal option x\n",

        // INPUT
        "tree grammar V;\n" +
        "options {\n" +
        "        rewrite=true;\n" +
        "        output=template;\n" +
        "}\n" +
        "a : A\n" +
        "  | A B -> template() \"kjsfdkdsj\" \n" +
        "  ;",
        // YIELDS
        "warning(47): V.g:3:8: illegal option rewrite\n",


        // INPUT
        "tree grammar V;\n" +
        "options { rewrite=true; }\n" +
        "a : A\n" +
        "  | A B -> template() \"kjsfdkdsj\" \n" +
        "  ;",
        // YIELDS
        "warning(47): V.g:3:8: illegal option rewrite\n"
    };

	static String[] C = {
		"parser grammar C;\n" +
		"options {output=AST;}\n" +
		"tokens { A; B; C; }\n" +
		"a : A -> B $a A ;", // no problem with or $a.

		""
	};

    @Test public void testA() { super.testErrors(A, false); }
	@Test public void testU() { super.testErrors(U, false); }
	@Test public void testE() { super.testErrors(C, false); }
}
