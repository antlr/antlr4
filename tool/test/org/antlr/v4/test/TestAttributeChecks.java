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
		"		 c\n" +
        "    ;\n" +
        "    finally {<finally>}\n" +
        "b[int d] returns [int e]\n" +
        "    :   {<inline2>}\n" +
        "    ;\n" +
        "c   :   ;\n" +
		"d	 :   ;\n";

    String scopeTemplate =
        "parser grammar A;\n"+
        "@members {\n" +
		"<members>\n" +
        "}\n" +
        "scope S { int i; }\n" +
        "a[int x] returns [int y]\n" +
        "scope { int z; }\n" +
        "scope S;\n" +
        "@init {<init>}\n" +
        "    :   lab=b[34] {\n" +
		"		 <inline>" +
		"		 }\n" +
        "    ;\n" +
        "    finally {<finally>}\n" +
        "b[int d] returns [int e]\n" +
        "scope { int f; }\n" +
        "    :   {<inline2>}\n" +
        "    ;\n" +
        "c   :   ;";

    String[] membersChecks = {
		"$a",			"error(29): A.g:2:11: unknown attribute reference a in $a",
        "$a.y",			"error(29): A.g:2:11: unknown attribute reference a in $a.y",
    };

    String[] initChecks = {
       	"$a",			"error(33): A.g:4:8: missing attribute access on rule reference a in $a",
		"$c",			"error(29): A.g:4:8: unknown attribute reference c in $c",
		"$a.q",			"error(31): A.g:4:10: unknown attribute q for rule a in $a.q",
    };

    String[] inlineChecks = {
		"$c.text",      "",

		"$a",			"error(33): A.g:6:4: missing attribute access on rule reference a in $a",
		"$b",           "error(33): A.g:6:4: missing attribute access on rule reference b in $b",
		"$lab",			"error(33): A.g:6:4: missing attribute access on rule reference lab in $lab",
		"$c",			"error(33): A.g:6:4: missing attribute access on rule reference c in $c", // no scope
		"$q",           "error(29): A.g:6:4: unknown attribute reference q in $q",
        "$q.y",         "error(29): A.g:6:4: unknown attribute reference q in $q.y",
        "$q = 3",       "error(29): A.g:6:4: unknown attribute reference q in $q",
        "$q = 3;",      "error(29): A.g:6:4: unknown attribute reference q in $q = 3;",
        "$q.y = 3;",    "error(29): A.g:6:4: unknown attribute reference q in $q.y = 3;",
        "$q = $blort;", "error(29): A.g:6:4: unknown attribute reference q in $q = $blort;\n" +
						"error(29): A.g:6:9: unknown attribute reference blort in $blort",
        "$a.ick",       "error(31): A.g:6:6: unknown attribute ick for rule a in $a.ick",
        "$a.ick = 3;",  "error(31): A.g:6:6: unknown attribute ick for rule a in $a.ick = 3;",
        "$b.d",         "error(30): A.g:6:6: cannot access rule d's parameter: $b.d",  // can't see rule ref's arg
		"$d.text",      "error(29): A.g:6:4: unknown attribute reference d in $d.text", // valid rule, but no ref
		"$lab.d",		"error(30): A.g:6:8: cannot access rule d's parameter: $lab.d",
    };

	String[] finallyChecks = {
		"$lab",			"error(33): A.g:9:14: missing attribute access on rule reference lab in $lab",
		"$a",           "error(33): A.g:9:14: missing attribute access on rule reference a in $a",
		"$q",           "error(29): A.g:9:14: unknown attribute reference q in $q",
		"$q.y",         "error(29): A.g:9:14: unknown attribute reference q in $q.y",
		"$q = 3",       "error(29): A.g:9:14: unknown attribute reference q in $q",
		"$q = 3;",      "error(29): A.g:9:14: unknown attribute reference q in $q = 3;",
		"$q.y = 3;",    "error(29): A.g:9:14: unknown attribute reference q in $q.y = 3;",
		"$q = $blort;", "error(29): A.g:9:14: unknown attribute reference q in $q = $blort;\n" +
						"error(29): A.g:9:19: unknown attribute reference blort in $blort",
		"$a.ick",       "error(31): A.g:9:16: unknown attribute ick for rule a in $a.ick",
		"$a.ick = 3;",  "error(31): A.g:9:16: unknown attribute ick for rule a in $a.ick = 3;",
		"$b",           "error(29): A.g:9:14: unknown attribute reference b in $b",
		"$b.d",         "error(29): A.g:9:14: unknown attribute reference b in $b.d",
		"$c.text",      "error(29): A.g:9:14: unknown attribute reference c in $c.text",
		"$lab.d",		"error(30): A.g:9:18: cannot access rule d's parameter: $lab.d",
	};

	String[] dynMembersChecks = {
		"$b::f",		"error(54): A.g:3:1: unknown dynamic scope: b in $b::f",
		"$S::j",		"error(55): A.g:3:4: unknown dynamically-scoped attribute for scope S: j in $S::j",
		"$S::j = 3;",	"error(55): A.g:3:4: unknown dynamically-scoped attribute for scope S: j in $S::j = 3;",
		"$S::j = $S::k;",	"error(55): A.g:3:4: unknown dynamically-scoped attribute for scope S: j in $S::j = $S::k;\n" +
							"error(55): A.g:3:12: unknown dynamically-scoped attribute for scope S: k in $S::k",
	};

	String[] dynInitChecks = {
		"$a",			"",
		"$b",			"",
		"$lab",			"",
		"$b::f",		"",
		"$S::j",		"error(55): A.g:8:11: unknown dynamically-scoped attribute for scope S: j in $S::j",
		"$S::j = 3;",	"error(55): A.g:8:11: unknown dynamically-scoped attribute for scope S: j in $S::j = 3;",
		"$S::j = $S::k;",	"error(55): A.g:8:11: unknown dynamically-scoped attribute for scope S: j in $S::j = $S::k;\n" +
							"error(55): A.g:8:19: unknown dynamically-scoped attribute for scope S: k in $S::k",
	};

	String[] dynInlineChecks = {
		"$a",				"",
		"$b",				"",
		"$lab",				"",
		"$b::f",			"",
		"$S",				"",

		"$S::j",			"error(55): A.g:10:7: unknown dynamically-scoped attribute for scope S: j in $S::j",
		"$S::j = 3;",		"error(55): A.g:10:7: unknown dynamically-scoped attribute for scope S: j in $S::j = 3;",
		"$S::j = $S::k;",	"error(55): A.g:10:7: unknown dynamically-scoped attribute for scope S: j in $S::j = $S::k;\n" +
							"error(55): A.g:10:15: unknown dynamically-scoped attribute for scope S: k in $S::k",
		"$Q[-1]::y",        "error(54): A.g:10:4: unknown dynamic scope: Q in $Q[-1]::y",
		"$Q[-i]::y",        "error(54): A.g:10:4: unknown dynamic scope: Q in $Q[-i]::y",
		"$Q[i]::y",    		"error(54): A.g:10:4: unknown dynamic scope: Q in $Q[i]::y",
		"$Q[0]::y",    		"error(54): A.g:10:4: unknown dynamic scope: Q in $Q[0]::y",
		"$Q[-1]::y = 23;",  "error(54): A.g:10:4: unknown dynamic scope: Q in $Q[-1]::y = 23;",
		"$Q[-i]::y = 23;",  "error(54): A.g:10:4: unknown dynamic scope: Q in $Q[-i]::y = 23;",
		"$Q[i]::y = 23;",   "error(54): A.g:10:4: unknown dynamic scope: Q in $Q[i]::y = 23;",
		"$Q[0]::y = 23;",   "error(54): A.g:10:4: unknown dynamic scope: Q in $Q[0]::y = 23;",
		"$S[-1]::y",        "error(55): A.g:10:11: unknown dynamically-scoped attribute for scope S: y in $S[-1]::y",
		"$S[-i]::y",        "error(55): A.g:10:11: unknown dynamically-scoped attribute for scope S: y in $S[-i]::y",
		"$S[i]::y",     	"error(55): A.g:10:10: unknown dynamically-scoped attribute for scope S: y in $S[i]::y",
		"$S[0]::y",     	"error(55): A.g:10:10: unknown dynamically-scoped attribute for scope S: y in $S[0]::y",
		"$S[-1]::y = 23;",  "error(55): A.g:10:11: unknown dynamically-scoped attribute for scope S: y in $S[-1]::y = 23;",
		"$S[-i]::y = 23;",  "error(55): A.g:10:11: unknown dynamically-scoped attribute for scope S: y in $S[-i]::y = 23;",
		"$S[i]::y = 23;",   "error(55): A.g:10:10: unknown dynamically-scoped attribute for scope S: y in $S[i]::y = 23;",
		"$S[0]::y = 23;",   "error(55): A.g:10:10: unknown dynamically-scoped attribute for scope S: y in $S[0]::y = 23;",
		"$S[$S::y]::i",		"error(55): A.g:10:10: unknown dynamically-scoped attribute for scope S: y in $S::y"
	};

	String[] dynFinallyChecks = {
		"$b::f",		"",
		"$S::j",		"error(55): A.g:12:17: unknown dynamically-scoped attribute for scope S: j in $S::j",
		"$S::j = 3;",	"error(55): A.g:12:17: unknown dynamically-scoped attribute for scope S: j in $S::j = 3;",
		"$S::j = $S::k;",	"error(55): A.g:12:17: unknown dynamically-scoped attribute for scope S: j in $S::j = $S::k;\n" +
							"error(55): A.g:12:25: unknown dynamically-scoped attribute for scope S: k in $S::k",
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

	@Test public void testDynMembersActions() throws RecognitionException {
		testActions("members", dynMembersChecks, scopeTemplate);
	}

	@Test public void testDynInitActions() throws RecognitionException {
		testActions("init", dynInitChecks, scopeTemplate);
	}

	@Test public void testDynInlineActions() throws RecognitionException {
		testActions("inline", dynInlineChecks, scopeTemplate);
	}

	@Test public void testDynFinallyActions() throws RecognitionException {
		testActions("finally", dynFinallyChecks, scopeTemplate);
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
