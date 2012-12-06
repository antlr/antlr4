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

package org.antlr.v4.test;

import org.junit.Test;

public class TestBasicSemanticErrors extends BaseTest {
    static String[] U = {
        // INPUT
        "parser grammar U;\n" +
        "options { foo=bar; k=\"3\";}\n" +
        "tokens {\n" +
		"        ID,\n" +
        "        f,\n" +
        "        S\n" +
        "}\n" +
        "tokens { A }\n" +
        "options { x=y; }\n" +
        "\n" +
        "a\n" +
        "options { blech=bar; greedy=true; }\n" +
        "        :       ID\n" +
        "        ;\n" +
        "b : ( options { ick=bar; greedy=true; } : ID )+ ;\n" +
        "c : ID<blue> ID<x=y> ;",
        // YIELDS
		"warning(83): U.g4:2:10: illegal option foo\n" +
		"warning(83): U.g4:2:19: illegal option k\n" +
		"error(60): U.g4:5:8: token names must start with an uppercase letter: f\n" +
		"warning(83): U.g4:9:10: illegal option x\n" +
		"error(54): U.g4:9:0: repeated grammar prequel spec (option, token, or import); please merge\n" +
		"error(54): U.g4:8:0: repeated grammar prequel spec (option, token, or import); please merge\n" +
		"warning(83): U.g4:12:10: illegal option blech\n" +
		"warning(83): U.g4:12:21: illegal option greedy\n" +
		"warning(83): U.g4:15:16: illegal option ick\n" +
		"warning(83): U.g4:15:25: illegal option greedy\n" +
		"warning(83): U.g4:16:16: illegal option x\n",
    };

	@Test public void testU() { super.testErrors(U, false); }

	/**
	 * Regression test for #25 "Don't allow labels on not token set subrules".
	 * https://github.com/antlr/antlr4/issues/25
	 */
	@Test
	public void testIllegalNonSetLabel() throws Exception {
		String grammar =
			"grammar T;\n" +
			"ss : op=('=' | '+=' | expr) EOF;\n" +
			"expr : '=' '=';\n" +
			"";

		String expected =
			"error(130): T.g4:2:5: label op assigned to a block which is not a set\n";

		testErrors(new String[] { grammar, expected }, false);
	}

}
