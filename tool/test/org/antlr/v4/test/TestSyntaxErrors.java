package org.antlr.v4.test;

import org.junit.Test;

public class TestSyntaxErrors extends BaseTest {
    static String[] A = {
        // INPUT
        "grammar A;\n" +
        "",
        // YIELDS
        "error(63): A.g::: grammar A has no rules",

		"A;",
		"error(17): <string>:1:0: 'A'<TOKEN_REF> came as a complete surprise to me",

		"grammar ;",
		"error(17): <string>:1:8: ';'<SEMI> came as a complete surprise to me",

		"grammar A\n" +
		"a : ID ;\n",
		"error(17): <string>:2:0: missing SEMI at 'a'",

		"grammar A;\n" +
		"a : ID ;;\n"+
		"b : B ;",
		"error(17): A.g:2:8: ';'<SEMI> came as a complete surprise to me",

		"grammar A;;\n" +
		"a : ID ;\n",
		"error(17): A;.g:1:10: ';'<SEMI> came as a complete surprise to me",

		"grammar A;\n" +
		"a @init : ID ;\n",
		"error(17): A.g:2:8: missing ACTION at ':' while matching a rule",

		"grammar A;\n" +
		"a  ( A | B ) D ;\n" +
		"b : B ;",
		"error(17): A.g:2:3: missing COLON at '(' while matching a rule",
    };

	@Test public void testA() { super.testErrors(A, true); }

	@Test public void testExtraColon() {
		String[] pair = new String[] {
			"grammar A;\n" +
			"a : : A ;\n" +
			"b : B ;",
			"error(17): A.g:2:4: ':'<COLON> came as a complete surprise to me while matching alternative",
		};
		super.testErrors(pair, true);
	}

	@Test public void testMissingRuleSemi() {
		String[] pair = new String[] {
			"grammar A;\n" +
			"a : A \n" +
			"b : B ;",
			"error(17): A.g:3:0: unterminated rule (missing ';') detected at 'b :' while looking for rule element",
		};
		super.testErrors(pair, true);
	}

	@Test public void testMissingRuleSemi2() {
		String[] pair = new String[] {
			"lexer grammar A;\n" +
			"A : 'a' \n" +
			"B : 'b' ;",
			"error(17): A.g:3:0: unterminated rule (missing ';') detected at 'B :' while looking for rule element",
		};
		super.testErrors(pair, true);
	}

	@Test public void testMissingRuleSemi3() {
		String[] pair = new String[] {
			"grammar A;\n" +
			"a : A \n" +
			"b[int i] returns [int y] : B ;",
			"error(17): A.g:3:9: unterminated rule (missing ';') detected at 'returns int y' while looking for rule element"
		};
		super.testErrors(pair, true);
	}

	@Test public void testMissingRuleSemi4() {
		String[] pair = new String[] {
			"grammar A;\n" +
			"a : b \n" +
			"  catch [Exception e] {...}\n" +
			"b : B ;\n",

			"error(17): A.g:2:4: unterminated rule (missing ';') detected at 'b catch' while looking for rule element"
		};
		super.testErrors(pair, true);
	}

	@Test public void testMissingRuleSemi5() {
		String[] pair = new String[] {
			"grammar A;\n" +
			"a : A \n" +
			"  catch [Exception e] {...}\n",

			"error(17): A.g:2:4: unterminated rule (missing ';') detected at 'A catch' while looking for rule element"
		};
		super.testErrors(pair, true);
	}

	@Test public void testBadRulePrequelStart() {
		String[] pair = new String[] {
			"grammar A;\n" +
			"a @ options {k=1;} : A ;\n" +
			"b : B ;",

			"error(17): A.g:2:4: 'options {' came as a complete surprise to me while looking for an identifier"
		};
		super.testErrors(pair, true);
	}

	@Test public void testBadRulePrequelStart2() {
		String[] pair = new String[] {
			"grammar A;\n" +
			"a } : A ;\n" +
			"b : B ;",

			"error(17): A.g:2:2: '}' came as a complete surprise to me while matching rule preamble"
		};
		super.testErrors(pair, true);
	}

}
