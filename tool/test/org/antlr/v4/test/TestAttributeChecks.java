package org.antlr.v4.test;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.stringtemplate.v4.ST;

/** */
public class TestAttributeChecks extends BaseTest {
    String attributeTemplate =
        "parser grammar A;\n"+
        "@members {<members>}\n" +
        "a[int x] returns [int y]\n" +
        "@init {<init>}\n" +
        "    :   lab=b[34] {\n" +
		"		 <inline>\n" +
		"		 }\n" +
        "    ;\n" +
        "    finally {<finally>}\n" +
        "b[int d] returns [int e]\n" +
        "    :   {<inline2>}\n" +
        "    ;\n" +
        "c   :   ;";

    String scopeTemplate =
        "parser grammar A;\n"+
        "@members {\n" +
        "}\n" +
        "scope S { int i; }\n" +
        "a[int x] returns [int y]\n" +
        "scope { int z; }\n" +
        "scope S;\n" +
        "@init {}\n" +
        "    :   {}\n" +
        "    ;\n" +
        "    finally {}\n" +
        "b[int d] returns [int e]\n" +
        "scope { int f; }\n" +
        "    :   {}\n" +
        "    ;\n" +
        "c   :   ;";

    String[] membersChecks = {
		"$a",			"error(29): A.g:2:11: unknown attribute reference a in $a",
        "$a.y",			"error(29): A.g:2:11: unknown attribute reference a in $a.y",
    };

    String[] initChecks = {
        "$a",			"error(33): A.g:4:8: missing attribute access on rule reference a in $a",
		"$a.q",			"error(31): A.g:4:10: unknown attribute rule q in $a.q",
    };

    String[] inlineChecks = {
        "$q",           "error(29): A.g:6:4: unknown attribute reference q in $q",
        "$q.y",         "error(29): A.g:6:4: unknown attribute reference q in $q.y",
        "$q = 3",       "error(29): A.g:6:4: unknown attribute reference q in $q",
        "$q = 3;",      "error(29): A.g:6:4: unknown attribute reference q in $q = 3;",
        "$q.y = 3;",    "error(29): A.g:6:4: unknown attribute reference q in $q.y = 3;",
        "$q = $blort;", "error(29): A.g:6:4: unknown attribute reference q in $q = $blort;\n" +
						"error(29): A.g:6:9: unknown attribute reference blort in $blort",
        "$a",           "error(33): A.g:6:4: missing attribute access on rule reference a in $a",
        "$a.ick",       "error(31): A.g:6:6: unknown attribute rule ick in $a.ick",
        "$a.ick = 3;",  "error(31): A.g:6:6: unknown attribute rule ick in $a.ick = 3;",
        "$b",           "error(33): A.g:6:4: missing attribute access on rule reference b in $b",
        "$b.d",         "error(30): A.g:6:6: cannot access rule d's parameter: $b.d",  // can't see rule ref's arg
        "$c.text",      "error(29): A.g:6:4: unknown attribute reference c in $c.text", // valid rule, but no ref
		"$lab",			"error(33): A.g:6:4: missing attribute access on rule reference lab in $lab",
		"$lab.d",		"error(31): A.g:6:8: unknown attribute rule d in $lab.d",
    };

	String[] finallyChecks = {
		"$q",           "error(29): A.g:8:14: unknown attribute reference q in $q",
		"$q.y",         "error(29): A.g:8:14: unknown attribute reference q in $q.y",
		"$q = 3",       "error(29): A.g:8:14: unknown attribute reference q in $q",
		"$q = 3;",      "error(29): A.g:8:14: unknown attribute reference q in $q = 3;",
		"$q.y = 3;",    "error(29): A.g:8:14: unknown attribute reference q in $q.y = 3;",
		"$q = $blort;", "error(29): A.g:8:14: unknown attribute reference q in $q = $blort;\n" +
						"error(29): A.g:8:19: unknown attribute reference blort in $blort",
		"$a",           "error(33): A.g:8:14: missing attribute access on rule reference a in $a",
		"$a.ick",       "error(31): A.g:8:16: unknown attribute rule ick in $a.ick",
		"$a.ick = 3;",  "error(31): A.g:8:16: unknown attribute rule ick in $a.ick = 3;",
		"$b",           "error(29): A.g:8:14: unknown attribute reference b in $b",
		"$b.d",         "error(29): A.g:8:14: unknown attribute reference b in $b.d",
		"$c.text",      "error(29): A.g:8:14: unknown attribute reference c in $c.text",
		"$lab",			"error(33): A.g:8:14: missing attribute access on rule reference lab in $lab",
		"$lab.d",		"error(31): A.g:8:18: unknown attribute rule d in $lab.d",
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

	@Test public void testFinallyActions() throws RecognitionException {
		testActions("finally", finallyChecks, attributeTemplate);
	}

    public void testActions(String location, String[] pairs, String template) {
        for (int i = 0; i < pairs.length; i+=2) {
            String action = pairs[i];
            String expected = pairs[i+1];
            ST st = new ST(template);
            st.add(location, action);
            String grammar = st.render();
            testErrors(new String[] {grammar, expected});
        }
    }
}
