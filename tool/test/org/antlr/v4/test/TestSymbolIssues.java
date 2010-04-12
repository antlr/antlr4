package org.antlr.v4.test;

import org.junit.Test;

/** */
public class TestSymbolIssues extends BaseTest {
    static String[] A = {
        // INPUT
        "grammar A;\n" +
        "options { opt='sss'; k=3; }\n" +
        "tokens { X; Y='y'; X='x'; Y='q'; Z; Z; }\n" +
        "scope Blort { int x; }\n" +
        "\n" +
        "@members {foo}\n" +
        "@members {bar}\n" +
        "@lexer::header {package jj;}\n" +
        "@lexer::header {package kk;}\n" +
        "\n" +
        "scope Blort { int x; }\n" +
        "\n" +
        "a[int i] returns [foo f] : X ID a[3] b[34] q ;\n" +
        "b returns [int g] : Y 'y' 'if' a ;\n" +
        "a : FJKD ;\n" +
        "\n" +
        "ID : 'a'..'z'+ ID ;",
        // YIELDS
        "error(49): A.g:2:10: illegal option opt\n" +
		"error(59): A.g:11:6: scope Blort redefinition\n" +
		"error(18): A.g:15:0: rule a redefinition\n" +
		"error(58): A.g:7:1: redefinition of members action\n" +
		"error(58): A.g:9:1: redefinition of header action\n" +
		"error(72): A.g:3:19: cannot alias X; token name already defined\n" +
		"error(72): A.g:3:26: cannot alias Y; token name already assigned to 'y'\n" +
		"error(72): A.g:3:36: cannot alias Z; token name already defined\n" +
		"error(46): A.g:13:37: rule b has no defined parameters\n" +
		"error(23): A.g:13:43: reference to undefined rule: q\n" +
		"error(45): A.g:14:31: missing parameter(s) on rule reference: a\n"
    };

    static String[] B = {
        // INPUT
        "parser grammar B;\n" +
        "tokens { X='x'; Y; }\n" +
        "scope s { int i; }\n" +
        "\n" +
        "a : s=ID b+=ID X=ID '.' ;\n" +
        "\n" +
        "b : x=ID x+=ID ;\n" +
        "\n" +
        "s : FOO ;",
        // YIELDS
        "error(25): B.g:2:9: can't assign string value to token name X in non-combined grammar\n" +
		"error(34): B.g:9:0: symbol s conflicts with global dynamic scope with same name\n" +
		"error(35): B.g:5:9: label b conflicts with rule with same name\n" +
		"error(34): B.g:5:4: symbol s conflicts with global dynamic scope with same name\n" +
		"error(36): B.g:5:15: label X conflicts with token with same name\n" +
		"error(41): B.g:7:9: label x type mismatch with previous definition: TOKEN_LIST_LABEL!=TOKEN_LABEL\n"
    };

    static String[] C = {
        // INPUT
        "grammar C;\n"+
        "options {output=AST;}\n"+
        "a : A x=b y+=Z 'hi' -> ID A r $foo $x b $y+ 'hi' 'eh?'\n"+
        "  | ID -> $x A ID  // shouldn't see these refs from other alt ('cept ID)\n"+
        "  ;\n"+
        "b : B ;\n"+
        "A : 'a';",
        // YIELDS
        "error(51): C.g:3:28: reference to rewrite element r not found to left of ->\n" +
		"error(51): C.g:3:30: reference to rewrite element foo not found to left of ->\n" +
		"error(51): C.g:3:49: reference to rewrite element 'eh?' not found to left of ->\n" +
		"error(51): C.g:4:10: reference to rewrite element x not found to left of ->\n"
    };

    static String[] D = {
        // INPUT
        "parser grammar D;\n" +
        "a[int j] \n" +
        "scope { int i; }\n" +
        "        :       i=ID j=ID ;\n" +
        "\n" +
        "b[int i] returns [int i] : ID ;\n" +
        "\n" +
        "c[int i] returns [String k]\n" +
        "scope { int i; int c; int k; }\n" +
        "        :       ID ;",

        // YIELDS
        "error(38): D.g:4:21: label j conflicts with rule a's return value or parameter with same name\n" +
		"error(37): D.g:4:16: label i conflicts with rule a's dynamically-scoped attribute with same name\n" +
		"error(42): D.g:6:0: rule b's argument i conflicts a return value with same name\n" +
		"error(40): D.g:9:6: rule c's dynamically-scoped attribute i conflicts with c's return value or parameter with same name\n" +
		"error(39): D.g:9:6: rule c's dynamically-scoped attribute c conflicts with the rule name\n" +
		"error(40): D.g:9:6: rule c's dynamically-scoped attribute k conflicts with c's return value or parameter with same name\n"
    };

    @Test public void testA() { super.testErrors(A, false); }
    @Test public void testB() { super.testErrors(B, false); }
    @Test public void testC() { super.testErrors(C, false); }
    @Test public void testD() { super.testErrors(D, false); }
}
