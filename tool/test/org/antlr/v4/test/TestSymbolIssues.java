package org.antlr.v4.test;

import org.junit.Test;

/** */
public class TestSymbolIssues extends BaseTest {
    static String[] A = {
        // INPUT
        "grammar A;\n" +
        "options { opt='sss'; k=3; }\n" +
        "\n" +
        "@members {foo}\n" +
        "@members {bar}\n" +
        "@lexer::header {package jj;}\n" +
        "@lexer::header {package kk;}\n" +
        "\n" +
        "a[int i] returns [foo f] : X ID a[3] b[34] q ;\n" +
        "b returns [int g] : Y 'y' 'if' a ;\n" +
        "a : FJKD ;\n" +
        "\n" +
        "ID : 'a'..'z'+ ID ;",
        // YIELDS
        "warning(83): A.g4:2:10: illegal option opt\n" +
        "warning(83): A.g4:2:21: illegal option k\n" +
		"error(94): A.g4:7:1: redefinition of header action\n" +
		"warning(51): A.g4:2:10: illegal option opt\n" +
		"error(19): A.g4:11:0: rule a redefinition\n" +
		"error(60): A.g4:5:1: redefinition of members action\n" +
		"error(47): A.g4:9:37: rule b has no defined parameters\n" +
		"error(24): A.g4:9:43: reference to undefined rule: q\n" +
		"error(46): A.g4:10:31: missing parameter(s) on rule reference: a\n"
    };

    static String[] B = {
        // INPUT
        "parser grammar B;\n" +
        "tokens { X='x'; Y; }\n" +
        "\n" +
        "a : s=ID b+=ID X=ID '.' ;\n" +
        "\n" +
        "b : x=ID x+=ID ;\n" +
        "\n" +
        "s : FOO ;",
        // YIELDS
        "error(59): B.g4:2:9: can't assign string value to token name X in non-combined grammar\n" +
		"error(69): B.g4:4:4: label s conflicts with rule with same name\n" +
		"error(69): B.g4:4:9: label b conflicts with rule with same name\n" +
		"error(70): B.g4:4:15: label X conflicts with token with same name\n" +
		"error(75): B.g4:6:9: label x type mismatch with previous definition: TOKEN_LIST_LABEL!=TOKEN_LABEL\n"
    };

    static String[] D = {
        // INPUT
        "parser grammar D;\n" +
        "a[int j] \n" +
        "        :       i=ID j=ID ;\n" +
        "\n" +
        "b[int i] returns [int i] : ID ;\n" +
        "\n" +
        "c[int i] returns [String k]\n" +
        "        :       ID ;",

        // YIELDS
        "error(72): D.g4:3:21: label j conflicts with rule a's return value or parameter with same name\n" +
		"error(76): D.g4:5:0: rule b's argument i conflicts a return value with same name\n"
    };

	static String[] E = {
		// INPUT
		"grammar E;\n" +
		"tokens {\n" +
		"	A; A;\n" +
		"	B='b'; B;\n" +
		"	C; C='c';\n" +
		"	D='d'; D='d';\n" +
		"	E='e'; X='e';\n" +
		"}\n" +
		"a : A ;\n",

		// YIELDS
		"error(108): E.g4:4:8: cannot redefine B; token name already defined\n" +
		"error(108): E.g4:5:4: cannot redefine C; token name already defined\n" +
		"error(108): E.g4:6:8: cannot redefine D; token name already defined\n" +
		"error(107): E.g4:7:8: cannot alias X='e'; string already assigned to E\n"
	};

    @Test public void testA() { super.testErrors(A, false); }
    @Test public void testB() { super.testErrors(B, false); }
	@Test public void testD() { super.testErrors(D, false); }
	@Test public void testE() { super.testErrors(E, false); }
}
