/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.test.tool;

import org.antlr.runtime.RecognitionException;
import org.antlr.v4.tool.ErrorType;
import org.junit.Before;
import org.junit.Test;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.misc.ErrorBuffer;

/** */
public class TestAttributeChecks extends BaseJavaToolTest {
	@Before
	@Override
	public void testSetUp() throws Exception {
		super.testSetUp();
	}

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
	    "$a",			"error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:2:11: unknown attribute reference a in $a\n",
        "$a.y",			"error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:2:11: unknown attribute reference a in $a.y\n",
    };

    String[] initChecks = {
		"$text",		"",
		"$start",		"",
		"$x = $y",		"",
		"$y = $x",		"",
		"$lab.e",		"",
		"$ids",			"",
		"$labs",		"",

		"$c",			"error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:5:8: unknown attribute reference c in $c\n",
		"$a.q",			"error(" + ErrorType.UNKNOWN_RULE_ATTRIBUTE.code + "): A.g4:5:10: unknown attribute q for rule a in $a.q\n",
    };

	String[] inlineChecks = {
		"$text",		"",
		"$start",		"",
		"$x = $y",		"",
		"$y = $x",		"",
		"$y.b = 3;",	"",
		"$ctx.x = $ctx.y",	"",
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
		"$lab",			"error(" + ErrorType.ISOLATED_RULE_REF.code + "): A.g4:7:4: missing attribute access on rule reference lab in $lab\n",
		"$q",           "error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:7:4: unknown attribute reference q in $q\n",
		"$q.y",         "error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:7:4: unknown attribute reference q in $q.y\n",
		"$q = 3",       "error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:7:4: unknown attribute reference q in $q\n",
		"$q = 3;",      "error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:7:4: unknown attribute reference q in $q = 3;\n",
		"$q.y = 3;",    "error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:7:4: unknown attribute reference q in $q.y\n",
		"$q = $blort;", "error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:7:4: unknown attribute reference q in $q = $blort;\n" +
						"error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:7:9: unknown attribute reference blort in $blort\n",
		"$a.ick",       "error(" + ErrorType.UNKNOWN_RULE_ATTRIBUTE.code + "): A.g4:7:6: unknown attribute ick for rule a in $a.ick\n",
		"$a.ick = 3;",  "error(" + ErrorType.UNKNOWN_RULE_ATTRIBUTE.code + "): A.g4:7:6: unknown attribute ick for rule a in $a.ick\n",
		"$b.d",         "error(" + ErrorType.INVALID_RULE_PARAMETER_REF.code + "): A.g4:7:6: parameter d of rule b is not accessible in this scope: $b.d\n",  // cant see rule refs arg
		"$d.text",      "error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:7:4: unknown attribute reference d in $d.text\n", // valid rule, but no ref
		"$lab.d",		"error(" + ErrorType.INVALID_RULE_PARAMETER_REF.code + "): A.g4:7:8: parameter d of rule b is not accessible in this scope: $lab.d\n",
		"$ids = null;",	"error(" + ErrorType.ASSIGNMENT_TO_LIST_LABEL.code + "): A.g4:7:4: cannot assign a value to list label ids\n",
		"$labs = null;","error(" + ErrorType.ASSIGNMENT_TO_LIST_LABEL.code + "): A.g4:7:4: cannot assign a value to list label labs\n",
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

		"$lab",			"error(" + ErrorType.ISOLATED_RULE_REF.code + "): A.g4:10:14: missing attribute access on rule reference lab in $lab\n",
		"$q",           "error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:10:14: unknown attribute reference q in $q\n",
		"$q.y",         "error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:10:14: unknown attribute reference q in $q.y\n",
		"$q = 3",       "error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:10:14: unknown attribute reference q in $q\n",
		"$q = 3;",      "error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:10:14: unknown attribute reference q in $q = 3;\n",
		"$q.y = 3;",    "error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:10:14: unknown attribute reference q in $q.y\n",
		"$q = $blort;", "error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:10:14: unknown attribute reference q in $q = $blort;\n" +
						"error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:10:19: unknown attribute reference blort in $blort\n",
		"$a.ick",       "error(" + ErrorType.UNKNOWN_RULE_ATTRIBUTE.code + "): A.g4:10:16: unknown attribute ick for rule a in $a.ick\n",
		"$a.ick = 3;",  "error(" + ErrorType.UNKNOWN_RULE_ATTRIBUTE.code + "): A.g4:10:16: unknown attribute ick for rule a in $a.ick\n",
		"$b.e",			"error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:10:14: unknown attribute reference b in $b.e\n", // cant see rule refs outside alts
		"$b.d",         "error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:10:14: unknown attribute reference b in $b.d\n",
		"$c.text",      "error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:10:14: unknown attribute reference c in $c.text\n",
		"$lab.d",		"error(" + ErrorType.INVALID_RULE_PARAMETER_REF.code + "): A.g4:10:18: parameter d of rule b is not accessible in this scope: $lab.d\n",
	};

	String[] dynMembersChecks = {
		"$S",			"error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:2:11: unknown attribute reference S in $S\n",
		"$S::i",		"error(" + ErrorType.UNDEFINED_RULE_IN_NONLOCAL_REF.code + "): A.g4:2:11: reference to undefined rule S in non-local ref $S::i\n",
		"$S::i=$S::i",	"error(" + ErrorType.UNDEFINED_RULE_IN_NONLOCAL_REF.code + "): A.g4:2:11: reference to undefined rule S in non-local ref $S::i\n" +
						"error(" + ErrorType.UNDEFINED_RULE_IN_NONLOCAL_REF.code + "): A.g4:2:17: reference to undefined rule S in non-local ref $S::i\n",

		"$b::f",		"error(" + ErrorType.UNKNOWN_RULE_ATTRIBUTE.code + "): A.g4:2:14: unknown attribute f for rule b in $b::f\n",
		"$S::j",		"error(" + ErrorType.UNDEFINED_RULE_IN_NONLOCAL_REF.code + "): A.g4:2:11: reference to undefined rule S in non-local ref $S::j\n",
		"$S::j = 3;",	"error(" + ErrorType.UNDEFINED_RULE_IN_NONLOCAL_REF.code + "): A.g4:2:11: reference to undefined rule S in non-local ref $S::j = 3;\n",
		"$S::j = $S::k;",	"error(" + ErrorType.UNDEFINED_RULE_IN_NONLOCAL_REF.code + "): A.g4:2:11: reference to undefined rule S in non-local ref $S::j = $S::k;\n",
	};

	String[] dynInitChecks = {
		"$a",			"error(" + ErrorType.ISOLATED_RULE_REF.code + "): A.g4:5:8: missing attribute access on rule reference a in $a\n",
		"$b",			"error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:5:8: unknown attribute reference b in $b\n",
		"$lab",			"error(" + ErrorType.ISOLATED_RULE_REF.code + "): A.g4:5:8: missing attribute access on rule reference lab in $lab\n",
		"$b::f",		"error(" + ErrorType.UNKNOWN_RULE_ATTRIBUTE.code + "): A.g4:5:11: unknown attribute f for rule b in $b::f\n",
		"$S::i",		"error(" + ErrorType.UNDEFINED_RULE_IN_NONLOCAL_REF.code + "): A.g4:5:8: reference to undefined rule S in non-local ref $S::i\n",
		"$S::i=$S::i",	"error(" + ErrorType.UNDEFINED_RULE_IN_NONLOCAL_REF.code + "): A.g4:5:8: reference to undefined rule S in non-local ref $S::i\n" +
						"error(" + ErrorType.UNDEFINED_RULE_IN_NONLOCAL_REF.code + "): A.g4:5:14: reference to undefined rule S in non-local ref $S::i\n",
		"$a::z",		"error(" + ErrorType.UNKNOWN_RULE_ATTRIBUTE.code + "): A.g4:5:11: unknown attribute z for rule a in $a::z\n",
		"$S",			"error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:5:8: unknown attribute reference S in $S\n",

		"$S::j",		"error(" + ErrorType.UNDEFINED_RULE_IN_NONLOCAL_REF.code + "): A.g4:5:8: reference to undefined rule S in non-local ref $S::j\n",
		"$S::j = 3;",	"error(" + ErrorType.UNDEFINED_RULE_IN_NONLOCAL_REF.code + "): A.g4:5:8: reference to undefined rule S in non-local ref $S::j = 3;\n",
		"$S::j = $S::k;",	"error(" + ErrorType.UNDEFINED_RULE_IN_NONLOCAL_REF.code + "): A.g4:5:8: reference to undefined rule S in non-local ref $S::j = $S::k;\n",
	};

	String[] dynInlineChecks = {
		"$a",			"error(" + ErrorType.ISOLATED_RULE_REF.code + "): A.g4:7:4: missing attribute access on rule reference a in $a\n",
		"$b",			"error(" + ErrorType.ISOLATED_RULE_REF.code + "): A.g4:7:4: missing attribute access on rule reference b in $b\n",
		"$lab",			"error(" + ErrorType.ISOLATED_RULE_REF.code + "): A.g4:7:4: missing attribute access on rule reference lab in $lab\n",
		"$b::f",		"error(" + ErrorType.UNKNOWN_RULE_ATTRIBUTE.code + "): A.g4:7:7: unknown attribute f for rule b in $b::f\n",
		"$S::i",		"error(" + ErrorType.UNDEFINED_RULE_IN_NONLOCAL_REF.code + "): A.g4:7:4: reference to undefined rule S in non-local ref $S::i\n",
		"$S::i=$S::i",	"error(" + ErrorType.UNDEFINED_RULE_IN_NONLOCAL_REF.code + "): A.g4:7:4: reference to undefined rule S in non-local ref $S::i\n" +
						"error(" + ErrorType.UNDEFINED_RULE_IN_NONLOCAL_REF.code + "): A.g4:7:10: reference to undefined rule S in non-local ref $S::i\n",
		"$a::z",		"error(" + ErrorType.UNKNOWN_RULE_ATTRIBUTE.code + "): A.g4:7:7: unknown attribute z for rule a in $a::z\n",

		"$S::j",			"error(" + ErrorType.UNDEFINED_RULE_IN_NONLOCAL_REF.code + "): A.g4:7:4: reference to undefined rule S in non-local ref $S::j\n",
		"$S::j = 3;",		"error(" + ErrorType.UNDEFINED_RULE_IN_NONLOCAL_REF.code + "): A.g4:7:4: reference to undefined rule S in non-local ref $S::j = 3;\n",
		"$S::j = $S::k;",	"error(" + ErrorType.UNDEFINED_RULE_IN_NONLOCAL_REF.code + "): A.g4:7:4: reference to undefined rule S in non-local ref $S::j = $S::k;\n",
		"$Q[-1]::y",        "error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:7:4: unknown attribute reference Q in $Q\n",
		"$Q[-i]::y",        "error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:7:4: unknown attribute reference Q in $Q\n",
		"$Q[i]::y",    		"error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:7:4: unknown attribute reference Q in $Q\n",
		"$Q[0]::y",    		"error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:7:4: unknown attribute reference Q in $Q\n",
		"$Q[-1]::y = 23;",  "error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:7:4: unknown attribute reference Q in $Q\n",
		"$Q[-i]::y = 23;",  "error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:7:4: unknown attribute reference Q in $Q\n",
		"$Q[i]::y = 23;",   "error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:7:4: unknown attribute reference Q in $Q\n",
		"$Q[0]::y = 23;",   "error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:7:4: unknown attribute reference Q in $Q\n",
		"$S[-1]::y",        "error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:7:4: unknown attribute reference S in $S\n",
		"$S[-i]::y",        "error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:7:4: unknown attribute reference S in $S\n",
		"$S[i]::y",     	"error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:7:4: unknown attribute reference S in $S\n",
		"$S[0]::y",     	"error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:7:4: unknown attribute reference S in $S\n",
		"$S[-1]::y = 23;",  "error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:7:4: unknown attribute reference S in $S\n",
		"$S[-i]::y = 23;",  "error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:7:4: unknown attribute reference S in $S\n",
		"$S[i]::y = 23;",   "error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:7:4: unknown attribute reference S in $S\n",
		"$S[0]::y = 23;",   "error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:7:4: unknown attribute reference S in $S\n",
		"$S[$S::y]::i",		"error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:7:4: unknown attribute reference S in $S\n" +
							"error(" + ErrorType.UNDEFINED_RULE_IN_NONLOCAL_REF.code + "): A.g4:7:7: reference to undefined rule S in non-local ref $S::y\n"
	};

	String[] dynFinallyChecks = {
		"$a",			"error(" + ErrorType.ISOLATED_RULE_REF.code + "): A.g4:10:14: missing attribute access on rule reference a in $a\n",
		"$b",			"error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:10:14: unknown attribute reference b in $b\n",
		"$lab",			"error(" + ErrorType.ISOLATED_RULE_REF.code + "): A.g4:10:14: missing attribute access on rule reference lab in $lab\n",
		"$b::f",		"error(" + ErrorType.UNKNOWN_RULE_ATTRIBUTE.code + "): A.g4:10:17: unknown attribute f for rule b in $b::f\n",
		"$S",			"error(" + ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE.code + "): A.g4:10:14: unknown attribute reference S in $S\n",
		"$S::i",		"error(" + ErrorType.UNDEFINED_RULE_IN_NONLOCAL_REF.code + "): A.g4:10:14: reference to undefined rule S in non-local ref $S::i\n",
		"$S::i=$S::i",	"error(" + ErrorType.UNDEFINED_RULE_IN_NONLOCAL_REF.code + "): A.g4:10:14: reference to undefined rule S in non-local ref $S::i\n" +
						"error(" + ErrorType.UNDEFINED_RULE_IN_NONLOCAL_REF.code + "): A.g4:10:20: reference to undefined rule S in non-local ref $S::i\n",
		"$a::z",		"error(" + ErrorType.UNKNOWN_RULE_ATTRIBUTE.code + "): A.g4:10:17: unknown attribute z for rule a in $a::z\n",

		"$S::j",		"error(" + ErrorType.UNDEFINED_RULE_IN_NONLOCAL_REF.code + "): A.g4:10:14: reference to undefined rule S in non-local ref $S::j\n",
		"$S::j = 3;",	"error(" + ErrorType.UNDEFINED_RULE_IN_NONLOCAL_REF.code + "): A.g4:10:14: reference to undefined rule S in non-local ref $S::j = 3;\n",
		"$S::j = $S::k;",	"error(" + ErrorType.UNDEFINED_RULE_IN_NONLOCAL_REF.code + "): A.g4:10:14: reference to undefined rule S in non-local ref $S::j = $S::k;\n",
	};

    @Test public void testMembersActions() throws RecognitionException {
        testActions("members", membersChecks, attributeTemplate);
    }

    @Test public void testDynamicMembersActions() throws RecognitionException {
        testActions("members", dynMembersChecks, attributeTemplate);
    }

    @Test public void testInitActions() throws RecognitionException {
        testActions("init", initChecks, attributeTemplate);
    }

    @Test public void testDynamicInitActions() throws RecognitionException {
        testActions("init", dynInitChecks, attributeTemplate);
    }

	@Test public void testInlineActions() throws RecognitionException {
		testActions("inline", inlineChecks, attributeTemplate);
	}

	@Test public void testDynamicInlineActions() throws RecognitionException {
		testActions("inline", dynInlineChecks, attributeTemplate);
	}

	@Test public void testBadInlineActions() throws RecognitionException {
		testActions("inline", bad_inlineChecks, attributeTemplate);
	}

	@Test public void testFinallyActions() throws RecognitionException {
		testActions("finally", finallyChecks, attributeTemplate);
	}

	@Test public void testDynamicFinallyActions() throws RecognitionException {
		testActions("finally", dynFinallyChecks, attributeTemplate);
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

    public void testActions(String location, String[] pairs, String template) {
        for (int i = 0; i < pairs.length; i+=2) {
            String action = pairs[i];
            String expected = pairs[i+1];
	        STGroup g = new STGroup('<','>');
	        g.setListener(new ErrorBuffer()); // hush warnings
            ST st = new ST(g, template);
            st.add(location, action);
            String grammar = st.render();
            testErrors(new String[] {grammar, expected}, false);
        }
    }
}
