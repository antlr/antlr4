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

import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.stringtemplate.v4.ST;

/** */
@SuppressWarnings("unused")
public class TestAttributeChecks extends BaseTest {
    String attributeTemplate =
        "parser grammar A;\n"+
        "@members {<members>}\n" +
		"tokens{ID}\n" +
        "a[int x] returns [int y]\n" +
        "@init {<init>}\n" +
        "    :   id=ID ids+=ID lab=b[34] labs+=b[34] {\n" +
		"		 <inline>\n" +
		"		 }\n" +
		"		 c\n" +
        "    ;\n" +
        "    finally {<finally>}\n" +
        "b[int d] returns [int e]\n" +
        "    :   {<inline2>}\n" +
        "    ;\n" +
        "c   :   ;\n";

    String[] membersChecks = {
		"$a",			"error(63): A.g4:2:11: unknown attribute reference 'a' in '$a'\n",
        "$a.y",			"error(63): A.g4:2:11: unknown attribute reference 'a' in '$a.y'\n",
    };

    String[] initChecks = {
		"$text",		"",
		"$start",		"",
		"$x = $y",		"",
		"$y = $x",		"",
		"$lab.e",		"",
		"$ids",			"",
		"$labs",		"",

		"$c",			"error(63): A.g4:5:8: unknown attribute reference 'c' in '$c'\n",
		"$a.q",			"error(65): A.g4:5:10: unknown attribute 'q' for rule 'a' in '$a.q'\n",
    };

	String[] inlineChecks = {
		"$text",		"",
		"$start",		"",
		"$x = $y",		"",
		"$y = $x",		"",
		"$a.x = $a.y",	"",
		"$lab.e",		"",
		"$lab.text",	"",
		"$b.e",			"",
		"$c.text",      "",
		"$ID",			"",
		"$ID.text",		"",
		"$id",			"",
		"$id.text",		"",
		"$ids",			"",
		"$labs",		"",
	};

	String[] bad_inlineChecks = {
		"$lab",			"error(67): A.g4:7:4: missing attribute access on rule reference 'lab' in '$lab'\n",
		"$q",           "error(63): A.g4:7:4: unknown attribute reference 'q' in '$q'\n",
		"$q.y",         "error(63): A.g4:7:4: unknown attribute reference 'q' in '$q.y'\n",
		"$q = 3",       "error(63): A.g4:7:4: unknown attribute reference 'q' in '$q'\n",
		"$q = 3;",      "error(63): A.g4:7:4: unknown attribute reference 'q' in '$q = 3;'\n",
		"$q.y = 3;",    "error(63): A.g4:7:4: unknown attribute reference 'q' in '$q.y = 3;'\n",
		"$q = $blort;", "error(63): A.g4:7:4: unknown attribute reference 'q' in '$q = $blort;'\n" +
						"error(63): A.g4:7:9: unknown attribute reference 'blort' in '$blort'\n",
		"$a.ick",       "error(65): A.g4:7:6: unknown attribute 'ick' for rule 'a' in '$a.ick'\n",
		"$a.ick = 3;",  "error(65): A.g4:7:6: unknown attribute 'ick' for rule 'a' in '$a.ick = 3;'\n",
		"$b.d",         "error(64): A.g4:7:6: parameter 'd' of rule 'b' is not accessible in this scope: $b.d\n",  // can't see rule ref's arg
		"$d.text",      "error(63): A.g4:7:4: unknown attribute reference 'd' in '$d.text'\n", // valid rule, but no ref
		"$lab.d",		"error(64): A.g4:7:8: parameter 'd' of rule 'b' is not accessible in this scope: $lab.d\n",
		"$ids = null;",	"error(135): A.g4:7:4: cannot assign a value to list label 'ids'\n",
		"$labs = null;","error(135): A.g4:7:4: cannot assign a value to list label 'labs'\n",
	};

