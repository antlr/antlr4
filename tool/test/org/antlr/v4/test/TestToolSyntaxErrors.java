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

public class TestToolSyntaxErrors extends BaseTest {
    static String[] A = {
        // INPUT
        "grammar A;\n" +
        "",
        // YIELDS
        "error(99): A.g4::: grammar A has no rules\n",

		"A;",
		"error(50): <string>:1:0: 'A' came as a complete surprise to me\n",

		"grammar ;",
		"error(50): <string>:1:8: ';' came as a complete surprise to me while looking for an identifier\n",

		"grammar A\n" +
		"a : ID ;\n",
		"error(50): <string>:2:0: missing SEMI at 'a'\n",

		"grammar A;\n" +
		"a : ID ;;\n"+
		"b : B ;",
		"error(50): A.g4:2:8: ';' came as a complete surprise to me\n",

		"grammar A;;\n" +
		"a : ID ;\n",
		"error(50): A;.g4:1:10: ';' came as a complete surprise to me\n",

		"grammar A;\n" +
		"a @init : ID ;\n",
		"error(50): A.g4:2:8: mismatched input ':' expecting ACTION while matching rule preamble\n",

		"grammar A;\n" +
		"a  ( A | B ) D ;\n" +
		"b : B ;",
		"error(50): A.g4:2:3: '(' came as a complete surprise to me while matching rule preamble\n" +
		"error(50): A.g4:2:11: mismatched input ')' expecting SEMI while matching a rule\n" +
		"error(50): A.g4:2:15: mismatched input ';' expecting COLON while matching a lexer rule\n",
    };

	@Test public void testA() { super.testErrors(A, true); }

	@Test public void testExtraColon() {
		String[] pair = new String[] {
			"grammar A;\n" +
			"a : : A ;\n" +
			"b : B ;",
			"error(50): A.g4:2:4: ':' came as a complete surprise to me while matching alternative\n",
		};
		super.testErrors(pair, true);
	}

	@Test public void testMissingRuleSemi() {
		String[] pair = new String[] {
			"grammar A;\n" +
			"a : A \n" +
			"b : B ;",
			"error(50): A.g4:3:0: unterminated rule (missing ';') detected at 'b :' while looking for rule element\n",
		};
		super.testErrors(pair, true);
	}

	@Test public void testMissingRuleSemi2() {
		String[] pair = new String[] {
			"lexer grammar A;\n" +
			"A : 'a' \n" +
			"B : 'b' ;",
			"error(50): A.g4:3:0: unterminated rule (missing ';') detected at 'B :' while looking for lexer rule element\n",
		};
		super.testErrors(pair, true);
	}

	@Test public void testMissingRuleSemi3() {
		String[] pair = new String[] {
			"grammar A;\n" +
			"a : A \n" +
			"b[int i] returns [int y] : B ;",
			"error(50): A.g4:3:9: unterminated rule (missing ';') detected at 'returns int y' while looking for rule element\n"
		};
		super.testErrors(pair, true);
	}

	@Test public void testMissingRuleSemi4() {
		String[] pair = new String[] {
			"grammar A;\n" +
			"a : b \n" +
			"  catch [Exception e] {...}\n" +
			"b : B ;\n",

			"error(50): A.g4:2:4: unterminated rule (missing ';') detected at 'b catch' while looking for rule element\n"
		};
		super.testErrors(pair, true);
	}

	@Test public void testMissingRuleSemi5() {
		String[] pair = new String[] {
			"grammar A;\n" +
			"a : A \n" +
			"  catch [Exception e] {...}\n",

			"error(50): A.g4:2:4: unterminated rule (missing ';') detected at 'A catch' while looking for rule element\n"
		};
		super.testErrors(pair, true);
	}

	@Test public void testBadRulePrequelStart() {
		String[] pair = new String[] {
			"grammar A;\n" +
			"a @ options {k=1;} : A ;\n" +
			"b : B ;",

			"error(50): A.g4:2:4: 'options {' came as a complete surprise to me while looking for an identifier\n"
		};
		super.testErrors(pair, true);
	}

	@Test public void testBadRulePrequelStart2() {
		String[] pair = new String[] {
			"grammar A;\n" +
			"a } : A ;\n" +
			"b : B ;",

			"error(50): A.g4:2:2: '}' came as a complete surprise to me while matching rule preamble\n"
		};
		super.testErrors(pair, true);
	}

	@Test public void testModeInParser() {
		String[] pair = new String[] {
			"grammar A;\n" +
			"a : A ;\n" +
			"mode foo;\n" +
			"b : B ;",

			"error(50): A.g4:4:0: 'b' came as a complete surprise to me\n" +
			"error(50): A.g4:4:6: mismatched input ';' expecting COLON while matching a lexer rule\n"
		};
		super.testErrors(pair, true);
	}

}