	String[] finallyChecks = {
		"$text",		"",
		"$start",		"",
		"$x = $y",		"",
		"$y = $x",		"",
		"$lab.e",		"",
		"$lab.text",	"",
		"$id",			"",
		"$id.text",		"",
		"$ids",			"",
		"$labs",		"",

		"$lab",			"error(67): A.g4:10:14: missing attribute access on rule reference 'lab' in '$lab'\n",
		"$q",           "error(63): A.g4:10:14: unknown attribute reference 'q' in '$q'\n",
		"$q.y",         "error(63): A.g4:10:14: unknown attribute reference 'q' in '$q.y'\n",
		"$q = 3",       "error(63): A.g4:10:14: unknown attribute reference 'q' in '$q'\n",
		"$q = 3;",      "error(63): A.g4:10:14: unknown attribute reference 'q' in '$q = 3;'\n",
		"$q.y = 3;",    "error(63): A.g4:10:14: unknown attribute reference 'q' in '$q.y = 3;'\n",
		"$q = $blort;", "error(63): A.g4:10:14: unknown attribute reference 'q' in '$q = $blort;'\n" +
						"error(63): A.g4:10:19: unknown attribute reference 'blort' in '$blort'\n",
		"$a.ick",       "error(65): A.g4:10:16: unknown attribute 'ick' for rule 'a' in '$a.ick'\n",
		"$a.ick = 3;",  "error(65): A.g4:10:16: unknown attribute 'ick' for rule 'a' in '$a.ick = 3;'\n",
		"$b.e",			"error(63): A.g4:10:14: unknown attribute reference 'b' in '$b.e'\n", // can't see rule refs outside alts
		"$b.d",         "error(63): A.g4:10:14: unknown attribute reference 'b' in '$b.d'\n",
		"$c.text",      "error(63): A.g4:10:14: unknown attribute reference 'c' in '$c.text'\n",
		"$lab.d",		"error(64): A.g4:10:18: parameter 'd' of rule 'b' is not accessible in this scope: $lab.d\n",
	};

	String[] dynMembersChecks = {
		"$S",			"",
		"$S::i",		"",
		"$S::i=$S::i",	"",

		"$b::f",		"error(54): A.g4:3:1: unknown dynamic scope: b in $b::f\n",
		"$S::j",		"error(55): A.g4:3:4: unknown dynamically-scoped attribute for scope S: j in $S::j\n",
		"$S::j = 3;",	"error(55): A.g4:3:4: unknown dynamically-scoped attribute for scope S: j in $S::j = 3;\n",
		"$S::j = $S::k;",	"error(55): A.g4:3:4: unknown dynamically-scoped attribute for scope S: j in $S::j = $S::k;\n" +
							"error(55): A.g4:3:12: unknown dynamically-scoped attribute for scope S: k in $S::k\n",
	};

	String[] dynInitChecks = {
		"$a",			"",
		"$b",			"",
		"$lab",			"",
		"$b::f",		"",
		"$S::i",		"",
		"$S::i=$S::i",	"",
		"$a::z",		"",
		"$S",			"",

		"$S::j",		"error(55): A.g4:8:11: unknown dynamically-scoped attribute for scope S: j in $S::j\n",
		"$S::j = 3;",	"error(55): A.g4:8:11: unknown dynamically-scoped attribute for scope S: j in $S::j = 3;\n",
		"$S::j = $S::k;",	"error(55): A.g4:8:11: unknown dynamically-scoped attribute for scope S: j in $S::j = $S::k;\n" +
							"error(55): A.g4:8:19: unknown dynamically-scoped attribute for scope S: k in $S::k\n",
	};

	String[] dynInlineChecks = {
		"$a",				"",
		"$b",				"",
		"$lab",				"",
		"$b::f",			"",
		"$S",				"",
		"$S::i",			"",
		"$S::i=$S::i",		"",
		"$a::z",			"",

		"$S::j",			"error(55): A.g4:10:7: unknown dynamically-scoped attribute for scope S: j in $S::j\n",
		"$S::j = 3;",		"error(55): A.g4:10:7: unknown dynamically-scoped attribute for scope S: j in $S::j = 3;\n",
		"$S::j = $S::k;",	"error(55): A.g4:10:7: unknown dynamically-scoped attribute for scope S: j in $S::j = $S::k;\n" +
							"error(55): A.g4:10:15: unknown dynamically-scoped attribute for scope S: k in $S::k\n",
		"$Q[-1]::y",        "error(54): A.g4:10:4: unknown dynamic scope: Q in $Q[-1]::y\n",
		"$Q[-i]::y",        "error(54): A.g4:10:4: unknown dynamic scope: Q in $Q[-i]::y\n",
		"$Q[i]::y",    		"error(54): A.g4:10:4: unknown dynamic scope: Q in $Q[i]::y\n",
		"$Q[0]::y",    		"error(54): A.g4:10:4: unknown dynamic scope: Q in $Q[0]::y\n",
		"$Q[-1]::y = 23;",  "error(54): A.g4:10:4: unknown dynamic scope: Q in $Q[-1]::y = 23;\n",
		"$Q[-i]::y = 23;",  "error(54): A.g4:10:4: unknown dynamic scope: Q in $Q[-i]::y = 23;\n",
		"$Q[i]::y = 23;",   "error(54): A.g4:10:4: unknown dynamic scope: Q in $Q[i]::y = 23;\n",
		"$Q[0]::y = 23;",   "error(54): A.g4:10:4: unknown dynamic scope: Q in $Q[0]::y = 23;\n",
		"$S[-1]::y",        "error(55): A.g4:10:11: unknown dynamically-scoped attribute for scope S: y in $S[-1]::y\n",
		"$S[-i]::y",        "error(55): A.g4:10:11: unknown dynamically-scoped attribute for scope S: y in $S[-i]::y\n",
		"$S[i]::y",     	"error(55): A.g4:10:10: unknown dynamically-scoped attribute for scope S: y in $S[i]::y\n",
		"$S[0]::y",     	"error(55): A.g4:10:10: unknown dynamically-scoped attribute for scope S: y in $S[0]::y\n",
		"$S[-1]::y = 23;",  "error(55): A.g4:10:11: unknown dynamically-scoped attribute for scope S: y in $S[-1]::y = 23;\n",
		"$S[-i]::y = 23;",  "error(55): A.g4:10:11: unknown dynamically-scoped attribute for scope S: y in $S[-i]::y = 23;\n",
		"$S[i]::y = 23;",   "error(55): A.g4:10:10: unknown dynamically-scoped attribute for scope S: y in $S[i]::y = 23;\n",
		"$S[0]::y = 23;",   "error(55): A.g4:10:10: unknown dynamically-scoped attribute for scope S: y in $S[0]::y = 23;\n",
		"$S[$S::y]::i",		"error(55): A.g4:10:10: unknown dynamically-scoped attribute for scope S: y in $S::y\n"
	};

	String[] dynFinallyChecks = {
		"$a",			"",
		"$b",			"",
		"$lab",			"",
		"$b::f",		"",
		"$S",			"",
		"$S::i",		"",
		"$S::i=$S::i",	"",
		"$a::z",		"",

		"$S::j",		"error(55): A.g4:12:17: unknown dynamically-scoped attribute for scope S: j in $S::j\n",
		"$S::j = 3;",	"error(55): A.g4:12:17: unknown dynamically-scoped attribute for scope S: j in $S::j = 3;\n",
		"$S::j = $S::k;",	"error(55): A.g4:12:17: unknown dynamically-scoped attribute for scope S: j in $S::j = $S::k;\n" +
							"error(55): A.g4:12:25: unknown dynamically-scoped attribute for scope S: k in $S::k\n",
	};

    @Test public void testMembersActions() throws RecognitionException {
        testActions("members", membersChecks, attributeTemplate);
    }

    @Test public void testInitActions() throws RecognitionException {
        testActions("init", initChecks, attributeTemplate);
    }

	@Test public void testInlineActions() throws RecognitionException {
		testActions("inline", inlineChecks, attributeTemplate);
	}

	@Test public void testBadInlineActions() throws RecognitionException {
		testActions("inline", bad_inlineChecks, attributeTemplate);
	}

	@Test public void testFinallyActions() throws RecognitionException {
		testActions("finally", finallyChecks, attributeTemplate);
	}

	@Test public void testTokenRef() throws RecognitionException {
		String grammar =
			"parser grammar S;\n" +
			"tokens{ID}\n" +
			"a : x=ID {Token t = $x; t = $ID;} ;\n";
		String expected =
			"";
		testErrors(new String[] {grammar, expected}, false);
	}

	@Test public void testNonDynamicAttributeOutsideRule() throws Exception {
		String action = "public void foo() { $x; }";
	}
	@Test public void testNonDynamicAttributeOutsideRule2() throws Exception {
		String action = "public void foo() { $x.y; }";
	}
    @Test public void testUnknownGlobalScope() throws Exception {
        String action = "$Symbols::names.add($id.text);";
    }
	@Test public void testUnknownDynamicAttribute() throws Exception {
		String action = "$a::x";
	}

	@Test public void testUnknownGlobalDynamicAttribute() throws Exception {
		String action = "$Symbols::x";
	}


    public void testActions(String location, String[] pairs, String template) {
        for (int i = 0; i < pairs.length; i+=2) {
            String action = pairs[i];
            String expected = pairs[i+1];
            ST st = new ST(template);
            st.add(location, action);
            String grammar = st.render();
            testErrors(new String[] {grammar, expected}, false);
        }
    }
}
